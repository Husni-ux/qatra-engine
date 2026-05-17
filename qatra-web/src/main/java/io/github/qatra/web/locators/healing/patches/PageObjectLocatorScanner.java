package io.github.qatra.web.locators.healing.patches;

import io.github.qatra.web.locators.healing.reports.HealingPatchSuggestion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight source scanner for mapping healed locator suggestions to Page Object fields.
 *
 * <p>This intentionally avoids modifying code and does not require an IDE plugin. It searches source
 * files for the old locator expression and returns a human-reviewable target location.</p>
 */
public final class PageObjectLocatorScanner {
    private PageObjectLocatorScanner() {}

    public static Optional<HealingPatchTarget> findTarget(Path sourceRoot, HealingPatchSuggestion suggestion) {
        if (sourceRoot == null || suggestion == null || !Files.exists(sourceRoot)) {
            return Optional.empty();
        }
        String oldExpression = HealingPatchSuggestion.toJavaBy(suggestion.oldLocator());
        try (var stream = Files.walk(sourceRoot)) {
            return stream.filter(path -> path.getFileName().toString().endsWith(".java"))
                    .map(path -> inspectFile(path, oldExpression))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private static Optional<HealingPatchTarget> inspectFile(Path file, String oldExpression) {
        try {
            var lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(oldExpression)) {
                    return Optional.of(HealingPatchTarget.of(file, i + 1, extractFieldName(line), oldExpression));
                }
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static String extractFieldName(String line) {
        Pattern pattern = Pattern.compile("(?:private|protected|public)?\\s*(?:static\\s+)?(?:final\\s+)?By\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
