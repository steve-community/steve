package de.rwth.idsg.steve.common.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class DBUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);
	private static DataSource dataSource = null;

	/**
	 * Returns a connection from the JDBC pool.
	 */
	public static Connection getConnectionFromPool() {
		Connection con = null;
		try {
			if (dataSource == null) {
				Context initContext = new InitialContext();
				dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/stevedb");	
				LOG.info("Data source is retrieved.");
			}
			con = dataSource.getConnection();
		} catch (Exception e) {
			LOG.error("SQL exception", e);
		}
		return con;
	}	

	/**
	 * Validates the BATCH execution of Data Manipulation Language (DML) statements, such as INSERT, UPDATE or DELETE.
	 * 
	 * If the row value in the updateCounts array is 0 or greater, the update was successfully executed.
	 * A value of SUCCESS_NO_INFO means update was successfully executed, but MySQL server unable to determine the number of rows affected.
	 * A value of EXECUTE_FAILED means that an error has occured.
	 */
	public static boolean validateDMLChanges(int [] updateCounts) {
		boolean updatedAll = false;

		for (int i = 0; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 1) {
				updatedAll = true;
			} else if (updateCounts[i] == (PreparedStatement.SUCCESS_NO_INFO | PreparedStatement.EXECUTE_FAILED)) {
				updatedAll = false;
				break;
			}
		}
		return updatedAll;
	}

	/**
	 * Releases all resources and returns the DB connection back to the pool.
	 */
	public static void releaseResources(Connection con, PreparedStatement pt, ResultSet rs) {
		try {
			if (rs != null) rs.close();
			if (pt != null) pt.close();
			if (con != null) con.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}