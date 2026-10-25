#!/bin/bash
TEST_FILES="frontend/acceptance-flow.spec.ts frontend/podcast-keyboard-playback.spec.ts frontend/identity-ui.spec.ts"

for file in $TEST_FILES; do
  if [ -f "$file" ]; then
    sed -i "s/import { test, expect } from '@playwright\/test';/import { test, expect } from '@playwright\/test';\nimport { setupE2EMocks } from '.\/src\/lib\/e2e-mocks';/g" "$file"
    sed -i "/await page.goto/i \    await setupE2EMocks(page);" "$file"
    sed -i "/await pageMobile.goto/i \    await setupE2EMocks(pageMobile);" "$file"
  fi
done
