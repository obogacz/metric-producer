# Metric Producer
Simple class implementation for emitting logs with execution time and custom parameters for java methods.

## Examples

### Basic usage
```java
MetricProducer.create("action")
        .measure(() -> {
            // do sth
        });

// output:
// metric-name: "action", params: {executionTime: 2}
```

### Metric name + class + method name
```java
MetricProducer.create("action", Main.class, "method()")
        .measure(() -> {
            // do sth
        });

// output:
// metric-name: "action", params: {executionTime: 1, class: "com.richcode.Main", method: "method()"}
```

### All parameter types
```java
MetricProducer.create("action")
        .addParam("intParam", 2137)
        .addParam("doubleParam", 21.37)
        .addParam("booleanParam", true)
        .addParam("dateParam", new Date())
        .addParam("stringParam", "someString")
        .addParam("enumParam", Foo.ENUM_FIELD)
        .addParam("objectParam", new Boo())
        .measure(() -> {
            // do sth
        });

// output:
// metric-name: "action", params: {intParam: 2137, doubleParam: 21.37, booleanParam: true, dateParam: "2023-01-01 22:53:24", stringParam: "someString", enumParam: "ENUM_FIELD", objectParam: "Main.Boo(field1=string, field2=2137)", executionTime: 17}
```

### Callback action
```java
String result = MetricProducer.create("action")
        .measure(() -> {
            return "string";
        });

// output:
// metric-name: "action", params: {executionTime: 2}
```

### Throwable action (exception: true)
```java
MetricProducer.create("action")
        .measure(() -> {
            throw new Exception();
        });

// output:
// metric-name: "action", params: {exception: true, executionTime: 1}
```
