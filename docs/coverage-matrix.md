# QATRA Coverage Matrix

This matrix shows what QATRA currently covers compared with common Selenium and API testing needs.

## Web Coverage

| Area | Status | Notes |
|---|---:|---|
| Browser lifecycle | ✅ | Create, navigate, quit, title, URL |
| Browser navigation | ✅ | Back, forward, refresh, base URL |
| Basic element actions | ✅ | Click, type, clear, submit |
| Advanced element actions | ✅ | Hover, double click, right click, scroll, focus, JS click |
| Selects | ✅ | By text, value, index |
| Checkbox/radio helpers | ✅ | Check, uncheck, selected assertions |
| File upload | ✅ | Standard input upload |
| Smart waits | ✅ | Visible, clickable, present, invisible, text, attribute, page ready |
| Alerts | ✅ | Accept, dismiss, prompt text, assertions |
| Frames | ✅ | Switch by locator/index/name, default content |
| Windows/tabs | ✅ | Open/switch/close helpers |
| Shadow DOM | ✅ | Open shadow root helpers |
| Cookies | ✅ | Add, get, remove, assert |
| Local/session storage | ✅ | Set, get, remove, assert |
| Tables | ✅ | Row/column/header/cell helpers |
| Drag/drop | ✅ | Selenium and HTML5 helpers |
| Page health | ✅ | Link/image checks |
| Downloads | ✅ | Basic file existence/content checks |
| Visual testing | Planned | Future phase |
| Accessibility testing | Planned | Future phase |
| Network/HAR capture | Planned | Future phase |
| Selenium Grid | Planned | Future phase |

## RTL Coverage

| Area | Status |
|---|---:|
| Element direction | ✅ |
| Arabic text detection | ✅ |
| Broken Arabic encoding detection | ✅ |
| Arabic placeholder detection | ✅ |
| Arabic/English digit checks | ✅ |
| Mixed direction warnings | ✅ |
| Alignment warnings | ✅ |
| Full page RTL scanner | ✅ |
| TXT/JSON/HTML report export | ✅ |
| Historical RTL reports | ✅ |
| Baseline comparison | ✅ |
| RTL quality gate | ✅ |

## API Coverage

| Area | Status | Notes |
|---|---:|---|
| GET/POST/PUT/PATCH/DELETE | ✅ | Starter fluent support |
| Headers | ✅ | Single and map-based |
| Bearer token | ✅ | Simple helper |
| Query params | ✅ | Single and map-based |
| Path params | ✅ | Single and map-based |
| JSON body | ✅ | String body helper |
| Status assertions | ✅ | Basic |
| Body assertions | ✅ | Contains |
| Header assertions | ✅ | Basic |
| JSONPath assertions | ✅ | Basic |
| Response time assertions | ✅ | Basic |
| Request/response Allure attachments | ✅ | Configurable |
| Multipart upload | Planned | Future phase |
| Form params | Planned | Future phase |
| Schema validation | Planned | Future phase |
| Reusable request specs | Planned | Future phase |

## Framework Coverage

| Area | Status |
|---|---:|
| TestNG base test | ✅ |
| Allure integration | ✅ |
| Screenshots | ✅ |
| Page source and browser logs | ✅ |
| Page Object Model | ✅ |
| Component Object Model | ✅ |
| Data driven CSV/JSON/Excel | ✅ |
| Retry analyzer | ✅ |
| Parallel helpers | ✅ |
| Stability helper | ✅ |
| Environment profiles | ✅ |
| CI/CD workflows | ✅ |
| Maven Central release | Planned |
