#!/usr/bin/env python3
"""
Verification Script for Automated Asset Encoding Pipeline and Migration V20261009171607316.

Verifies:
1. Migration SQL file V20261009171607316 exists and contains required tables and constraints.
2. Executing encode_assets.py on a studio-quality source image generates WebP and AVIF variants at defined breakpoints (375px, 768px, 1440px).
3. Generated variant images maintain aspect ratio, correct dimensions, and valid image headers.
4. Individual WebP and AVIF delivery variants are significantly smaller than the original high-resolution studio source image.
"""

import json
import math
import os
import subprocess
import sys
import tempfile
from pathlib import Path
from PIL import Image

try:
    import pillow_avif  # noqa: F401
except ImportError:
    pass


def generate_studio_test_image(filepath: Path, width: int = 2560, height: int = 1440):
    """Generates a realistic high-resolution studio photo source image with detailed gradients and noise."""
    img = Image.new("RGB", (width, height))
    pixels = img.load()

    # Create a smooth photographic gradient studio background with subtle radial luminance
    cx, cy = width / 2, height / 2
    max_dist = math.sqrt(cx**2 + cy**2)

    for y in range(0, height, 2):
        for x in range(0, width, 2):
            dist = math.sqrt((x - cx)**2 + (y - cy)**2)
            factor = 1.0 - (dist / max_dist) * 0.6
            r = int(min(255, max(0, 30 * factor + (x % 50))))
            g = int(min(255, max(0, 45 * factor + (y % 50))))
            b = int(min(255, max(0, 90 * factor + ((x + y) % 70))))

            # Fill 2x2 blocks for efficiency during generation
            for dy in range(2):
                if y + dy < height:
                    for dx in range(2):
                        if x + dx < width:
                            pixels[x + dx, y + dy] = (r, g, b)

    img.save(filepath, "PNG", compress_level=1)


def test_migration_file():
    """Validates Flyway migration V20261009171607316 SQL file."""
    migration_path = Path("src/main/resources/db/migration/V20261009171607316__create_media_assets.sql")
    assert migration_path.exists(), f"Migration file missing at {migration_path}"

    sql_content = migration_path.read_text(encoding="utf-8")
    assert "CREATE TABLE IF NOT EXISTS media_assets" in sql_content, "Missing media_assets table creation"
    assert "CREATE TABLE IF NOT EXISTS media_asset_variants" in sql_content, "Missing media_asset_variants table creation"
    assert "fk_media_asset_variants_asset" in sql_content, "Missing foreign key constraint"
    assert "uq_media_asset_variant" in sql_content, "Missing unique constraint on asset_id, format, breakpoint_width"
    print("✓ Flyway migration V20261009171607316 verification passed.")


def test_asset_encoding_pipeline():
    """Tests the automated asset encoding script with high-res source image."""
    with tempfile.TemporaryDirectory() as tmp_dir:
        tmp_path = Path(tmp_dir)
        source_img_path = tmp_path / "studio_portrait.png"
        output_dir = tmp_path / "output_assets"

        # 1. Generate high-res studio source image (2560x1440)
        generate_studio_test_image(source_img_path, width=2560, height=1440)
        orig_file_size = source_img_path.stat().st_size
        assert orig_file_size > 0, "Original image size must be greater than 0"

        # 2. Run encoding script
        cmd = [
            sys.executable,
            "scripts/encode_assets.py",
            "--source", str(source_img_path),
            "--output-dir", str(output_dir),
            "--breakpoints", "375,768,1440",
            "--formats", "webp,avif",
            "--webp-quality", "82",
            "--avif-quality", "75",
        ]

        result = subprocess.run(cmd, capture_output=True, text=True)
        assert result.returncode == 0, f"encode_assets.py failed:\nSTDOUT:\n{result.stdout}\nSTDERR:\n{result.stderr}"

        # 3. Check manifest
        manifest_path = output_dir / "manifest.json"
        assert manifest_path.exists(), "Manifest JSON file was not created"

        with open(manifest_path, "r", encoding="utf-8") as f:
            manifest = json.load(f)

        assert len(manifest["assets"]) == 1, "Expected 1 asset record in manifest"
        asset = manifest["assets"][0]

        expected_breakpoints = [375, 768, 1440]
        expected_formats = ["WEBP", "AVIF"]

        variants = asset["variants"]
        assert len(variants) == len(expected_breakpoints) * len(expected_formats), \
            f"Expected {len(expected_breakpoints) * len(expected_formats)} variants, got {len(variants)}"

        # 4. Verify each variant image file and payload savings
        asset_dir = output_dir / "studio_portrait"

        for bp in expected_breakpoints:
            for fmt in expected_formats:
                matching = [v for v in variants if v["breakpoint_width"] == bp and v["format"] == fmt]
                assert len(matching) == 1, f"Missing variant for bp={bp}, fmt={fmt}"
                var = matching[0]

                var_file = asset_dir / f"studio_portrait_{bp}w.{fmt.lower()}"
                assert var_file.exists(), f"Variant file does not exist: {var_file}"

                var_size = var_file.stat().st_size

                # Each individual delivery variant must be substantially smaller than the source image
                assert var_size < orig_file_size, \
                    f"Variant {fmt} {bp}w ({var_size} bytes) should be smaller than source ({orig_file_size} bytes)"

                # Verify image header and dimensions
                with Image.open(var_file) as var_img:
                    w, h = var_img.size
                    assert w == bp, f"Expected width {bp}, got {w}"
                    expected_h = max(1, int(round(bp * (1440 / 2560))))
                    assert abs(h - expected_h) <= 1, f"Expected height ~{expected_h}, got {h}"

        # 5. Check largest single variant (1440w AVIF / WEBP) reduction compared to source
        largest_variant = max(variants, key=lambda v: v["file_size_bytes"])
        largest_variant_size = largest_variant["file_size_bytes"]
        reduction_vs_largest = ((orig_file_size - largest_variant_size) / orig_file_size) * 100

        print("✓ Asset encoding pipeline test passed!")
        print(f"  Original studio source (2560x1440): {orig_file_size / 1024:.1f} KB")
        print(f"  Largest delivery variant ({largest_variant['format']} {largest_variant['breakpoint_width']}w): {largest_variant_size / 1024:.1f} KB ({reduction_vs_largest:.1f}% savings)")


def main():
    try:
        test_migration_file()
        test_asset_encoding_pipeline()
        print("\nSUCCESS: Automated asset encoding pipeline verification completed successfully.")
    except AssertionError as err:
        print(f"\nFAILURE: {err}", file=sys.stderr)
        sys.exit(1)
    except Exception as ex:
        print(f"\nUNEXPECTED ERROR: {ex}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
