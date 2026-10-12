#!/usr/bin/env python3
"""
Automated Asset Encoding Pipeline Script
----------------------------------------
Encodes and scales studio source images into modern WebP and AVIF formats
at defined breakpoint widths (e.g. 375px, 768px, 1440px).

Usage:
    python3 scripts/encode_assets.py --source <path_to_image_or_dir> --output-dir <path> [options]

Options:
    --source           Path to input image or directory containing images
    --output-dir       Path to target directory for encoded variants (default: frontend/public/assets/images)
    --breakpoints      Comma-separated list of breakpoint widths in px (default: 375,768,1440)
    --formats          Comma-separated list of formats (default: webp,avif)
    --webp-quality     Quality setting for WebP (default: 82)
    --avif-quality     Quality setting for AVIF (default: 75)
    --manifest         Path to write output JSON manifest (default: <output_dir>/manifest.json)
"""

import argparse
import json
import os
import sys
from pathlib import Path
from PIL import Image

# Ensure AVIF and HEIF plugins are registered if available
try:
    import pillow_avif  # noqa: F401
except ImportError:
    pass

try:
    import pillow_heif
    pillow_heif.register_heif_opener()
except ImportError:
    pass

SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp", ".tiff", ".bmp"}


def process_image(
    source_path: Path,
    output_dir: Path,
    breakpoints: list[int],
    formats: list[str],
    webp_quality: int,
    avif_quality: int,
) -> dict:
    """Processes a single source image into scaled WebP and AVIF variants."""
    asset_key = source_path.stem
    asset_out_dir = output_dir / asset_key
    asset_out_dir.mkdir(parents=True, exist_ok=True)

    with Image.open(source_path) as img:
        # Convert paletted / RGBA / grayscale to RGBA or RGB as appropriate
        if img.mode not in ("RGB", "RGBA"):
            img = img.convert("RGBA" if "A" in img.mode or "transparency" in img.info else "RGB")

        orig_w, orig_h = img.size
        orig_size = source_path.stat().st_size

        asset_record = {
            "asset_key": asset_key,
            "source_path": str(source_path),
            "original_width": orig_w,
            "original_height": orig_h,
            "original_size_bytes": orig_size,
            "variants": [],
        }

        total_variant_size = 0

        for bp in sorted(breakpoints):
            # Do not upscale if source is smaller than breakpoint, but ensure target width matches breakpoint or scaled
            target_w = min(bp, orig_w) if orig_w >= bp else bp
            aspect_ratio = orig_h / orig_w
            target_h = max(1, int(round(target_w * aspect_ratio)))

            # Resize using Lanczos resampling for high visual sharpness
            resample = getattr(Image, "Resampling", Image).LANCZOS
            resized_img = img.resize((target_w, target_h), resample=resample)

            for fmt in formats:
                fmt_lower = fmt.lower()
                out_filename = f"{asset_key}_{target_w}w.{fmt_lower}"
                out_path = asset_out_dir / out_filename

                save_kwargs = {}
                if fmt_lower == "webp":
                    save_kwargs = {"format": "WEBP", "quality": webp_quality, "method": 6}
                elif fmt_lower == "avif":
                    save_kwargs = {"format": "AVIF", "quality": avif_quality}
                elif fmt_lower in ("jpg", "jpeg"):
                    save_kwargs = {"format": "JPEG", "quality": 85, "optimize": True}
                elif fmt_lower == "png":
                    save_kwargs = {"format": "PNG", "optimize": True}
                else:
                    save_kwargs = {"format": fmt.upper()}

                # Convert RGBA to RGB for JPEG if needed
                save_img = resized_img
                if fmt_lower in ("jpg", "jpeg") and save_img.mode == "RGBA":
                    save_img = save_img.convert("RGB")

                save_img.save(out_path, **save_kwargs)
                variant_size = out_path.stat().st_size
                total_variant_size += variant_size

                rel_path = str(out_path.relative_to(output_dir.parent) if output_dir.parent in out_path.parents else out_path)

                asset_record["variants"].append({
                    "format": fmt_lower.upper(),
                    "breakpoint_width": bp,
                    "width": target_w,
                    "height": target_h,
                    "file_path": rel_path,
                    "file_size_bytes": variant_size,
                })

        reduction_pct = ((orig_size - total_variant_size) / orig_size) * 100 if orig_size > 0 else 0
        asset_record["total_variants_size_bytes"] = total_variant_size
        asset_record["payload_reduction_percent"] = round(reduction_pct, 2)

        return asset_record


def main():
    parser = argparse.ArgumentParser(description="Automated Asset Encoding Pipeline")
    parser.add_argument("--source", required=True, help="Path to input image file or directory")
    parser.add_argument("--output-dir", default="frontend/public/assets/images", help="Output directory")
    parser.add_argument("--breakpoints", default="375,768,1440", help="Comma-separated breakpoint widths")
    parser.add_argument("--formats", default="webp,avif", help="Comma-separated target formats")
    parser.add_argument("--webp-quality", type=int, default=82, help="WebP compression quality (0-100)")
    parser.add_argument("--avif-quality", type=int, default=75, help="AVIF compression quality (0-100)")
    parser.add_argument("--manifest", help="Manifest output JSON file path")

    args = parser.parse_args()

    source_path = Path(args.source)
    if not source_path.exists():
        print(f"Error: Source path '{source_path}' does not exist.", file=sys.stderr)
        sys.exit(1)

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    breakpoints = [int(bp.strip()) for bp in args.breakpoints.split(",") if bp.strip()]
    formats = [fmt.strip().lower() for fmt in args.formats.split(",") if fmt.strip()]

    source_files = []
    if source_path.is_file():
        if source_path.suffix.lower() in SUPPORTED_EXTENSIONS:
            source_files.append(source_path)
    elif source_path.is_dir():
        for root, _, files in os.walk(source_path):
            for file in files:
                f_path = Path(root) / file
                if f_path.suffix.lower() in SUPPORTED_EXTENSIONS:
                    source_files.append(f_path)

    if not source_files:
        print(f"No supported image files found in '{source_path}'.", file=sys.stderr)
        sys.exit(1)

    manifest_data = {
        "pipeline": "Automated Asset Encoding Pipeline",
        "breakpoints": breakpoints,
        "formats": [f.upper() for f in formats],
        "assets": [],
    }

    print(f"Processing {len(source_files)} image(s)... Breakpoints: {breakpoints}, Formats: {formats}")

    for src_file in source_files:
        record = process_image(
            source_path=src_file,
            output_dir=output_dir,
            breakpoints=breakpoints,
            formats=formats,
            webp_quality=args.webp_quality,
            avif_quality=args.avif_quality,
        )
        manifest_data["assets"].append(record)
        print(
            f"Encoded '{src_file.name}' -> {len(record['variants'])} variants. "
            f"Original: {record['original_size_bytes']} bytes | "
            f"Variants total: {record['total_variants_size_bytes']} bytes "
            f"({record['payload_reduction_percent']}% reduction)"
        )

    manifest_path = Path(args.manifest) if args.manifest else output_dir / "manifest.json"
    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(manifest_data, f, indent=2)

    print(f"Manifest written to: {manifest_path}")


if __name__ == "__main__":
    main()
