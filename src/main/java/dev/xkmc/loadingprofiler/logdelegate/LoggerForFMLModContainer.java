package dev.xkmc.loadingprofiler.logdelegate;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class LoggerForFMLModContainer extends Logger {

	private static final Field FIELD;

	static {
		try {
			var cls = FMLModContainer.class;
			var field = cls.getDeclaredField("modClasses");
			field.setAccessible(true);
			FIELD = field;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public LoggerForFMLModContainer(LoggerContext context, String name, MessageFactory messageFactory) {
		super(context, name, messageFactory);
	}

	@Override
	public void trace(Marker marker, String message, Object p0, Object p1) {
		super.trace(marker, message, p0, p1);
		if (message.charAt(12) == 'a') {
			wrap(ModLoadingContext.get().getActiveContainer());
		}
	}

	@Override
	public void trace(Marker marker, String message, Object p0) {
		super.trace(marker, message, p0);
		if (message.charAt(0) == 'C') {
			ModTracker.finishConstruct((String) p0);
		}
	}

	private void wrap(ModContainer cont) {
		if (cont instanceof FMLModContainer fml) {
			try {
				var list = (List) FIELD.get(fml);
				var ans = MyList.of(fml, list);
				FIELD.set(fml, ans);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static class MyList<T> extends ArrayList<T> {

		public static <T> MyList<T> of(FMLModContainer fml, List<T> list) {
			if (list instanceof MyList<T> ans) return ans;
			return new MyList<>(fml, list);
		}

		private final FMLModContainer fml;
		private boolean init = false;

		public MyList(FMLModContainer fml, @NotNull Collection<? extends T> c) {
			super(c);
			this.fml = fml;
		}

		@Override
		public @NotNull Iterator<T> iterator() {
			if (!init) {
				init = true;
				ModTracker.startConstruct(fml.getModId());
			}
			return super.iterator();
		}
	}

}

