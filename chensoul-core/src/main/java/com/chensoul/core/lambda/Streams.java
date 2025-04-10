package com.chensoul.core.lambda;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public interface Streams {

	static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * <p>
	 * filterStream.
	 * </p>
	 *
	 * @param values    a S object
	 * @param predicate a {@link Predicate} object
	 * @param <T>       a T class
	 * @param <S>       a S class
	 * @return a {@link Stream} object
	 */
	static <T, S extends Iterable<T>> Stream<T> filterStream(S values, Predicate<? super T> predicate) {
		return StreamSupport.stream(values.spliterator(), false).filter(predicate);
	}

	/**
	 * <p>
	 * filterList.
	 * </p>
	 *
	 * @param values    a S object
	 * @param predicate a {@link Predicate} object
	 * @param <T>       a T class
	 * @param <S>       a S class
	 * @return a {@link List} object
	 */
	static <T, S extends Iterable<T>> List<T> filterList(S values, Predicate<? super T> predicate) {
		return filterStream(values, predicate).collect(toList());
	}

	/**
	 * <p>
	 * filterSet.
	 * </p>
	 *
	 * @param values    a S object
	 * @param predicate a {@link Predicate} object
	 * @param <T>       a T class
	 * @param <S>       a S class
	 * @return a {@link Set} object
	 */
	static <T, S extends Iterable<T>> Set<T> filterSet(S values, Predicate<? super T> predicate) {
		// new Set with insertion order
		return filterStream(values, predicate).collect(LinkedHashSet::new, Set::add, Set::addAll);
	}

	/**
	 * <p>
	 * filter.
	 * </p>
	 *
	 * @param values    a S object
	 * @param predicate a {@link Predicate} object
	 * @param <T>       a T class
	 * @param <S>       a S class
	 * @return a S object
	 */
	static <T, S extends Iterable<T>> S filter(S values, Predicate<? super T> predicate) {
		final boolean isSet = Set.class.isAssignableFrom(values.getClass());
		return (S) (isSet ? filterSet(values, predicate) : filterList(values, predicate));
	}

	/**
	 * <p>
	 * filterAll.
	 * </p>
	 *
	 * @param values     a S object
	 * @param predicates a {@link Predicate} object
	 * @param <T>        a T class
	 * @param <S>        a S class
	 * @return a S object
	 */
	@SafeVarargs
	static <T, S extends Iterable<T>> S filterAll(S values, Predicate<? super T>... predicates) {
		return filter(values, Predicates.and(predicates));
	}

	/**
	 * <p>
	 * filterAny.
	 * </p>
	 *
	 * @param values     a S object
	 * @param predicates a {@link Predicate} object
	 * @param <T>        a T class
	 * @param <S>        a S class
	 * @return a S object
	 */
	@SafeVarargs
	static <T, S extends Iterable<T>> S filterAny(S values, Predicate<? super T>... predicates) {
		return filter(values, Predicates.or(predicates));
	}

	/**
	 * <p>
	 * filterFirst.
	 * </p>
	 *
	 * @param values     a {@link Iterable} object
	 * @param predicates a {@link Predicate} object
	 * @param <T>        a T class
	 * @return a T object
	 */
	@SafeVarargs
	static <T> T filterFirst(Iterable<T> values, Predicate<? super T>... predicates) {
		return StreamSupport.stream(values.spliterator(), false)
			.filter(Predicates.and(predicates))
			.findFirst()
			.orElse(null);
	}

}
