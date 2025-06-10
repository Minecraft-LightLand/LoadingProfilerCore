package dev.xkmc.loadingprofiler.logdelegate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;
import org.jetbrains.annotations.Nullable;

public class LogDelegator {

	public static void delegate(Class<?> cls, Factory factory) {
		String name = cls.getName();
		LoggerContext ctx = (LoggerContext) LogManager.getContext(cls.getClassLoader(), false);
		ctx.getLoggerRegistry().putIfAbsent(name, null, factory.create(ctx, name, null));
	}

	public interface Factory {

		Logger create(LoggerContext ctx, String name, @Nullable MessageFactory fact);

	}

}
