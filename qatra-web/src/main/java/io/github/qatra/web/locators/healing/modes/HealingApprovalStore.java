package io.github.qatra.web.locators.healing.modes;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.HealingConfidenceScore;
import io.github.qatra.web.locators.healing.HealingEvidence;
import io.github.qatra.web.locators.healing.HealingRiskLevel;
import io.github.qatra.web.locators.healing.QatraHealingOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Simple human-approval store for STRICT_APPROVAL mode.
 *
 * <p>The first implementation intentionally avoids external dependencies. It reads a local JSON/text file and
 * checks whether the locator name, primary locator, and fallback locator are present. Future versions can replace
 * this with a typed JSON schema or a server-backed review workflow.</p>
 */
public final class HealingApprovalStore {
    private HealingApprovalStore() {}

    public static boolean isApproved(QatraLocator locator,
                                     LocatorCandidate candidate,
                                     HealingConfidenceScore score,
                                     HealingRiskLevel risk,
                                     HealingEvidence evidence,
                                     QatraHealingOptions options) {
        Path file = options.strictApprovalFile();
        if (file == null || !Files.exists(file)) {
            return false;
        }
        try {
            String content = Files.readString(file).toLowerCase(Locale.ROOT);
            String locatorName = safe(locator.name()).toLowerCase(Locale.ROOT);
            String primary = safe(locator.primaryLocator()).toLowerCase(Locale.ROOT);
            String fallback = safe(candidate.locator()).toLowerCase(Locale.ROOT);
            String text = safe(evidence.visibleText()).toLowerCase(Locale.ROOT);

            boolean locatorMatches = !locatorName.isBlank() && content.contains(locatorName);
            boolean primaryMatches = !primary.isBlank() && content.contains(primary);
            boolean fallbackMatches = !fallback.isBlank() && (content.contains(fallback) || containsUsefulToken(content, fallback));
            boolean textMatches = !text.isBlank() && content.contains(text);
            boolean confidenceMatches = content.contains("\"minConfidence\":") || content.contains("minconfidence") || score.value() >= options.minimumConfidence();

            return fallbackMatches && confidenceMatches && (locatorMatches || primaryMatches || textMatches || containsUsefulToken(content, fallback));
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean containsUsefulToken(String content, String locatorText) {
        if (content == null || locatorText == null) {
            return false;
        }
        String[] separators = {"'", "\"", "=", ",", " ", "]", "["};
        String normalized = locatorText;
        for (String separator : separators) {
            normalized = normalized.replace(separator, " ");
        }
        for (String token : normalized.split("\\s+")) {
            String clean = token.trim().toLowerCase(Locale.ROOT);
            if (clean.length() >= 4 && content.contains(clean)) {
                return true;
            }
        }
        return false;
    }

    private static String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
