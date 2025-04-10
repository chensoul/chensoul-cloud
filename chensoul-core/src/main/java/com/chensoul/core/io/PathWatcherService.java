package com.chensoul.core.io;

import com.chensoul.core.lambda.function.CheckedConsumer;
import com.chensoul.core.lambda.function.CheckedSupplier;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
public class PathWatcherService implements WatcherService, Runnable, DisposableBean {

	private static final String PROPERTY_DISABLE_WATCHER = PathWatcherService.class.getName();

	private static final WatchEvent.Kind[] KINDS = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};

	private final Consumer<File> onCreate;

	private final Consumer<File> onModify;

	private final Consumer<File> onDelete;

	private WatchService watchService;

	private Thread thread;

	public PathWatcherService(final File watchablePath, final Consumer<File> onModify) {
		this(watchablePath.toPath(), __ -> {
		}, onModify, __ -> {
		});
	}

	public PathWatcherService(final Path watchablePath, final Consumer<File> onCreate, final Consumer<File> onModify,
							  final Consumer<File> onDelete) {
		log.info("Watching directory path at [{}]", watchablePath);
		this.onCreate = onCreate;
		this.onModify = onModify;
		this.onDelete = onDelete;
		if (shouldEnableWatchService()) {
			initializeWatchService(watchablePath);
		}
	}

	@Override
	public void run() {
		if (shouldEnableWatchService()) {
			try {
				WatchKey key = null;
				while ((key = watchService.take()) != null) {
					handleEvent(key);
					val valid = key.reset();
					if (!valid) {
						log.info("Directory key is no longer valid. Quitting watcher util");
					}
				}
			} catch (final InterruptedException | ClosedWatchServiceException e) {
				log.trace(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void close() {
		log.trace("Closing util registry watcher thread");
		if (watchService != null) {
			try {
				watchService.close();
			} catch (IOException ignored) {

			}
		}
		if (this.thread != null) {
			thread.interrupt();
		}
		log.trace("Closed util registry watcher thread");
	}

	@Override
	public void start(final String name) {
		if (shouldEnableWatchService()) {
			log.trace("Starting watcher thread");
			thread = new Thread(this, name);
			thread.start();
		}
	}

	@Override
	public void destroy() {
		close();
	}

	protected void handleEvent(final WatchKey key) {
		key.pollEvents().forEach(CheckedConsumer.unchecked(event -> {
			String eventName = event.kind().name();

			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path filename = ev.context();

			Path parent = (Path) key.watchable();
			Path fullPath = parent.resolve(filename);
			File file = fullPath.toFile();

			log.trace("Detected event [{}] on file [{}]", eventName, file);
			if (eventName.equals(ENTRY_CREATE.name()) && file.exists()) {
				onCreate.accept(file);
			} else if (eventName.equals(ENTRY_DELETE.name())) {
				onDelete.accept(file);
			} else if (eventName.equals(ENTRY_MODIFY.name()) && file.exists()) {
				onModify.accept(file);
			}
		}));
	}

	protected boolean shouldEnableWatchService() {
		String watchServiceEnabled = StringUtils.defaultIfBlank(System.getenv(PROPERTY_DISABLE_WATCHER),
			System.getProperty(PROPERTY_DISABLE_WATCHER));
		return StringUtils.isBlank(watchServiceEnabled) || BooleanUtils.toBoolean(watchServiceEnabled);
	}

	protected void initializeWatchService(final Path watchablePath) {
		this.watchService = CheckedSupplier.unchecked(() -> watchablePath.getFileSystem().newWatchService()).get();
		log.debug("Created watcher for events of type [{}]",
			Arrays.stream(KINDS).map(WatchEvent.Kind::name).collect(Collectors.joining(",")));
		CheckedConsumer.unchecked(__ -> watchablePath.register(Objects.requireNonNull(this.watchService), KINDS));
	}

}
