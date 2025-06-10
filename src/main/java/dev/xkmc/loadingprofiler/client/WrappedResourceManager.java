package dev.xkmc.loadingprofiler.client;

import dev.xkmc.loadingprofiler.init.ClientStages;
import dev.xkmc.loadingprofiler.init.LPCore;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PeriodicNotificationManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WrappedResourceManager extends ReloadableResourceManager {

	private static final List<PreparableReloadListener> ACTUAL = new ArrayList<>();

	public final List<WrapperListener> list = new ArrayList<>();
	public long startTime = 0, totalTime = 0;
	private final AtomicLong noTaskStart = new AtomicLong();
	private final AtomicLong noTaskTime = new AtomicLong();
	private final AtomicInteger taskCount = new AtomicInteger();
	public ReloadInstance instance;

	public WrappedResourceManager(PackType type) {
		super(type);
		LPClientTracker.set(this);
		LPCore.LOGGER.info("Wrapped Resource Manager Created!");
		registerReloadListener(new ModelAnalyzer());
	}

	public String getRunning() {
		StringBuilder builder = new StringBuilder();
		List<WrapperListener> running = new ArrayList<>();
		for (var e : list) {
			if (e.asyncRun.get() > 0 || e.syncRun.get() > 0) {
				running.add(e);
			}
		}
		if (running.size() == 1) {
			return running.get(0).detail();
		}
		boolean first = true;
		for (var e : running) {
			if (first) {
				builder.append("Running Tasks: [");
			} else {
				builder.append(", ");
			}
			first = false;
			builder.append(e.name);
		}
		if (first) {
			return "No Running Tasks";
		}
		builder.append("]");
		return builder.toString();
	}

	public void onStop() {
		LPCore.LOGGER.info("Wrapped Resource Reload Complete");
		totalTime = Util.getMillis();
		List<ReloadReportEntry> ans = new ArrayList<>();
		for (var e : list) {
			ans.add(e.getReport());
		}
		LPClientTracker.fillReport(totalTime, noTaskTime.get() / 1000000, ans);
		var vanilla = new ReloadableResourceManager(this.type);
		Minecraft.getInstance().resourceManager = vanilla;
		vanilla.resources = this.resources;
		vanilla.listeners.addAll(ACTUAL);
	}

	@Override
	public ReloadInstance createReload(Executor async, Executor sync, CompletableFuture<Unit> start, List<PackResources> resources) {
		LPEarly.info(ClientStages.CREATE_RELOAD);
		LPCore.LOGGER.info("Start profiling resource reload");
		for (var e : list) {
			e.clear();
		}
		ACTUAL.addAll(listeners);
		ACTUAL.removeIf(e -> e instanceof ModelAnalyzer);
		listeners.clear();
		listeners.addAll(list);
		startTime = Util.getMillis();
		instance = super.createReload(async, sync, start, resources);
		LPEarly.info(ClientStages.SETUP_OVERLAY);
		return instance;
	}

	@Override
	public void registerReloadListener(PreparableReloadListener listener) {
		var ans = new WrapperListener(listener);
		list.add(ans);
		super.registerReloadListener(listener);
		if (listener instanceof PeriodicNotificationManager)
			LPEarly.info(ClientStages.SETUP_DISPLAY);
	}

	public record ReloadReportEntry(String module, String name, long async, long sync) {
	}

	public class WrapperListener implements PreparableReloadListener {

		public final PreparableReloadListener inner;
		public final AtomicLong syncTime, asyncTime;
		public final AtomicInteger syncRun, asyncRun;
		public String module, name;

		public WrapperListener(PreparableReloadListener inner) {
			this.inner = inner;
			syncTime = new AtomicLong();
			asyncTime = new AtomicLong();
			syncRun = new AtomicInteger();
			asyncRun = new AtomicInteger();
			module = inner.getClass().getModule().getName();
			name = LPEarly.parseClassName(inner.getClass());
			LPCore.LOGGER.info("Wrapped Reload Listener <{}> Created!", module + " - " + name);
		}

		protected void clear() {
			syncTime.set(0);
			asyncTime.set(0);
			syncRun.set(0);
			asyncRun.set(0);
		}

		protected ReloadReportEntry getReport() {
			return new ReloadReportEntry(module, name, asyncTime.get(), syncTime.get());
		}

		@Override
		public CompletableFuture<Void> reload(
				PreparationBarrier barrier, ResourceManager manager,
				ProfilerFiller profAsync, ProfilerFiller profSync,
				Executor async, Executor sync
		) {
			return inner.reload(barrier, manager, profAsync, profSync,
					new WrappedExecutor(async, asyncTime, asyncRun),
					new WrappedExecutor(sync, syncTime, syncRun));
		}

		public String detail() {
			if (inner instanceof ModelManager) {
				return ModelStateTracker.getModelState();
			}
			return "Running " + name;
		}

		private class WrappedExecutor implements Executor {

			private final Executor executor;
			private final AtomicLong runTime;
			private final AtomicInteger runStack;

			private WrappedExecutor(Executor executor, AtomicLong runTime, AtomicInteger runStack) {
				this.executor = executor;
				this.runTime = runTime;
				this.runStack = runStack;
			}

			@Override
			public void execute(@NotNull Runnable runnable) {
				executor.execute(() -> {
					long start = System.nanoTime();
					int prev = taskCount.getAndAdd(1);
					if (prev == 0) {
						long prevTime = noTaskStart.get();
						if (prevTime > 0) {
							noTaskTime.addAndGet(start - prevTime);
						}
					}
					runStack.addAndGet(1);
					runnable.run();
					long next = System.nanoTime();
					runStack.addAndGet(-1);
					runTime.addAndGet(next - start);
					int count = taskCount.addAndGet(-1);
					if (count == 0) {
						noTaskStart.set(next);
					}
				});
			}

		}
	}

}
