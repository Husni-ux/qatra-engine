# GitHub Issues — IDE-Free Code Patch Strategy

## Issue 1 — Export healing patch suggestions
Generate JSON and Markdown patch suggestions when a locator is healed.

## Issue 2 — Generate unified diff suggestions
Create a `.diff` artifact that can be reviewed manually by automation engineers.

## Issue 3 — Add approval template workflow
Generate `qatra-healing-approval-template.json` to support future human-approved healing.

## Issue 4 — Add source locator scanner
Scan Java Page Object files to locate old locator expressions and map them to reviewable patch targets.

## Issue 5 — Prepare future IDE plugin integration
Design generated artifacts so they can later be consumed by an IntelliJ plugin without changing the core engine.
