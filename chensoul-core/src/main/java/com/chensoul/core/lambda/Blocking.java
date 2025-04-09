/**
 * Copyright (c), Data Geekery GmbH, contact@datageekery.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chensoul.core.lambda;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.function.*;

/**
 * A factory class for methods that wrap functional interfaces like {@link Supplier} in a
 * "blocking" ({@link ManagedBlocker}) equivalent, which can be used with the
 * {@link ForkJoinPool}.
 *
 * @author Lukas Eder
 */
public final class Blocking {

	private Blocking() {
	}

	public static Runnable runnable(Runnable runnable) {
		return () -> supplier(() -> {
			runnable.run();
			return null;
		}).get();
	}

	public static <T, U> BiConsumer<T, U> biConsumer(BiConsumer<? super T, ? super U> biConsumer) {
		return (t, u) -> runnable(() -> biConsumer.accept(t, u)).run();
	}

	public static <T, U, R> BiFunction<T, U, R> biFunction(BiFunction<? super T, ? super U, ? extends R> biFunction) {
		return (t, u) -> supplier(() -> biFunction.apply(t, u)).get();
	}

	public static <T, U> BiPredicate<T, U> biPredicate(BiPredicate<? super T, ? super U> biPredicate) {
		return (t, u) -> supplier(() -> biPredicate.test(t, u)).get();
	}

	public static <T> Consumer<T> consumer(Consumer<? super T> consumer) {
		return t -> runnable(() -> consumer.accept(t)).run();
	}

	public static <T, R> Function<T, R> function(Function<? super T, ? extends R> function) {
		return t -> supplier(() -> function.apply(t)).get();
	}

	public static <T> Predicate<T> predicate(Predicate<? super T> predicate) {
		return t -> supplier(() -> predicate.test(t)).get();
	}

	public static <T> Supplier<T> supplier(Supplier<? extends T> supplier) {
		return new BlockingSupplier<>(supplier);
	}

	static class BlockingSupplier<T> implements Supplier<T> {

		private static final Object NULL = new Object();

		final Supplier<? extends T> supplier;

		volatile T result = (T) NULL;

		BlockingSupplier(Supplier<? extends T> supplier) {
			this.supplier = supplier;
		}

		@Override
		public T get() {
			try {
				ForkJoinPool.managedBlock(new ManagedBlocker() {
					@Override
					public boolean block() throws InterruptedException {
						result = supplier.get();
						return true;
					}

					@Override
					public boolean isReleasable() {
						return result != NULL;
					}
				});
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			return result;
		}

	}

}
