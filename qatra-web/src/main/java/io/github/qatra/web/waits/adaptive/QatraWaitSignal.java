package io.github.qatra.web.waits.adaptive;

/**
 * Readiness signal categories collected by the QATRA Adaptive Wait Engine.
 */
public enum QatraWaitSignal {
    DOM,
    JAVASCRIPT,
    NETWORK,
    VISUAL,
    RTL,
    ENCODING,
    COMPONENT,
    DIAGNOSTIC
}
