package com.richcode;

import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Main {

    public static void main(String[] args) {

        // Basic usage
        MetricProducer.create("action")
                .measure(Main::action);

        // Usage with all parameter types
        MetricProducer.create("action")
                .addParam("intParam", 2137)
                .addParam("doubleParam", 21.37)
                .addParam("booleanParam", true)
                .addParam("dateParam", new Date())
                .addParam("stringParam", "someString")
                .addParam("enumParam", Foo.ENUM_FIELD)
                .addParam("objectParam", new Boo())
                .measure(Main::action);

        // Usage with a throwable callback action
        try {
            String result = MetricProducer.create("action", Main.class, "method()")
                    .measure(Main::throwableCallbackAction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Exception flag
        try {
            MetricProducer.create("action")
                    .measure(() -> {
                        throw new Exception();
                    });
        } catch (Exception e) {
        }

    }

    @SneakyThrows
    private static void action() {
        Thread.sleep(2137);
    }

    private static String throwableCallbackAction() throws Exception {
        // does sth
        return "result";
    }

    private enum Foo {
        ENUM_FIELD
    }

    @ToString
    private static class Boo {
        private final String field1 = "string";
        private final int field2 = 2137;
    }

}
