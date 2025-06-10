package dev.xkmc.loadingprofiler.reporting;

import dev.xkmc.loadingprofiler.init.LPCore;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class ReportWriter {

	public static String generate(List<String> writer) {
		String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String log = "logs/profiler/loading-" + time + ".txt";
		Path path = FMLPaths.GAMEDIR.get().resolve(log);
		write(path, (out) -> writer.forEach(out::println));
		return log;
	}

	private static void write(Path path, Consumer<PrintStream> cons) {
		PrintStream stream = null;
		try {
			stream = getStream(path);
			cons.accept(stream);
		} catch (Exception e) {
			LPCore.LOGGER.throwing(Level.ERROR, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					LPCore.LOGGER.throwing(Level.FATAL, e);
				}
			}
		}
	}

	private static PrintStream getStream(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("failed to create directory " + file.getParentFile());
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("failed to create file " + file);
			}
		}
		return new PrintStream(file);
	}


}
