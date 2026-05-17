package io.github.qatra.web.locators.healing.modes;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.HealingDecision;
import io.github.qatra.web.locators.healing.QatraHealingOptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Writes a small audit trail for mode decisions so teams can review self-healing behavior. */
public final class HealingModeAuditExporter {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private HealingModeAuditExporter() {}

    public static void export(QatraLocator locator, LocatorCandidate candidate, HealingDecision decision, QatraHealingOptions options) {
        if (options == null || !options.exportModeAudit()) {
            return;
        }
        try {
            Path dir = Path.of("target", "qatra-reports", "healing", "modes");
            Files.createDirectories(dir.resolve("history"));
            String json = json(locator, candidate, decision, options);
            Files.writeString(dir.resolve("healing-mode-audit-latest.json"), json, StandardCharsets.UTF_8);
            Files.writeString(dir.resolve("history").resolve("healing-mode-audit_" + TS.format(LocalDateTime.now()) + ".json"), json, StandardCharsets.UTF_8);
            Files.writeString(dir.resolve("healing-mode-audit-latest.txt"), txt(locator, candidate, decision, options), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Reporting must never break test execution.
        }
    }

    private static String json(QatraLocator locator, LocatorCandidate candidate, HealingDecision decision, QatraHealingOptions options) {
        return "{\n" +
                "  \"generatedAt\": \"" + LocalDateTime.now() + "\",\n" +
                "  \"mode\": \"" + options.mode() + "\",\n" +
                "  \"locatorName\": \"" + esc(locator.name()) + "\",\n" +
                "  \"primaryLocator\": \"" + esc(locator.primaryLocator()) + "\",\n" +
                "  \"candidateSource\": \"" + esc(candidate.source()) + "\",\n" +
                "  \"candidateLocator\": \"" + esc(candidate.locator()) + "\",\n" +
                "  \"status\": \"" + decision.status() + "\",\n" +
                "  \"confidence\": " + decision.confidence().value() + ",\n" +
                "  \"risk\": \"" + decision.riskLevel() + "\",\n" +
                "  \"reason\": \"" + esc(decision.reason()) + "\",\n" +
                "  \"guardrails\": {\n" +
                "    \"blockHidden\": " + options.blockHiddenCandidates() + ",\n" +
                "    \"blockDisabled\": " + options.blockDisabledCandidates() + ",\n" +
                "    \"failOnAmbiguous\": " + options.failOnAmbiguousCandidates() + ",\n" +
                "    \"maxMatches\": " + options.maximumMatchesForAutoHeal() + "\n" +
                "  }\n" +
                "}\n";
    }

    private static String txt(QatraLocator locator, LocatorCandidate candidate, HealingDecision decision, QatraHealingOptions options) {
        return "QATRA Healing Mode Audit\n" +
                "Generated: " + LocalDateTime.now() + "\n" +
                "Mode: " + options.mode() + "\n" +
                "Locator: " + locator.name() + "\n" +
                "Primary: " + locator.primaryLocator() + "\n" +
                "Candidate: " + candidate.source() + " -> " + candidate.locator() + "\n" +
                "Status: " + decision.status() + "\n" +
                "Confidence: " + decision.confidence().value() + "%\n" +
                "Risk: " + decision.riskLevel() + "\n" +
                "Reason: " + decision.reason() + "\n";
    }

    private static String esc(Object value) {
        return value == null ? "" : String.valueOf(value).replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
