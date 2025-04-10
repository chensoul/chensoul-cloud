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
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * A {@link Comparator} that allows for checked exceptions.
 *
 * @author Lukas Eder
 */
@FunctionalInterface
public interface CheckedComparator<T> {

    /**
     * @see {@link Sneaky#comparator(CheckedComparator)}
     */
    static <T> Comparator<T> sneaky(CheckedComparator<T> comparator) {
        return Sneaky.comparator(comparator);
    }

    /**
     * @see {@link Unchecked#comparator(CheckedComparator)}
     */
    static <T> Comparator<T> unchecked(CheckedComparator<T> comparator) {
        return Unchecked.comparator(comparator);
    }

    /**
     * @see {@link Unchecked#comparator(CheckedComparator, Consumer)}
     */
    static <T> Comparator<T> unchecked(CheckedComparator<T> comparator, Consumer<Throwable> handler) {
        return Unchecked.comparator(comparator, handler);
    }

    /**
     * Compares its two arguments for order.
     */
    int compare(T o1, T o2) throws Throwable;
}
