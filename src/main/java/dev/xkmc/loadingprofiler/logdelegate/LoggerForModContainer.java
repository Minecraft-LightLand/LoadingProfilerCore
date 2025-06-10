package dev.xkmc.loadingprofiler.logdelegate;

import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import net.neoforged.bus.api.Event;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;


public class LoggerForModContainer extends Logger {

	public LoggerForModContainer(LoggerContext context, String name, MessageFactory messageFactory) {
		super(context, name, messageFactory);
	}

	@Override
	public void trace(Marker marker, String message, Object p0, Object p1) {
		super.trace(marker, message, p0, p1);
		switch (message.charAt(12)) {
			case ' ' -> ModTracker.startEvent((String) p0, (Event) p1);
			case 'f' -> ModTracker.finishEvent((String) p0, (Event) p1);
			default -> LPBootCore.LOGGER.error("Unknown FML Mod Container logging massage: {}", message);
		}
	}

	@Override
	public void trace(Marker marker, String message, Object phase, Object p0, Object p1) {
		super.trace(marker, message, phase, p0, p1);
		switch (message.charAt(12)) {
			case ' ' -> ModTracker.startEvent((String) p0, (Event) p1);
			case 'f' -> ModTracker.finishEvent((String) p0, (Event) p1);
			default -> LPBootCore.LOGGER.error("Unknown FML Mod Container logging massage: {}", message);
		}
	}

}

