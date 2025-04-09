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
package com.chensoul.core.lambda.function;

import com.chensoul.core.lambda.Sneaky;
import com.chensoul.core.lambda.Unchecked;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A {@link BiFunction} that allows for checked exceptions.
 *
 * @author Lukas Eder
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {

	/**
	 * @see {@link Sneaky#biFunction(CheckedBiFunction)}
	 */
	static <T, U, R> BiFunction<T, U, R> sneaky(CheckedBiFunction<T, U, R> function) {
		return Sneaky.biFunction(function);
	}

	/**
	 * @see {@link Unchecked#biFunction(CheckedBiFunction)}
	 */
	static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> function) {
		return Unchecked.biFunction(function);
	}

	/**
	 * @see {@link Unchecked#biFunction(CheckedBiFunction, Consumer)}
	 */
	static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> function, Consumer<Throwable> handler) {
		return Unchecked.biFunction(function, handler);
	}

	/**
	 * Applies this function to the given arguments.
	 * @param t the first function argument
	 * @param u the second function argument
	 * @return the function result
	 */
	R apply(T t, U u) throws Throwable;

}
