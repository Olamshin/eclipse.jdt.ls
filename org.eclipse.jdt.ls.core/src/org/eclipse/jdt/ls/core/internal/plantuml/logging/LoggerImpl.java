package org.eclipse.jdt.ls.core.internal.plantuml.logging;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;

public class LoggerImpl implements Logger {

    @Override
    public String localize(Message key, Object... args) {
        return key.name();
    }

    @Override
    public void debug(Object message, Object... args) {
        JavaLanguageServerPlugin.debugTrace(message.toString());
    }

    @Override
    public void info(Message key, Object... args) {
        JavaLanguageServerPlugin.logInfo(key.name());
    }

    @Override
    public void warn(Message key, Object... args) {
        JavaLanguageServerPlugin.logInfo(key.name());
    }

    @Override
    public void error(Message key, Object... args) {
        JavaLanguageServerPlugin.logError(key.name());
    }

}
