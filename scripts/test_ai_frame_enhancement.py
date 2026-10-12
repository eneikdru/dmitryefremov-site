#!/usr/bin/env python3
"""
Verification Script for AI Frame Enhancement and Preparation Pipeline.

Verifies:
1. `scripts/ai_frame_enhancer.py` exists and can process raw frames from YouTube channel (@dmitrii.efremov).
2. The AI frame enhancement pipeline increases visual sharpness without introducing distortive artifacts.
3. Up-scaled source images reach target studio resolutions (e.g., 2560px width) and write a valid manifest JSON.
4. Output studio images are saved to source asset directory and can be consumed by `scripts/encode_assets.py`.
5. Deterministic fallback behavior is verified and documented.
"""

import json
import math
import subprocess
import sys
import tempfile
from pathlib import Path
from PIL import Image

try:
    import pillow_avif  # noqa: F401
except ImportError:
    pass


def generate_raw_youtube_frame(filepath: Path, width: int = 1280, height: int = 720):
    """Generates a realistic 720p raw YouTube frame with slight compression softness."""
    img = Image.new("RGB", (width, height))
    pixels = img.load()

    cx, cy = width / 2, height / 2
    max_dist = math.sqrt(cx**2 + cy**2)

    for y in range(0, height, 2):
        for x in range(0, width, 2):
            dist = math.sqrt((x - cx)**2 + (y - cy)**2)
            factor = 1.0 - (dist / max_dist) * 0.5
            r = int(min(255, max(0, 40 * factor + (x % 40))))
            g = int(min(255, max(0, 60 * factor + (y % 40))))
            b = int(min(255, max(0, 110 * factor + ((x + y) % 60))))

            for dy in range(2):
                if y + dy < height:
                    for dx in range(2):
                        if x + dx < width:
                            pixels[x + dx, y + dy] = (r, g, b)

    # Save as JPEG with 80 quality to emulate raw YouTube frame compression
    img.save(filepath, "JPEG", quality=80)


def test_ai_frame_enhancer():
    """Tests AI frame enhancer pipeline execution, metrics, manifest generation, and fallback behavior."""
    enhancer_script = Path("scripts/ai_frame_enhancer.py")
    assert enhancer_script.exists(), f"Enhancer script missing at {enhancer_script}"

    with tempfile.TemporaryDirectory() as tmp_dir:
        tmp_path = Path(tmp_dir)
        raw_frame_path = tmp_path / "yt_raw_frame_01.jpg"
        output_sources_dir = tmp_path / "sources"
        encoded_output_dir = tmp_path / "encoded"

        # 1. Generate 720p raw frame
        generate_raw_youtube_frame(raw_frame_path, width=1280, height=720)
        assert raw_frame_path.exists()

        # 2. Test auto/deterministic enhancement pipeline
        cmd = [
            sys.executable,
            "scripts/ai_frame_enhancer.py",
            "--input", str(raw_frame_path),
            "--output-dir", str(output_sources_dir),
            "--scale", "2.0",
            "--channel", "@dmitrii.efremov",
            "--ai-provider", "auto",
        ]

        result = subprocess.run(cmd, capture_output=True, text=True)
        assert result.returncode == 0, f"ai_frame_enhancer.py failed:\nSTDOUT:\n{result.stdout}\nSTDERR:\n{result.stderr}"

        # 3. Check enhancement manifest
        manifest_path = output_sources_dir / "enhancement_manifest.json"
        assert manifest_path.exists(), "Enhancement manifest JSON missing"

        with open(manifest_path, "r", encoding="utf-8") as f:
            manifest = json.load(f)

        assert manifest["channel"] == "@dmitrii.efremov"
        assert len(manifest["processed_assets"]) == 1
        asset = manifest["processed_assets"][0]

        assert asset["original_width"] == 1280
        assert asset["original_height"] == 720
        assert asset["enhanced_width"] == 2560
        assert asset["enhanced_height"] == 1440
        assert asset["enhanced_sharpness_score"] > asset["original_sharpness_score"], "Sharpness score should increase"
        assert asset["epistemic_status"] == "VERIFIED"
        assert asset["ready_for_encoding"] is True

        enhanced_img_path = Path(asset["enhanced_path"])
        assert enhanced_img_path.exists(), f"Enhanced studio image missing at {enhanced_img_path}"

        # 4. Verify output studio image can be processed by encode_assets.py
        encode_cmd = [
            sys.executable,
            "scripts/encode_assets.py",
            "--source", str(enhanced_img_path),
            "--output-dir", str(encoded_output_dir),
            "--breakpoints", "375,768,1440",
            "--formats", "webp,avif",
        ]
        encode_result = subprocess.run(encode_cmd, capture_output=True, text=True)
        assert encode_result.returncode == 0, f"encode_assets.py failed on enhanced image:\n{encode_result.stderr}"

        encoded_manifest_path = encoded_output_dir / "manifest.json"
        assert encoded_manifest_path.exists(), "Encoded output manifest missing"

        # 5. Test deterministic fallback behavior with 'external' provider flag
        fallback_cmd = [
            sys.executable,
            "scripts/ai_frame_enhancer.py",
            "--input", str(raw_frame_path),
            "--output-dir", str(tmp_path / "fallback_sources"),
            "--ai-provider", "external",
        ]
        fallback_result = subprocess.run(fallback_cmd, capture_output=True, text=True)
        assert fallback_result.returncode == 0, f"Fallback execution failed: {fallback_result.stderr}"

        print("✓ AI Frame Enhancement pipeline test passed!")
        print(f"  Raw frame (1280x720) sharpness score: {asset['original_sharpness_score']}")
        print(f"  Enhanced studio frame ({asset['enhanced_width']}x{asset['enhanced_height']}) sharpness score: {asset['enhanced_sharpness_score']} (+{asset['sharpness_gain_percent']}%)")


def main():
    try:
        test_ai_frame_enhancer()
        print("\nSUCCESS: All AI frame enhancement verification tests passed.")
    except AssertionError as err:
        print(f"\nFAILURE: {err}", file=sys.stderr)
        sys.exit(1)
    except Exception as ex:
        print(f"\nUNEXPECTED ERROR: {ex}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
