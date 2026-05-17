package io.github.qatra.web.locators.healing.arabic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Deterministic Arabic UI action dictionary for locator healing.
 *
 * <p>It maps business actions such as save, submit, approve, reject, search,
 * and cancel to common Arabic labels found in government and enterprise systems.</p>
 */
public final class ArabicActionDictionary {

    private static final Map<String, List<String>> ACTIONS = new LinkedHashMap<>();

    static {
        register("save", "حفظ", "حفظ الطلب", "حفظ البيانات", "حفظ التغييرات", "حفظ وإغلاق", "تخزين");
        register("submit", "إرسال", "ارسال", "إرسال الطلب", "تقديم", "تقديم الطلب", "رفع الطلب");
        register("confirm", "تأكيد", "تاكيد", "تأكيد الطلب", "نعم", "موافق", "اعتماد التأكيد");
        register("approve", "اعتماد", "موافقة", "قبول", "قبول الطلب", "اعتماد الطلب", "الموافقة");
        register("reject", "رفض", "رفض الطلب", "إرجاع", "ارجاع", "إرجاع الطلب", "عدم الموافقة");
        register("cancel", "إلغاء", "الغاء", "إلغاء الطلب", "تراجع", "إغلاق", "اغلاق");
        register("back", "رجوع", "عودة", "السابق", "العودة", "رجوع للخلف");
        register("next", "التالي", "متابعة", "استمرار", "التالي >", "الانتقال التالي");
        register("search", "بحث", "ابحث", "استعلام", "بحث متقدم", "تنفيذ البحث");
        register("filter", "تصفية", "فلترة", "تطبيق الفلتر", "تطبيق التصفية");
        register("login", "تسجيل الدخول", "دخول", "الدخول", "تسجيل");
        register("logout", "تسجيل الخروج", "خروج", "الخروج");
        register("edit", "تعديل", "تحرير", "تحديث", "تعديل البيانات");
        register("delete", "حذف", "مسح", "إزالة", "ازالة", "حذف الطلب");
        register("add", "إضافة", "اضافة", "إضافة جديد", "جديد", "إنشاء", "انشاء");
        register("print", "طباعة", "اطبع", "طباعة التقرير");
        register("download", "تحميل", "تنزيل", "تنزيل الملف", "تحميل الملف");
        register("upload", "رفع", "رفع الملف", "تحميل مرفق", "إرفاق", "ارفاق");
    }

    private ArabicActionDictionary() {}

    public static void register(String action, String... labels) {
        if (action == null || action.isBlank()) return;
        String key = normalizeAction(action);
        List<String> values = ACTIONS.computeIfAbsent(key, ignored -> new ArrayList<>());
        for (String label : labels) {
            if (label != null && !label.isBlank() && values.stream().noneMatch(existing -> ArabicTextSimilarity.semanticContains(existing, label))) {
                values.add(label);
            }
        }
    }

    public static List<String> synonyms(String action) {
        if (action == null || action.isBlank()) return List.of();
        return ACTIONS.getOrDefault(normalizeAction(action), List.of(action));
    }

    public static boolean knownAction(String action) {
        return action != null && ACTIONS.containsKey(normalizeAction(action));
    }

    public static boolean matchesAction(String action, String actualText) {
        for (String synonym : synonyms(action)) {
            if (ArabicTextSimilarity.semanticContains(actualText, synonym)) {
                return true;
            }
        }
        return false;
    }

    public static String matchedSynonym(String action, String actualText) {
        for (String synonym : synonyms(action)) {
            if (ArabicTextSimilarity.semanticContains(actualText, synonym)) {
                return synonym;
            }
        }
        return "";
    }

    public static String normalizeAction(String action) {
        return action == null ? "" : action.trim().toLowerCase(Locale.ROOT).replace("_", "-").replace(" ", "-");
    }
}
