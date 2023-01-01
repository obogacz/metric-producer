package com.richcode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricProducer {

    private static final String PARAM_CLASS_NAME = "class";
    private static final String PARAM_METHOD_NAME = "method";
    private static final String PARAM_EXECUTION_TIME = "executionTime";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String name;
    private final String className;
    private final String methodName;
    private final LocalDateTime start;
    private final List<Param> params = new LinkedList<>();
    private final boolean debug;

    public static MetricProducer create(String name) {
        return new MetricProducer(name, null, null, LocalDateTime.now(), false);
    }

    public static MetricProducer create(String name, Class<?> clazz) {
        return new MetricProducer(name, clazz.getName(), null, LocalDateTime.now(), false);
    }

    public static MetricProducer create(String name, Class<?>  clazz, String method) {
        return new MetricProducer(name, clazz.getName(), method, LocalDateTime.now(), false);
    }

    public static MetricProducer createInDebug(String name) {
        return new MetricProducer(name, null, null, LocalDateTime.now(), true);
    }

    public static MetricProducer createInDebug(String name, Class<?>  clazz) {
        return new MetricProducer(name, clazz.getName(), null, LocalDateTime.now(), true);
    }

    public static MetricProducer createInDebug(String name, Class<?>  clazz, String method) {
        return new MetricProducer(name, clazz.getName(), method, LocalDateTime.now(), true);
    }

    public MetricProducer addParam(String name, Number value) {
        return addMetricParam(name, value.toString());
    }

    public MetricProducer addParam(String name, Date value) {
        return addMetricParam(name, "\"" + DATE_FORMAT.format(value) + "\"");
    }

    public MetricProducer addParam(String name, Boolean value) {
        return addMetricParam(name, value.toString());
    }

    public MetricProducer addParam(String name, String value) {
        return addMetricParam(name, "\"" + value + "\"");
    }

    public MetricProducer addParam(String name, Enum<?> value) {
        return addMetricParam(name, "\"" + value.name() + "\"");
    }

    public MetricProducer addParam(String name, Object value) {
        return addMetricParam(name, "\"" + value.toString() + "\"");
    }

    private MetricProducer addMetricParam(String name, String value) {
        params.add(new Param(name, value));
        return this;
    }

    public <E extends Throwable> void measure(Action<E> action) throws E {
        try {
            action.execute();
        } finally {
            emit();
        }
    }

    public <T, E extends Throwable> T measure(CallbackAction<T, E> action) throws E {
        try {
            return action.execute();
        } finally {
            emit();
        }
    }

    private void emit() {
        if (debug) {
            log.debug(createMessage());
        } else {
            log.info(createMessage());
        }
    }

    private String createMessage() {
        addDefaultParams();
        return String.format("metric-name: \"%s\", params: {%s}", name, getParamsAsString());
    }

    private void addDefaultParams() {
        addParam(PARAM_EXECUTION_TIME, start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
        if (className != null) {
            addParam(PARAM_CLASS_NAME, className);
        }
        if (methodName != null) {
            addParam(PARAM_METHOD_NAME, methodName);
        }
    }

    private String getParamsAsString() {
        return params.stream()
                .map(Param::toString)
                .collect(joining(", "));
    }

    public interface Action<E extends Throwable> {
        void execute() throws E;
    }

    public interface CallbackAction<T, E extends Throwable> {
        T execute() throws E;
    }

    @RequiredArgsConstructor
    private static class Param {
        private final String name;
        private final String value;

        @Override
        public String toString() {
            return name + ": " + value;
        }
    }

}
