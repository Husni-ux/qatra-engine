package io.github.qatra.web.locators.healing.arabic;

import java.util.List;

/** Arabic semantic matching facade used by healing evidence and locator builders. */
public final class ArabicSemanticMatcher {

    private ArabicSemanticMatcher() {}

    public static boolean matchesAction(String expectedAction, String actualText) {
        return ArabicActionDictionary.matchesAction(expectedAction, actualText);
    }

    public static boolean matchesExpectedArabicText(String expectedArabicText, String actualText) {
        return ArabicTextSimilarity.semanticContains(actualText, expectedArabicText);
    }

    public static int bestActionSimilarity(String expectedAction, String actualText) {
        int best = 0;
        for (String synonym : ArabicActionDictionary.synonyms(expectedAction)) {
            best = Math.max(best, ArabicTextSimilarity.similarityPercent(actualText, synonym));
        }
        return best;
    }

    public static String bestMatchingActionLabel(String expectedAction, String actualText) {
        String bestLabel = "";
        int bestScore = 0;
        List<String> synonyms = ArabicActionDictionary.synonyms(expectedAction);
        for (String synonym : synonyms) {
            int score = ArabicTextSimilarity.similarityPercent(actualText, synonym);
            if (score > bestScore) {
                bestScore = score;
                bestLabel = synonym;
            }
        }
        return bestScore >= 70 ? bestLabel : "";
    }
}
