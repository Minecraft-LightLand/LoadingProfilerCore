package dev.xkmc.lpcore.logdelegate;

import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;


public class LoggerForFMLModContainer extends Logger {

	public LoggerForFMLModContainer(LoggerContext context, String name, MessageFactory messageFactory) {
		super(context, name, messageFactory);
	}

	@Override
	public void debug(Marker marker, String message, Object params) {
		super.debug(marker, message, params);
		if (message.charAt(0) == 'C') {
			ModTracker.startCreate((String) params);
		}
	}

	@Override
	public void trace(Marker marker, String message, Object p0, Object p1) {
		super.trace(marker, message, p0, p1);
		switch (message.charAt(12)) {
			case 'a' -> ModTracker.finishCreate((String) p0);
			case 'i' -> ModTracker.startConstruct((String) p0);
			case 'n' -> ModTracker.finishConstruct((String) p0);
			case ' ' -> ModTracker.startEvent((String) p0, (Event) p1);
			case 'f' -> ModTracker.finishEvent((String) p0, (Event) p1);
			default -> LPBootCore.LOGGER.error("Unknown FML Mod Container logging mssage: {}", message);
		}
	}

	@Override
	public void trace(Marker marker, String message, Object p0) {
		super.trace(marker, message, p0);
		if (message.charAt(0) == 'I') {
			ModTracker.startSubscribe((String) p0);
		}
		if (message.charAt(0) == 'C') {
			ModTracker.finishSubscribe((String) p0);
		}
	}

}

