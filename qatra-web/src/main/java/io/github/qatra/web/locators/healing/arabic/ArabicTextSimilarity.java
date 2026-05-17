package io.github.qatra.web.locators.healing.arabic;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Arabic-aware text normalization and similarity helpers used by QATRA healing.
 *
 * <p>The goal is not NLP magic. It is deterministic, explainable matching for
 * common Arabic UI labels where small differences such as diacritics, tatweel,
 * alef/hamza forms, and optional words should not break locator healing.</p>
 */
public final class ArabicTextSimilarity {

    private ArabicTextSimilarity() {}

    public static String normalizeArabic(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replace('\u0640', ' ') // tatweel
                .replaceAll("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]", "")
                .replace('أ', 'ا')
                .replace('إ', 'ا')
                .replace('آ', 'ا')
                .replace('ٱ', 'ا')
                .replace('ى', 'ي')
                .replace('ئ', 'ي')
                .replace('ؤ', 'و')
                .replace('ة', 'ه')
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
        return normalized;
    }

    public static boolean containsArabic(String value) {
        return value != null && value.matches(".*[\\u0600-\\u06FF].*");
    }

    public static boolean semanticContains(String actual, String expected) {
        String a = normalizeArabic(actual);
        String e = normalizeArabic(expected);
        if (a.isBlank() || e.isBlank()) {
            return false;
        }
        return a.contains(e) || e.contains(a) || tokenOverlapPercent(a, e) >= 70 || levenshteinSimilarity(a, e) >= 82;
    }

    public static int similarityPercent(String first, String second) {
        String a = normalizeArabic(first);
        String b = normalizeArabic(second);
        if (a.isBlank() || b.isBlank()) {
            return 0;
        }
        if (a.equals(b)) {
            return 100;
        }
        if (a.contains(b) || b.contains(a)) {
            return 92;
        }
        return Math.max(tokenOverlapPercent(a, b), levenshteinSimilarity(a, b));
    }

    public static int tokenOverlapPercent(String first, String second) {
        Set<String> a = tokens(first);
        Set<String> b = tokens(second);
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        int overlap = 0;
        for (String token : a) {
            if (b.contains(token)) overlap++;
        }
        int denominator = Math.min(a.size(), b.size());
        return (int) Math.round((overlap * 100.0) / denominator);
    }

    private static Set<String> tokens(String value) {
        Set<String> tokens = new LinkedHashSet<>();
        Arrays.stream(normalizeArabic(value).split(" "))
                .map(String::trim)
                .filter(token -> token.length() > 1)
                .forEach(tokens::add);
        return tokens;
    }

    private static int levenshteinSimilarity(String first, String second) {
        int distance = levenshtein(first, second);
        int max = Math.max(first.length(), second.length());
        if (max == 0) return 100;
        return (int) Math.round((1.0 - (distance / (double) max)) * 100.0);
    }

    private static int levenshtein(String first, String second) {
        int[] costs = new int[second.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= first.length(); i++) {
            costs[0] = i;
            int northwest = i - 1;
            for (int j = 1; j <= second.length(); j++) {
                int cost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                int newValue = Math.min(Math.min(costs[j] + 1, costs[j - 1] + 1), northwest + cost);
                northwest = costs[j];
                costs[j] = newValue;
            }
        }
        return costs[second.length()];
    }
}
