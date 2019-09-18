package com.actoneye.util.sql.lambda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.actoneye.util.sql.exception.DataAccessException;

public class ResultSetIterator implements Iterator<Tuple> {

	private ResultSet rs;
	private PreparedStatement ps;
	private Connection connection;
	private String sql;
	private List<? extends Object> params;

	public ResultSetIterator(Connection connection, String sql, List<? extends Object> params) {
		assert connection != null;
		assert sql != null;
		this.connection = connection;
		this.sql = sql;
		this.params = params;
	}

	public void init() {
		try {
			ps = connection.prepareStatement(sql);
			int cnt = 1;
			for (Object param : params)
				ps.setObject(cnt++, param);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			close();
			throw new DataAccessException(e);
		}
	}

	@Override
	public boolean hasNext() {
		if (ps == null) {
			init();
		}
		try {
			boolean hasMore = rs.next();
			if (!hasMore) {
				close();
			}
			return hasMore;
		} catch (SQLException e) {
			close();
			throw new DataAccessException(e);
		}

	}

	void close() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {

			}
		}
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			// nothing we can do here
		}
	}

	@Override
	public Tuple next() {
		try {
			return SQL.rowAsTuple(sql, rs);
		} catch (DataAccessException e) {
			close();
			throw e;
		}
	}
}
