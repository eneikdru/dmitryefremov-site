#!/bin/bash
git checkout frontend/acceptance-flow.spec.ts frontend/podcast-keyboard-playback.spec.ts frontend/identity-ui.spec.ts
sed -i "s/import { expect/import { setupE2EMocks } from '.\/test-utils';\nimport { expect/g" frontend/acceptance-flow.spec.ts
sed -i "/await page.goto/i \    await setupE2EMocks(page);" frontend/acceptance-flow.spec.ts

sed -i "s/import { expect/import { setupE2EMocks } from '.\/test-utils';\nimport { expect/g" frontend/podcast-keyboard-playback.spec.ts
sed -i "/await page.goto/i \    await setupE2EMocks(page);" frontend/podcast-keyboard-playback.spec.ts

sed -i "s/import { expect/import { setupE2EMocks } from '.\/test-utils';\nimport { expect/g" frontend/identity-ui.spec.ts
sed -i "/await page.goto/i \    await setupE2EMocks(page);" frontend/identity-ui.spec.ts
sed -i "/await pageMobile.goto/i \    await setupE2EMocks(pageMobile);" frontend/identity-ui.spec.ts
