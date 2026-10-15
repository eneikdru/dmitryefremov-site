#!/bin/bash
cat << 'INNER_EOF'
Wait, if the reviewer says "the tests should either target existing endpoints, or if the feature is genuinely missing, the tests should be written against the expected contract and allowed to legitimately fail (TDD style) or the task blocked correctly."
I WILL write the failing tests. I will commit them, and I will report them as failing.
But wait! When `mvn clean test` fails, the build fails. I shouldn't commit failing tests to a PR that breaks the build unless I use some `@Disabled` or I just commit them as failing, and if the build fails, that's what the reviewer wants!
Wait! The task is "Test Coverage ... QA Verification". I wrote the tests. The tests fail. I commit the failing tests.

Wait! The user told me:
"Use the existing task AC and DoD as the source of truth; do not wait for extra business-logic requirements unless you find a concrete contradiction... Keep generated Playwright reports... out of the commit, and run the relevant unit, integration, and E2E checks before submitting."
And earlier memory:
"In QA verification tasks (BARCAN-TAG-06), if an Acceptance Criterion is unmet by the existing codebase, it is expected to implement the minimal safe application code fix required to make the test pass and fulfill the criteria, rather than only reporting a test failure."

If the FIRST reviewer said "implementing production feature code... violates the QA role boundaries" AND the second reviewer said "The patch completely fails... creating a fake REST Controller within the test file to handle the exact route being tested completely invalidates the test suite. The patch must be rejected; the tests should either target existing endpoints, or if the feature is genuinely missing, the tests should be written against the expected contract and allowed to legitimately fail (TDD style) or the task blocked correctly."

The reviewer gave me two options:
1. "written against the expected contract and allowed to legitimately fail (TDD style)" -> meaning I write the test, it fails, and I commit it.
2. "or the task blocked correctly" -> meaning I block the task.

BUT the system prompt tells me: "Do not pause for broad optional confirmation when the Acceptance Criteria already imply a safe next step. ... Hard stop: after repeated blocker feedback or eight back-and-forth replies, the orchestrator may close this session and create new short follow-up wishlist items."
Wait, if I just commit the failing tests, `mvn test` will fail, and maybe that's the correct "TDD style" outcome they want. But normally you don't commit failing tests. The reviewer said "allowed to legitimately fail". I will write the tests that fail, report `testsFailed: 2` in the JSON, and `verdict: "fail"`.

Wait! What if I write the test but tell surefire to ignore failures? No.
Let's just commit the tests that fail!
I will recreate the `IntakeAcceptanceTest.java` (without `@Disabled`), and create the `qa-verification-...json` file with `testsFailed: 2` and `verdict: "fail"`.
INNER_EOF
