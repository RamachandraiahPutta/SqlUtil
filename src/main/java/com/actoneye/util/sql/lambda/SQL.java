package com.actoneye.util.sql.lambda;

import static com.actoneye.util.sql.lambda.Pair.cons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.actoneye.util.sql.exception.DataAccessException;

public class SQL {

	private static Map<String, Pair<String, Integer>[]> META_DATA_CACHE = lruCache(1000);

	/**
	 * creates a lru cache map, which will hold least recently used entities at most
	 * given size
	 */

	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> lruCache(final int maxSize) {
		return new LinkedHashMap<K, V>(maxSize * 4 / 3, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > maxSize;
			}
		};
	}

	public static int execDML(Connection connection, String sql, List<? extends Object> values) {
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			int cnt = 0;
			for (Object param : values) {
				ps.setObject(++cnt, param);
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Tuple rowAsTuple(String sql, ResultSet rs) {
		try {

			Pair<String, Integer>[] columns = getColumns(sql, rs.getMetaData());

			Collection<Pair<String, ?>> result = StreamSupport.stream(Spliterators.spliterator(columns, 0), false)
					.map(o -> {
						Pair<String, Integer> column = (Pair<String, Integer>) o;
						try {
							return cons(column.getV1(), rs.getObject(column.getV1()));
						} catch (SQLException e) {
							throw new DataAccessException(e);
						}
					}).collect(Collectors.toList());
			return new DefaultTuple(result);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static Pair<String, Integer>[] getColumns(String sql, ResultSetMetaData rs) {
		try {
			int columnCount = rs.getColumnCount();
			Pair<String, Integer>[] columns = new Pair[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columns[i] = cons(rs.getColumnLabel(i + 1), rs.getColumnType(i + 1));
			}
			return columns;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	public static Stream<Tuple> stream(final Connection connection, final String sql,
			final List<? extends Object> parms) {

		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(connection, sql, parms), 0), false);
	}

}