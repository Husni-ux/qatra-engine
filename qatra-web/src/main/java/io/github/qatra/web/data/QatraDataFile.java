package io.github.qatra.web.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the data file used by a QATRA TestNG data provider.
 *
 * <pre>
 * {@literal @}Test(dataProvider = "qatraData", dataProviderClass = QatraDataProvider.class)
 * {@literal @}QatraDataFile(path = "test-data/login-users.csv")
 * public void loginTest(QatraDataRecord user) {
 *     user.get("username");
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QatraDataFile {

    /** File path. Can be a classpath resource or a filesystem path. */
    String path();

    /** File format. Use AUTO to detect from extension. */
    DataFormat format() default DataFormat.AUTO;

    /** CSV delimiter. */
    char delimiter() default ',';

    /** Whether the first row contains column names. */
    boolean hasHeader() default true;

    /** Excel sheet name. When empty, sheetIndex is used. */
    String sheet() default "";

    /** Excel sheet index when sheet name is not provided. */
    int sheetIndex() default 0;
}
