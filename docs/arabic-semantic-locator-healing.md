# Arabic Semantic Locator Healing

Phase 3.18.3 adds Arabic-aware semantic locator healing to QATRA.

## Why this matters

Arabic enterprise and government systems often change button IDs, wrappers, and CSS classes, while the business action stays the same:

- حفظ
- حفظ الطلب
- إرسال
- اعتماد
- رفض
- إلغاء
- بحث

Traditional Selenium locators do not understand that these are business actions. QATRA now provides a deterministic Arabic semantic layer that can use action intent as healing evidence.

## New capabilities

- Arabic action dictionary
- Arabic text normalization
- Diacritics and tatweel tolerance
- Alef/Yeh/Ta Marbuta normalization
- Arabic semantic action fallback locators
- Arabic nearby label fallback for form fields
- Arabic business-action confidence scoring
- Evidence-based healing decisions

## Example

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-btn"))
        .named("Save request button")
        .expectedRole("button")
        .semanticArabicAction("save")
        .fallbackDataTestId("submit-request")
        .fallbackArabicAction("save")
        .build();

driver()
        .element()
        .smartClick(saveButton);
```

If the primary locator fails, QATRA can evaluate Arabic labels such as `حفظ`, `حفظ الطلب`, and `حفظ البيانات`, then score the candidate using visibility, role, uniqueness, and semantic evidence.

## Arabic label-based fields

```java
QatraLocator facilityName = QatraLocator.primary(By.id("facility-name"))
        .named("Facility name field")
        .expectedRole("textbox")
        .expectedArabicText("اسم المنشأة")
        .fallbackArabicLabel("اسم المنشأة")
        .build();

driver().element().smartType(facilityName, "منشأة تجريبية");
```

## Supported action examples

- save: حفظ، حفظ الطلب، حفظ البيانات
- submit: إرسال، إرسال الطلب، تقديم الطلب
- approve: اعتماد، موافقة، قبول
- reject: رفض، إرجاع، عدم الموافقة
- cancel: إلغاء، تراجع، إغلاق
- search: بحث، استعلام
- filter: تصفية، تطبيق الفلتر

## Important principle

QATRA does not blindly click Arabic text. The semantic match is one input into the confidence/risk engine. Auto-healing still depends on the configured healing mode, confidence threshold, and risk level.
