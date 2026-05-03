package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import org.openqa.selenium.By;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight PageFactory-like helper for QATRA Page Objects and Components.
 */
public final class QatraPageFactory {

    private QatraPageFactory() {
    }

    /**
     * Create a QATRA page using a constructor that accepts io.github.qatra.web.WebDriver.
     */
    public static <T extends QatraPage> T create(Class<T> pageClass, WebDriver driver) {
        try {
            Constructor<T> constructor = pageClass.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Failed to create QATRA page object: " + pageClass.getName() +
                            ". The page must define a constructor that accepts io.github.qatra.web.WebDriver.",
                    e
            );
        }
    }

    /**
     * Create a QATRA reusable component using a constructor(WebDriver, By).
     */
    public static <T extends QatraComponent> T createComponent(Class<T> componentClass, WebDriver driver, By rootLocator) {
        try {
            Constructor<T> constructor = componentClass.getDeclaredConstructor(WebDriver.class, By.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver, rootLocator);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Failed to create QATRA component object: " + componentClass.getName() +
                            ". The component must define a constructor(WebDriver, By).",
                    e
            );
        }
    }

    /** Inject fields annotated with @QatraFindBy into a QatraPage instance. */
    public static void initElements(QatraPage page) {
        initElements(page, page.qatraDriver(), null);
    }

    /** Inject fields annotated with @QatraFindBy into a QatraComponent instance. */
    public static void initElements(QatraComponent component) {
        initElements(component, component.qatraDriver(), component.rootLocator());
    }

    private static void initElements(Object target, WebDriver driver, By scopeLocator) {
        List<Field> fields = collectFields(target.getClass());
        for (Field field : fields) {
            QatraFindBy annotation = field.getAnnotation(QatraFindBy.class);
            if (annotation == null) {
                continue;
            }

            By locator = buildBy(annotation);
            Object value = createFieldValue(driver, field, locator, scopeLocator);
            try {
                field.setAccessible(true);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to inject QATRA field: " + field.getName(), e);
            }
        }
    }

    private static Object createFieldValue(WebDriver driver, Field field, By locator, By scopeLocator) {
        Class<?> fieldType = field.getType();
        if (QatraElement.class.isAssignableFrom(fieldType)) {
            return new QatraElement(driver, locator, scopeLocator);
        }
        if (QatraElementCollection.class.isAssignableFrom(fieldType)) {
            return new QatraElementCollection(driver, locator, scopeLocator);
        }
        if (QatraComponent.class.isAssignableFrom(fieldType)) {
            @SuppressWarnings("unchecked")
            Class<? extends QatraComponent> componentClass = (Class<? extends QatraComponent>) fieldType;
            return createComponent(componentClass, driver, locator);
        }
        if (By.class.isAssignableFrom(fieldType)) {
            return locator;
        }
        throw new IllegalStateException(
                "Unsupported @QatraFindBy field type: " + fieldType.getName() +
                        ". Supported types: QatraElement, QatraElementCollection, QatraComponent, By."
        );
    }

    private static List<Field> collectFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    static By buildBy(QatraFindBy findBy) {
        if (!isBlank(findBy.id())) return By.id(findBy.id());
        if (!isBlank(findBy.name())) return By.name(findBy.name());
        if (!isBlank(findBy.css())) return By.cssSelector(findBy.css());
        if (!isBlank(findBy.xpath())) return By.xpath(findBy.xpath());
        if (!isBlank(findBy.tagName())) return By.tagName(findBy.tagName());
        if (!isBlank(findBy.className())) return By.className(findBy.className());
        if (!isBlank(findBy.linkText())) return By.linkText(findBy.linkText());
        if (!isBlank(findBy.partialLinkText())) return By.partialLinkText(findBy.partialLinkText());
        if (!isBlank(findBy.testId())) return By.cssSelector("[data-testid='" + cssAttributeEscape(findBy.testId()) + "']");
        if (!isBlank(findBy.dataTestId())) return By.cssSelector("[data-testid='" + cssAttributeEscape(findBy.dataTestId()) + "']");

        throw new IllegalStateException("@QatraFindBy must define at least one locator strategy.");
    }

    static By buildBy(QatraPageLoaded pageLoaded) {
        if (!isBlank(pageLoaded.id())) return By.id(pageLoaded.id());
        if (!isBlank(pageLoaded.name())) return By.name(pageLoaded.name());
        if (!isBlank(pageLoaded.css())) return By.cssSelector(pageLoaded.css());
        if (!isBlank(pageLoaded.xpath())) return By.xpath(pageLoaded.xpath());
        if (!isBlank(pageLoaded.tagName())) return By.tagName(pageLoaded.tagName());
        if (!isBlank(pageLoaded.className())) return By.className(pageLoaded.className());
        if (!isBlank(pageLoaded.linkText())) return By.linkText(pageLoaded.linkText());
        if (!isBlank(pageLoaded.partialLinkText())) return By.partialLinkText(pageLoaded.partialLinkText());
        if (!isBlank(pageLoaded.testId())) return By.cssSelector("[data-testid='" + cssAttributeEscape(pageLoaded.testId()) + "']");
        if (!isBlank(pageLoaded.dataTestId())) return By.cssSelector("[data-testid='" + cssAttributeEscape(pageLoaded.dataTestId()) + "']");

        throw new IllegalStateException("@QatraPageLoaded must define at least one locator strategy.");
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String cssAttributeEscape(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
