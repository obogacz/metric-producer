package com.richcode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class MetricProducerTest {

    private static final TestAppender appender;

    static {
        appender = new TestAppender();
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(MetricProducer.class);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
    }

    @BeforeEach
    void beforeEach() {
        appender.clear();
    }

    @Test
    void shouldProduceMinimalLog() {
        MetricProducer.create("test-metric")
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("metric-name: \"test-metric\""));
        assertTrue(appender.contains("params"));
        assertTrue(appender.contains("executionTime"));
        assertFalse(appender.contains("exception: true"));

    }

    @Test
    void shouldProduceLogWithClass() {
        MetricProducer.create("test-metric", MetricProducerTest.class)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("metric-name: \"test-metric\""));
        assertTrue(appender.contains("class: \"com.richcode.MetricProducerTest\""));
    }

    @Test
    void shouldProduceLogWithClassAndMethod() {
        MetricProducer.create("test-metric", MetricProducerTest.class, "method()")
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("metric-name: \"test-metric\""));
        assertTrue(appender.contains("class: \"com.richcode.MetricProducerTest\""));
        assertTrue(appender.contains("method: \"method()\""));
    }

    @Test
    void shouldProduceLogWithIntParam() {
        MetricProducer.create("test-metric")
                .addParam("intParam", 2137)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("intParam: 2137"));
    }

    @Test
    void shouldProduceLogWithDoubleParam() {
        MetricProducer.create("test-metric")
                .addParam("doubleParam", 21.37)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("doubleParam: 21.37"));
    }

    @Test
    void shouldProduceLogWithDateParam() {
        Date date = new GregorianCalendar(
                2022,
                Calendar.DECEMBER,
                26,
                21,
                37,
                12
                ).getTime();

        MetricProducer.create("test-metric")
                .addParam("dateParam", date)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("dateParam: \"2022-12-26 21:37:12\""));
    }

    @Test
    void shouldProduceLogWithBooleanParam() {
        MetricProducer.create("test-metric")
                .addParam("booleanParam", true)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("booleanParam: true"));
    }

    @Test
    void shouldProduceLogWithStringParam() {
        MetricProducer.create("test-metric")
                .addParam("stringParam", "string")
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("stringParam: \"string\""));
    }

    @Test
    void shouldProduceLogWithEnumParam() {
        MetricProducer.create("test-metric")
                .addParam("enumParam", TestEnum.TEST_VAL)
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("enumParam: \"TEST_VAL\""));
    }

    @Test
    void shouldProduceLogWithObjectParam() {
        MetricProducer.create("test-metric")
                .addParam("objectParam", new TestClass())
                .measure(this::doNothing);

        assertEquals(1, appender.size());
        assertTrue(appender.contains("objectParam: \"MetricProducerTest.TestClass(field=value)"));
    }

    @Test
    void shouldProduceLogWhenThrowException() {
        assertThrows(Exception.class, () -> MetricProducer.create("test-metric")
                .measure(() -> {
                    throw new Exception();
                }));

        assertEquals(1, appender.size());
    }

    @Test
    void shouldProduceLogAndReturnValue() {
        String result = MetricProducer.create("test-metric")
                .measure(() -> "test");

        assertEquals("test", result);
    }

    @Test
    void shouldProduceLogWithExceptionFlag() {
        assertThrows(Exception.class, () -> MetricProducer.create("test-metric")
                .measure(() -> {
                    throw new Exception();
                }));

        assertTrue(appender.contains("exception: true"));
    }

    private void doNothing() {
        // do nothing
    }

    private enum TestEnum {
        TEST_VAL
    }

    @ToString
    private static class TestClass {
        private final String field = "value";
    }

}