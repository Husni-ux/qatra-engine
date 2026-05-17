# GitHub Issues — Accessibility Tree Locator Healing

## Issue: Add Accessibility Tree Locator Healing

Implement accessibility-aware locator recovery using role, aria-label, aria-labelledby, labels, placeholder, title, alt, and inferred native roles.

## Issue: Add Accessible Name Resolver

Create a utility that resolves practical accessible names for Selenium WebElements using ARIA and associated label logic.

## Issue: Add Role + Accessible Name Fallbacks to QatraLocator

Add builder methods:

- `expectedAccessibleName()`
- `fallbackAccessibleName()`
- `fallbackRole()`
- `fallbackRoleAndAccessibleName()`
- `fallbackPlaceholder()`
- `fallbackLabelText()`

## Issue: Improve Healing Confidence with Accessibility Signals

Increase healing confidence when role and accessible-name signals match expected semantic hints.

## Issue: Add Accessibility Locator Healing Tests

Add TestNG tests for role/name fallback, aria-labelledby, label-based inputs, and placeholder-based inputs.
