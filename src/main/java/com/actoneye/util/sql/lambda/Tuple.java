package com.actoneye.util.sql.lambda;

import static com.actoneye.util.sql.lambda.TransformerService.convert;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A tuple is as defined in the relational model, where each element is defined
 * by a name, and is ordered according to the order of elements in the resultant
 * set
 * 
 * It is Collection of Pair objects
 */
public interface Tuple extends Collection<Pair<String, ? extends Object>> {

	public default <T> Optional<T> val(String name) {
		return (Optional<T>) val(name, Object.class);
	}

	public <T> Optional<T> val(int index);

	public <T> Optional<T> val(int index, Class<T> type);

	public default long asLong(String name) {
		return val(name).map((val) -> convert(val, Long.class)).orElse(0L);
	}

	public default float asFloat(String name) {
		return val(name).map((val) -> convert(val, Float.class)).orElse(0.0f);
	}

	public default double asDouble(String name) {
		return val(name).map((val) -> convert(val, Double.class)).orElse(0.0);
	}

	public default BigDecimal asBigDecimal(String name) {
		return val(name).map((val) -> convert(val, BigDecimal.class)).orElse(null);
	}

	public default short asShort(String name) {
		return val(name).map((val) -> convert(val, Short.class)).orElse((short) 0);
	}

	public default byte asByte(String name) {
		return val(name).map((val) -> convert(val, Byte.class)).orElse((byte) 0);
	}

	public default boolean asBoolean(String name) {
		return val(name).map((val) -> convert(val, Boolean.class)).orElse(false);
	}

	public default char asChar(String name) {
		return val(name).map((val) -> convert(val, Character.class)).orElse((char) 0);
	}

	public default int asInt(String name) {
		return val(name).map((val) -> convert(val, Integer.class)).orElse(0);
	}

	public default int asInt(int index) {
		return val(index).map((val) -> convert(val, Integer.class)).orElse(0);
	}

	public default float asFloat(int index) {
		return val(index).map((val) -> convert(val, Float.class)).orElse(0.0f);
	}

	public default double asDouble(int index) {
		return val(index).map((val) -> convert(val, Double.class)).orElse(0.0);
	}

	public default BigDecimal asBigDecimal(int index) {
		return val(index).map((val) -> convert(val, BigDecimal.class)).orElse(null);
	}

	public default long asLong(int index) {
		return (long) val(index).map((val) -> convert(val, Long.class)).orElse(0L);
	}

	public default short asShort(int index) {
		return (short) val(index).map((val) -> convert(val, Short.class)).orElse((short) 0);
	}

	public default byte asByte(int index) {
		return (byte) val(index).map((val) -> convert(val, Byte.class)).orElse((byte) 0);
	}

	public default boolean asBoolean(int index) {
		return (boolean) val(index).map((val) -> convert(val, Boolean.class)).orElse(false);
	}

	public default char asChar(int index) {
		return (char) val(index).map((val) -> convert(val, Character.class)).orElse((char) 0);
	}

	public default String asString(String name) {
		return val(name).map((val) -> convert(val, String.class)).orElse(null);
	}

	default Timestamp asTimestamp(String name) {
		return val(name).map((val) -> convert(val, Timestamp.class)).orElse(null);
	}

	public default <T> Optional<T> val(String name, Class<T> type) {
		for (Pair<String, ?> pair : this) {
			if (name.equalsIgnoreCase(pair.getV1())) {
				return ofNullable(TransformerService.convert(pair.getV2(), type));
			}
		}
		return Optional.empty();
	}

	public default Tuple subTuple(final String... subset) {
		Collection<Pair<String, ? extends Object>> result = (Collection<Pair<String, ? extends Object>>) stream()
				.filter((pair) -> {
					for (String s : subset) {
						if (s.equals(pair.getV1())) {
							return true;
						}
					}
					return false;
				}).collect(toList());
		return createFrom(result);
	}

	public default Tuple merge(Tuple second) {
		Collection<Pair<String, ?>> mergedCollection = new ArrayList<Pair<String, ?>>(this);
		mergedCollection.addAll(this);
		second.forEach((item) -> {
			if (!mergedCollection.contains(item)) {
				mergedCollection.add(item);
			}
		});
		return createFrom(mergedCollection);
	}

	public default Tuple set(Pair<String, ?>... columns) {
		DefaultTuple newTuple = new DefaultTuple(columns);
		return newTuple.merge(this);
	}

	public default Tuple reduce(String suffix) {
		List<Pair<String, ?>> results = stream()
				.filter((column) -> column.getV1().toLowerCase().startsWith(suffix.toLowerCase())).collect(toList());
		return new DefaultTuple(results);
	}

	public default boolean hasVal(String name) {
		return val(name).isPresent();
	}

	public Tuple createFrom(Collection<Pair<String, ? extends Object>> values);

}
