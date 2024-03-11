package org.eclipse.jdt.ls.core.internal.plantuml.logging;

public interface Logger {

    String localize(Message key, Object... args);

    void debug(Object message, Object... args);

    void info(Message key, Object... args);

    void warn(Message key, Object... args);

    void error(Message key, Object... args);

}
