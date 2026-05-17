# QATRA Roadmap

QATRA is being built incrementally. This roadmap shows the current direction.

## Completed Foundation

```text
Phase 1.x  Web framework foundation
Phase 2.x  RTL/Arabic engine expansion
Phase 3.0  API module starter
Phase 3.1  Selenium core coverage completion
Phase 3.2  Selenium assertions and helpers
Phase 3.3  Page Object Model support
Phase 3.4  Advanced Page Object and Component Object support
Phase 3.5  Data driven testing support
Phase 3.6  Retry, parallel execution, and stability
Phase 3.7  Environment profiles and configuration management
Phase 3.8  CI/CD and release readiness
Phase 3.9  Documentation polish and public README
Phase 3.10 Web hardening, RTL regex fix, and parallel-safe RTL config
Phase 3.11  Smart Wait Engine v2 and fluent API consistency
Phase 3.12  QATRA Adaptive Wait Engine
Phase 3.13  Web Component Layer
Phase 3.14  Web Assertion Engine Cleanup
Phase 3.15  Web Assertion Failure Diagnostics
Phase 3.16  Adaptive element actions integration
```

## Next Recommended Phases

### Phase 4.0 — Maven Central Publishing Preparation

- Finalize groupId and artifact IDs
- Add source and javadoc JAR configuration
- Add release profile
- Prepare signing strategy
- Prepare publishing documentation

### Phase 4.1 — API Module Expansion

- Basic Auth
- Form params
- Multipart uploads
- Cookies
- Reusable request specifications
- JSON schema validation
- Request/response export to files

### Phase 4.2 — Web Advanced Utilities

- Download manager
- Browser console assertion improvements
- Full-page screenshots
- Element screenshots
- Network logs where supported
- Visual comparison starter

### Phase 4.3 — Accessibility Testing Starter

- Accessibility scan integration
- Common accessibility assertions
- Accessibility report export

### Phase 4.4 — Dedicated Examples Module

- `qatra-examples`
- Minimal runnable examples
- Training-friendly examples
- CI smoke examples

### Phase 5.0 — qatra-mobile

- Appium driver factory
- Mobile actions
- Mobile assertions
- Android/iOS configuration profiles

### Phase 6.0 — qatra-db

- JDBC connection management
- Query helpers
- Database assertions
- Test data cleanup helpers

## Long-Term Vision

QATRA should become an Arabic-first quality automation platform that supports:

```text
Web
API
Mobile
Database
RTL quality
Evidence-rich reporting
CI/CD quality gates
```

## Phase 3.12 — QATRA Adaptive Wait Engine

Goal: make QATRA waits stronger than raw Selenium waits and more specialized than generic framework synchronization.

Scope:

- Diagnostic-rich FluentWait wrapper.
- Multi-signal readiness model.
- Arabic/RTL readiness conditions.
- Visual stability and overlay checks.
- Component-specific readiness checks.
- Timeout evidence reports.

Status: implemented and followed by Phase 3.16 adaptive element action integration. Full production hardening still requires more real-world web examples.

## Self-Healing Locators

- [Self-Healing Locator Engine](self-healing-locator-engine.md)


### Phase 3.18.4 — Proactive Locator Quality Advisor

Detect fragile locators before failure using score, risk, recommendations, HTML/JSON/TXT reports, and optional quality gates.

## Phase 3.18.7 — IDE-Free Code Patch Strategy

Add a safe, human-reviewed workflow for converting healed locators into maintainable Page Object updates through generated patch artifacts.


## Phase 3.18.8 — Accessibility Tree Locator Healing

Accessibility-aware self-healing locator strategy for Arabic and enterprise web applications.


## Phase 3.18.9 — Arabic Component Self-Healing

Added business-intent based healing for Arabic dropdowns, tables, modals, toast messages, date pickers, and label-based form fields. This extends QATRA self-healing from element-level fallback into component-aware recovery for Arabic/RTL enterprise systems.
