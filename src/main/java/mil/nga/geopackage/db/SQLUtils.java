package mil.nga.geopackage.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.user.ContentValues;

/**
 * SQL Utility methods
 * 
 * @author osbornb
 */
public class SQLUtils {

	/**
	 * Logger
	 */
	private static final Logger log = Logger
			.getLogger(SQLUtils.class.getName());

	/**
	 * Execute the SQL
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 */
	public static void execSQL(Connection connection, String sql) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			throw new GeoPackageException("Failed to execute SQL statement: "
					+ sql, e);
		} finally {
			closeStatement(statement, sql);
		}

	}

	/**
	 * Query for results
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param selectionArgs
	 *            selection arguments
	 * @return result set
	 */
	public static ResultSet query(Connection connection, String sql,
			String[] selectionArgs) {

		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.prepareStatement(sql);
			setArguments(statement, selectionArgs);
			resultSet = statement.executeQuery();
		} catch (SQLException e) {
			throw new GeoPackageException("Failed to execute SQL statement: "
					+ sql, e);
		} finally {
			if (resultSet == null) {
				closeStatement(statement, sql);
			}
		}

		return resultSet;
	}

	/**
	 * Attempt to count the results of the query
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param selectionArgs
	 *            selection arguments
	 * @return count if known, -1 if not able to determine
	 */
	public static int count(Connection connection, String sql,
			String[] selectionArgs) {

		if (!sql.toLowerCase().contains(" count(*) ")) {
			int index = sql.toLowerCase().indexOf(" from ");
			if (index == -1) {
				return -1;
			}
			sql = "select count(*)" + sql.substring(index);
		}

		int count = querySingleInteger(connection, sql, selectionArgs, true);

		return count;
	}

	/**
	 * Get the query count
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return count
	 */
	public static int count(Connection connection, String table, String where,
			String[] args) {
		StringBuilder countQuery = new StringBuilder();
		countQuery.append("select count(*) from ").append(
				CoreSQLUtils.quoteWrap(table));
		if (where != null) {
			countQuery.append(" where ").append(where);
		}
		String sql = countQuery.toString();

		int count = querySingleInteger(connection, sql, args, true);

		return count;
	}

	/**
	 * Get the min query result
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return min or null
	 * @since 1.1.1
	 */
	public static Integer min(Connection connection, String table,
			String column, String where, String[] args) {

		Integer min = null;
		if (count(connection, table, where, args) > 0) {
			StringBuilder minQuery = new StringBuilder();
			minQuery.append("select min(")
					.append(CoreSQLUtils.quoteWrap(column)).append(") from ")
					.append(CoreSQLUtils.quoteWrap(table));
			if (where != null) {
				minQuery.append(" where ").append(where);
			}
			String sql = minQuery.toString();

			min = querySingleInteger(connection, sql, args, false);
		}

		return min;
	}

	/**
	 * Get the max query result
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return max or null
	 * @since 1.1.1
	 */
	public static Integer max(Connection connection, String table,
			String column, String where, String[] args) {

		Integer max = null;
		if (count(connection, table, where, args) > 0) {
			StringBuilder maxQuery = new StringBuilder();
			maxQuery.append("select max(")
					.append(CoreSQLUtils.quoteWrap(column)).append(") from ")
					.append(CoreSQLUtils.quoteWrap(table));
			if (where != null) {
				maxQuery.append(" where ").append(where);
			}
			String sql = maxQuery.toString();

			max = querySingleInteger(connection, sql, args, false);
		}

		return max;
	}

	/**
	 * Query the SQL for a single integer result
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql
	 * @param args
	 *            query arguments
	 * @param allowEmptyResults
	 *            true to accept empty results as a 0 return
	 * @return Integer result, null if no result
	 */
	private static int querySingleInteger(Connection connection, String sql,
			String[] args, boolean allowEmptyResults) {

		int result = 0;

		Object value = querySingleResult(connection, sql, args, 0,
				GeoPackageDataType.MEDIUMINT);
		if (value != null) {
			result = ((Number) value).intValue();
		} else if (!allowEmptyResults) {
			throw new GeoPackageException(
					"Failed to query for single result. SQL: " + sql);
		}

		return result;
	}

	/**
	 * Query the SQL for a single result object with the expected data type
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @return result, null if no result
	 * @since 3.1.0
	 */
	public static Object querySingleResult(Connection connection, String sql,
			String[] args, int column, GeoPackageDataType dataType) {
		ResultSetResult result = wrapQuery(connection, sql, args);
		Object value = ResultUtils.buildSingleResult(result, column, dataType);
		return value;
	}

	/**
	 * Query for values from a single column up to the limit
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @param limit
	 *            result row limit
	 * @return single column results
	 * @since 3.1.0
	 */
	public static List<Object> querySingleColumnResults(Connection connection,
			String sql, String[] args, int column, GeoPackageDataType dataType,
			Integer limit) {
		ResultSetResult result = wrapQuery(connection, sql, args);
		List<Object> results = ResultUtils.buildSingleColumnResults(result,
				column, dataType, limit);
		return results;
	}

	/**
	 * Query for values up to the limit
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @param limit
	 *            result row limit
	 * @return results
	 * @since 3.1.0
	 */
	public static List<List<Object>> queryResults(Connection connection,
			String sql, String[] args, GeoPackageDataType[] dataTypes,
			Integer limit) {
		ResultSetResult result = wrapQuery(connection, sql, args);
		List<List<Object>> results = ResultUtils.buildResults(result,
				dataTypes, limit);
		return results;
	}

	/**
	 * Execute a deletion
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return deleted count
	 */
	public static int delete(Connection connection, String table, String where,
			String[] args) {
		StringBuilder delete = new StringBuilder();
		delete.append("delete from ").append(CoreSQLUtils.quoteWrap(table));
		if (where != null) {
			delete.append(" where ").append(where);
		}
		String sql = delete.toString();

		PreparedStatement statement = null;

		int count = 0;
		try {
			statement = connection.prepareStatement(sql);
			setArguments(statement, args);
			count = statement.executeUpdate();
		} catch (SQLException e) {
			throw new GeoPackageException(
					"Failed to execute SQL delete statement: " + sql, e);
		} finally {
			closeStatement(statement, sql);
		}

		return count;
	}

	/**
	 * Update table rows
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param values
	 *            content values
	 * @param whereClause
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return updated count
	 */
	public static int update(Connection connection, String table,
			ContentValues values, String whereClause, String[] whereArgs) {

		StringBuilder update = new StringBuilder();
		update.append("update ").append(CoreSQLUtils.quoteWrap(table))
				.append(" set ");

		int setValuesSize = values.size();
		int argsSize = (whereArgs == null) ? setValuesSize
				: (setValuesSize + whereArgs.length);
		Object[] args = new Object[argsSize];
		int i = 0;
		for (String colName : values.keySet()) {
			update.append((i > 0) ? "," : "");
			update.append(CoreSQLUtils.quoteWrap(colName));
			args[i++] = values.get(colName);
			update.append("=?");
		}
		if (whereArgs != null) {
			for (i = setValuesSize; i < argsSize; i++) {
				args[i] = whereArgs[i - setValuesSize];
			}
		}
		if (whereClause != null) {
			update.append(" WHERE ");
			update.append(whereClause);
		}
		String sql = update.toString();

		PreparedStatement statement = null;

		int count = 0;
		try {
			statement = connection.prepareStatement(sql);
			setArguments(statement, args);
			count = statement.executeUpdate();
		} catch (SQLException e) {
			throw new GeoPackageException(
					"Failed to execute SQL update statement: " + sql, e);
		} finally {
			closeStatement(statement, sql);
		}

		return count;
	}

	/**
	 * Insert a new row
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param values
	 *            content values
	 * @return row id or -1 on an exception
	 */
	public static long insert(Connection connection, String table,
			ContentValues values) {
		try {
			return insertOrThrow(connection, table, values);
		} catch (Exception e) {
			log.log(Level.WARNING, "Error inserting into table: " + table
					+ ", Values: " + values, e);
			return -1;
		}
	}

	/**
	 * Insert a new row
	 * 
	 * @param connection
	 *            connection
	 * @param table
	 *            table name
	 * @param values
	 *            content values
	 * @return row id
	 */
	public static long insertOrThrow(Connection connection, String table,
			ContentValues values) {

		StringBuilder insert = new StringBuilder();
		insert.append("insert into ").append(CoreSQLUtils.quoteWrap(table))
				.append("(");

		Object[] args = null;
		int size = (values != null && values.size() > 0) ? values.size() : 0;

		args = new Object[size];
		int i = 0;
		for (String colName : values.keySet()) {
			insert.append((i > 0) ? "," : "");
			insert.append(CoreSQLUtils.quoteWrap(colName));
			args[i++] = values.get(colName);
		}
		insert.append(')');
		insert.append(" values (");
		for (i = 0; i < size; i++) {
			insert.append((i > 0) ? ",?" : "?");
		}
		insert.append(')');

		String sql = insert.toString();

		PreparedStatement statement = null;

		long id = 0;
		try {
			statement = connection.prepareStatement(sql);
			setArguments(statement, args);
			int count = statement.executeUpdate();

			if (count == 0) {
				throw new GeoPackageException(
						"Failed to execute SQL insert statement: " + sql
								+ ". No rows added from execution.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					id = generatedKeys.getLong(1);
				} else {
					throw new GeoPackageException(
							"Failed to execute SQL insert statement: " + sql
									+ ". No row id was found.");
				}
			}
		} catch (SQLException e) {
			throw new GeoPackageException(
					"Failed to execute SQL insert statement: " + sql, e);
		} finally {
			closeStatement(statement, sql);
		}

		return id;
	}

	/**
	 * Set the prepared statement arguments
	 * 
	 * @param statement
	 *            prepared statement
	 * @param selectionArgs
	 *            selection arguments
	 * @throws SQLException
	 *             upon failure
	 */
	public static void setArguments(PreparedStatement statement,
			Object[] selectionArgs) throws SQLException {
		if (selectionArgs != null) {
			for (int i = 0; i < selectionArgs.length; i++) {
				statement.setObject(i + 1, selectionArgs[i]);
			}
		}
	}

	/**
	 * Close the statement
	 * 
	 * @param statement
	 *            statement
	 * @param sql
	 *            sql statement
	 */
	public static void closeStatement(Statement statement, String sql) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				log.log(Level.WARNING, "Failed to close SQL Statement: " + sql,
						e);
			}
		}
	}

	/**
	 * Close the ResultSet
	 * 
	 * @param resultSet
	 *            result set
	 * @param sql
	 *            sql statement
	 */
	public static void closeResultSet(ResultSet resultSet, String sql) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.log(Level.WARNING, "Failed to close SQL ResultSet: " + sql,
						e);
			}
		}
	}

	/**
	 * Close the ResultSet Statement from which it was created, which closes all
	 * ResultSets as well
	 * 
	 * @param resultSet
	 *            result set
	 * @param sql
	 *            sql statement
	 */
	public static void closeResultSetStatement(ResultSet resultSet, String sql) {
		if (resultSet != null) {
			try {
				resultSet.getStatement().close();
			} catch (SQLException e) {
				log.log(Level.WARNING, "Failed to close SQL ResultSet: " + sql,
						e);
			}
		}
	}

	/**
	 * Perform the query and wrap as a result
	 * 
	 * @param connection
	 *            connection
	 * @param sql
	 *            sql statement
	 * @param selectionArgs
	 *            selection arguments
	 * @return result
	 * @since 3.1.0
	 */
	public static ResultSetResult wrapQuery(Connection connection, String sql,
			String[] selectionArgs) {
		return new ResultSetResult(query(connection, sql, selectionArgs));
	}

	/**
	 * Begin a transaction for the connection
	 * 
	 * @param connection
	 *            connection
	 * @return pre-transaction auto commit value
	 * @since 3.3.0
	 */
	public static boolean beginTransaction(Connection connection) {
		boolean autoCommit;
		try {
			autoCommit = connection.getAutoCommit();
			if (autoCommit) {
				connection.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new GeoPackageException("Failed to begin transaction", e);
		}
		return autoCommit;
	}

	/**
	 * End a transaction for the connection
	 * 
	 * @param connection
	 *            connection
	 * @param successful
	 *            true to commit, false to rollback
	 * @since 3.3.0
	 */
	public static void endTransaction(Connection connection, boolean successful) {
		endTransaction(connection, successful, null);
	}

	/**
	 * End a transaction for the connection
	 * 
	 * @param connection
	 *            connection
	 * @param successful
	 *            true to commit, false to rollback
	 * @param autoCommit
	 *            pre-transaction auto commit value
	 * @since 3.3.0
	 */
	public static void endTransaction(Connection connection,
			boolean successful, Boolean autoCommit) {
		try {
			if (successful) {
				connection.commit();
			} else {
				connection.rollback();
			}
			if (autoCommit != null && autoCommit) {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new GeoPackageException("Failed to end transaction", e);
		}
	}

}
