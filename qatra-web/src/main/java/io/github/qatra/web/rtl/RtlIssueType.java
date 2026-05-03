package io.github.qatra.web.rtl;

/**
 * Categories used by the QATRA RTL scanner.
 *
 * <p>Issue type is different from severity. Severity tells how serious the issue is;
 * type tells what kind of Arabic/RTL quality risk was detected.</p>
 */
public enum RtlIssueType {
    DIRECTION,
    ENCODING,
    PLACEHOLDER,
    DIGITS,
    MIXED_DIRECTION,
    ALIGNMENT,
    SCAN
}
