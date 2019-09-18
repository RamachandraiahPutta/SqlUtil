package com.actoneye.util.sql;

import static com.actoneye.util.sql.lambda.SQL.stream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.actoneye.util.sql.lambda.Tuple;

public class SqlUtilApplication {

	public static void main(String[] args) throws SQLException {

		String query = "select * from ne_users where user_id=?";

		Optional<Tuple> result1 = executeOne(query, Arrays.asList(6061));
		result1.ifPresent(System.out::println);

		String query2 = "select * from ne_users where user_id>? and user_id<?";
		List<User> users = execute(query2, Arrays.asList(6060, 6072),
				t -> new User(t.asString("user_id"), t.asString("email"), t.asString("fname"), t.asString("lname")));
		System.out.println(users);

	}

	static <T> List<T> execute(final String query, final List<? extends Object> parameters, final Function<Tuple, T> f)
			throws SQLException {

		try (final Connection connection = getConnection()) {
			return stream(connection, query, parameters).map(f).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	static <T> Optional<Tuple> executeOne(final String query, final List<? extends Object> parameters)
			throws SQLException {

		try (final Connection connection = getConnection()) {
			return stream(connection, query, parameters).findFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {

			String url = "jdbc:mysql://localhost:3306/diva?useSSL=false";
			String user = "root";
			String password = "root";
			connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return connection;

	}

}

class User {

	private String id;
	private String email;
	private String firstName;
	private String lastName;

	public User(String id, String email, String firstName, String lastName) {
		super();
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public void setName(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}
