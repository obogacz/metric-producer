package com.richcode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestAppender extends ListAppender<ILoggingEvent> {

    public boolean contains(String string) {
        return getAll().stream()
                .anyMatch(line -> line.contains(string));
    }

    public boolean contains(String string, Level level) {
        return getAll(level).stream()
                .anyMatch(line -> line.contains(string));
    }

    public List<String> getAll() {
        return this.list.stream()
                .map(ILoggingEvent::getMessage)
                .collect(toList());
    }

    public List<String> getAll(Level level) {
        return this.list.stream()
                .filter(event -> event.getLevel().equals(level))
                .map(ILoggingEvent::getMessage)
                .collect(toList());
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

}
