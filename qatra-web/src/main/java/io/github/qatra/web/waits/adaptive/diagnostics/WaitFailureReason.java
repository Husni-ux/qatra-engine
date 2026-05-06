package io.github.qatra.web.waits.adaptive.diagnostics;

/**
 * Normalized timeout failure categories useful for reports and future quality gates.
 */
public enum WaitFailureReason {
    ELEMENT_NOT_FOUND,
    ELEMENT_NOT_VISIBLE,
    ELEMENT_NOT_ENABLED,
    ELEMENT_STALE,
    ELEMENT_MOVING,
    ELEMENT_COVERED,
    PAGE_NOT_READY,
    NETWORK_NOT_IDLE,
    ARABIC_TEXT_MISSING,
    BROKEN_ARABIC,
    MOJIBAKE_DETECTED,
    RTL_NOT_APPLIED,
    COMPONENT_NOT_READY,
    UNKNOWN
}
