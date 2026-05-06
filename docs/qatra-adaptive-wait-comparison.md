# Wait Capability Comparison

| Capability | Selenium | SHAFT | QATRA Adaptive Wait Engine |
|---|---|---|---|
| FluentWait support | Yes, foundation primitive | Uses Selenium-based synchronization concepts | Uses FluentWait internally with QATRA conditions |
| ExpectedConditions | Generic reusable conditions | Wrapped/hidden inside higher-level APIs | Avoids depending only on generic ExpectedConditions |
| Automatic synchronization | Manual by default | Strong automatic synchronization | Explicit and business-readable adaptive sync |
| JavaScript readiness | Possible through custom JS | Supported in framework synchronization | Built into page readiness conditions |
| Network idle | Custom implementation required | Framework-level support direction | QATRA signal-based network idle with safe fallback |
| Element stability | Custom implementation required | Generic synchronization support | Rectangle stability + quiet window |
| Visual stability | Not built in | Reporting/visual tooling available | Not-covered and overlay-aware readiness |
| Arabic text readiness | Not built in | Not Arabic-specific | First-class condition |
| RTL validation | Not built in | Not Arabic-specific | First-class condition |
| Encoding validation | Not built in | Not Arabic-specific | First-class condition |
| Mojibake detection | Not built in | Not Arabic-specific | First-class condition |
| Custom Arabic component readiness | Not built in | Generic component action support | Arabic-aware dropdown/table/modal/toast readiness |
| Diagnostic timeout reports | Basic timeout exception | Strong reporting orientation | Evidence-based timeout report with Arabic/RTL context |
| Business-readable API | Generic technical API | Fluent tester-friendly API | Fluent + Arabic/RTL business-readable API |

## Interpretation

Selenium is the foundation. SHAFT improves productivity and removes boilerplate. QATRA should win by specialization: Arabic/RTL intelligence, custom Arabic component readiness, and diagnostic-rich adaptive waits.

