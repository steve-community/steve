package de.rwth.idsg.steve.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ClientDBAccess {

	public static synchronized HashMap<String,String> getChargePoints(String ocppVersion){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId, endpoint_address FROM chargebox WHERE ocppVersion=?");
			pt.setString(1, ocppVersion);
			rs = pt.executeQuery();

			HashMap<String,String> results = new HashMap<String,String>();
			while (rs.next()) { results.put(rs.getString(1), rs.getString(2));	}

			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}
	}
}
