package io.github.qatra.web.waits.adaptive;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable builder used by QatraAdaptiveWait.require() to collect readiness conditions.
 */
public final class QatraConditionGroup {

    private final List<QatraCondition> conditions = new ArrayList<>();

    public QatraConditionGroup add(QatraCondition condition) {
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public List<QatraCondition> conditions() {
        return List.copyOf(conditions);
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }
}
