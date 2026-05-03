package io.github.qatra.web.data;

import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;

/**
 * Ready-to-use TestNG data providers for QATRA.
 */
public final class QatraDataProvider {

    private QatraDataProvider() {
    }

    /**
     * Reads the file declared by {@link QatraDataFile} on the test method.
     */
    @DataProvider(name = "qatraData")
    public static Object[][] qatraData(Method method) {
        QatraDataFile dataFile = method.getAnnotation(QatraDataFile.class);
        if (dataFile == null) {
            throw new IllegalArgumentException("Test method must be annotated with @QatraDataFile: " + method.getName());
        }
        return QatraData.testNgRecords(dataFile);
    }

    /** Alias for readability when a test expects one QatraDataRecord parameter. */
    @DataProvider(name = "qatraRecords")
    public static Object[][] qatraRecords(Method method) {
        return qatraData(method);
    }
}
