package de.rwth.idsg.steve.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2012._06.AuthorizationStatus;
import ocpp.cp._2012._06.IdTagInfo;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.utils.DBUtils;
import de.rwth.idsg.steve.common.utils.DateTimeUtils;
import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;
import de.rwth.idsg.steve.model.ChargePoint;
import de.rwth.idsg.steve.model.ConnectorStatus;
import de.rwth.idsg.steve.model.Heartbeat;
import de.rwth.idsg.steve.model.Reservation;
import de.rwth.idsg.steve.model.Statistics;
import de.rwth.idsg.steve.model.Transaction;
import de.rwth.idsg.steve.model.User;


/**
 * This class has helper methods for database access that are used by the OCPP client, or the HTTP servlet.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
public class ClientDBAccess {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientDBAccess.class);
	
	
	public static Statistics getStats(){		
		Connection connect = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			// getStats is the stored procedure in our MySQL DB
			cs = connect.prepareCall("{CALL getStats(?,?,?,?,?,?,?,?,?,?,?)}");
			
			for (int i=1; i<=11; i++) {
				cs.registerOutParameter(i, java.sql.Types.INTEGER);
			}
			cs.execute();
			
			Statistics stat = new Statistics(
					cs.getInt(1),
					cs.getInt(2),
					cs.getInt(3),
					cs.getInt(4),
					cs.getInt(5),
					cs.getInt(6),
					cs.getInt(7),
					cs.getInt(8),
					cs.getInt(9),
					cs.getInt(10),
					cs.getInt(11));				
				
			return stat;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, cs, rs);
		}
	}

	public static HashMap<String,String> getChargePoints(String ocppVersion){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId, endpoint_address FROM chargebox WHERE ocppVersion=?");
			pt.setString(1, ocppVersion);
			rs = pt.executeQuery();

			HashMap<String,String> results = new HashMap<String,String>();
			while (rs.next()) { results.put(rs.getString(1), rs.getString(2));	}

			return results;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	public static List<String> getChargePoints() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId FROM chargebox");
			rs = pt.executeQuery();
			
			List<String> list = new ArrayList<String>();
			while (rs.next()) { list.add(rs.getString(1)); }
			
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static ChargePoint getChargePointDetails(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;		
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM chargebox WHERE chargeBoxId=?;");
			pt.setString(1, chargeBoxId);
			rs = pt.executeQuery();
			
			ChargePoint cp = null;
			if ( rs.next() ) {				
				cp = new ChargePoint(
						rs.getString(1), 
						rs.getString(2), 
						rs.getString(3), 
						rs.getString(4),
						rs.getString(5), 
						rs.getString(6), 
						rs.getString(7), 
						rs.getString(8), 
						rs.getString(9),
						DateTimeUtils.humanize(rs.getTimestamp(10)),
						rs.getString(11), 
						rs.getString(12), 
						rs.getString(13), 
						rs.getString(14), 
						rs.getString(15),
						DateTimeUtils.humanize(rs.getTimestamp(16)),
						DateTimeUtils.humanize(rs.getTimestamp(17)));
			}
			return cp;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}		
	}	
	
	public static List<Heartbeat> getChargePointHeartbeats() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId, lastHeartbeatTimestamp FROM chargebox ORDER BY lastHeartbeatTimestamp DESC;");
			rs = pt.executeQuery();

			List<Heartbeat> list = new ArrayList<Heartbeat>();
			while ( rs.next() ) {
				Heartbeat hb = new Heartbeat(
						rs.getString(1),
						DateTimeUtils.humanize(rs.getTimestamp(2)));

				list.add(hb);
			}
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	public static List<ConnectorStatus> getChargePointConnectorStatus() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT c.chargeBoxId, c.connectorId, cs.statustimeStamp, cs.status, cs.errorCode "
					+ "FROM connector_status cs "
					+ "INNER JOIN connector c ON cs.connector_pk = c.connector_pk "
					+ "INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk) AS t1 ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max "
					+ "ORDER BY cs.statustimeStamp DESC;");
			rs = pt.executeQuery();

			List<ConnectorStatus> list = new ArrayList<ConnectorStatus>();
			while ( rs.next() ) {
				ConnectorStatus cs = new ConnectorStatus(
						rs.getString(1),
						rs.getInt(2),
						DateTimeUtils.humanize(rs.getTimestamp(3)),
						rs.getString(4),
						rs.getString(5));

				list.add(cs);
			}
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	public static synchronized void addChargePoint(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("INSERT IGNORE INTO chargebox (chargeBoxId) VALUES (?)");
			pt.setString(1, chargeBoxId);
			pt.executeUpdate();
		
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void deleteChargePoint(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			connect.setAutoCommit(false);
			
			pt = connect.prepareStatement("DELETE FROM chargebox WHERE chargeBoxId=?");
			pt.setString(1, chargeBoxId);			
			int count = pt.executeUpdate();
			
			// Validate the change
			if (count == 1) {
				connect.commit();
				LOG.info("The charge point with chargeBoxId {} is deleted.", chargeBoxId);
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
				LOG.info("The charge point with chargeBoxId {} could NOT be deleted.", chargeBoxId);
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static List<Integer> getConnectorIds(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT connectorId FROM connector WHERE chargeBoxId = ?");
			pt.setString(1, chargeBoxId);
			rs = pt.executeQuery();
			
			List<Integer> connList = new ArrayList<Integer>();
			while (rs.next()) { connList.add(rs.getInt(1)); }
			
			return connList;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static List<User> getUsers(){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM user;");
			rs = pt.executeQuery();

			List<User> userList = new ArrayList<User>();
			while ( rs.next() ) {				
				User user = new User(
						rs.getString(1), 
						rs.getString(2),
						DateTimeUtils.humanize(rs.getTimestamp(3)),
						rs.getBoolean(4), 
						rs.getBoolean(5));

				userList.add(user);
			}
			return userList;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);	
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}

	public static synchronized void updateUser(String idTag, String parentIdTag, Timestamp expiryTimestamp, boolean blockUser) {		
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			connect.setAutoCommit(false);
			
			pt = connect.prepareStatement("UPDATE user SET parentIdTag = ?, expiryDate = ?, blocked = ? WHERE idTag = ?");
			pt.setString(1, parentIdTag);
			pt.setTimestamp(2, expiryTimestamp);
			pt.setBoolean(3, blockUser);
			pt.setString(4, idTag);
			
			int count = pt.executeUpdate();			
			// Validate the change
			if (count == 1) {
				connect.commit();
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}		
	}
			
	public static synchronized void addUser(String idTag, String parentIdTag, Timestamp expiryTimestamp) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("INSERT IGNORE INTO user (idTag, parentIdTag, expiryDate) VALUES (?,?,?)");
			pt.setString(1, idTag);
			pt.setString(2, parentIdTag);
			pt.setTimestamp(3, expiryTimestamp);			
			pt.executeUpdate();
			
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void deleteUser(String idTag) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			connect.setAutoCommit(false);
			
			pt = connect.prepareStatement("DELETE FROM user WHERE idTag=?");
			pt.setString(1, idTag);			
			int count = pt.executeUpdate();
			
			// Validate the change
			if (count == 1) {
				connect.commit();
				LOG.info("The user with idTag {} is deleted.", idTag);
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
				LOG.info("The user with idTag {} could NOT be deleted.", idTag);
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	
	public static List<Reservation> getReservations(){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM reservation WHERE expiryDatetime >= NOW() ORDER BY expiryDatetime;");
			rs = pt.executeQuery();

			List<Reservation> reservList = new ArrayList<Reservation>();
			while ( rs.next() ) {
				Reservation res = new Reservation(
						rs.getInt(1),
						rs.getString(2), 
						rs.getString(3),
						DateTimeUtils.humanize(rs.getTimestamp(4)),
						DateTimeUtils.humanize(rs.getTimestamp(5)));

				reservList.add(res);
			}
			return reservList;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);	
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	public static List<Integer> getExistingReservationIds(String chargeBoxId){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT reservation_pk FROM reservation WHERE chargeBoxId=? AND expiryDatetime >= NOW()");
			
			pt.setString(1, chargeBoxId);
			rs = pt.executeQuery();

			List<Integer> list = new ArrayList<Integer>();
			while (rs.next()) { list.add(rs.getInt(1)); }
			
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	/**
	 * Returns the id of the reservation, if the reservation is booked.
	 *
	 */
	public static synchronized int bookReservation(String idTag, String chargeBoxId, DateTime startDatetime, DateTime expiryDatetime) {

		// Check the dates first
		// startDatetime can be null, if the reservation starts from current time on
		if (startDatetime == null) {
			startDatetime = new DateTime();
			// Continue only if: startDatetime < expiryDatetime
			if ( startDatetime.isAfter(expiryDatetime) ) {
				throw new InputException(ExceptionMessage.INVALID_DATETIME);
			}
		} else {
			DateTime now = new DateTime();
			// Continue only if: now < startDatetime < expiryDatetime
			if ( !(now.isBefore(startDatetime) && startDatetime.isBefore(expiryDatetime)) ) {
				throw new InputException(ExceptionMessage.INVALID_DATETIME);
			}
		}
		
		Timestamp startTimestamp = new Timestamp(startDatetime.getMillis());
		Timestamp expiryTimestamp = new Timestamp(expiryDatetime.getMillis());
		
		// Initialize with an invalid id
		int reservationId = -1;
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			
			// Check overlapping
			//isOverlapping(connect, pt, startTimestamp, expiryTimestamp, chargeBoxId);
			
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("INSERT INTO reservation (idTag, chargeBoxId, startDatetime, expiryDatetime) VALUES (?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);

			// Set the parameter indices  
			pt.setString(1, idTag);
			pt.setString(2, chargeBoxId);
			pt.setTimestamp(3, startTimestamp);
			pt.setTimestamp(4, expiryTimestamp);
			// Insert the new status
			int count = pt.executeUpdate();
			
			// Validate the change
			if (count == 1) {
				// Get the generated key in order to obtain the auto-incremented reservation_pk
				ResultSet rs = pt.getGeneratedKeys();
				if (rs.next()) {
					reservationId = rs.getInt(1); // transaction_pk is the 1. column
				}
				connect.commit();
				LOG.info("A new reservation {} is booked.", reservationId);
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
		return reservationId;
	}

	/**
	 * Cancels a reservation.
	 *
	 */
	public static synchronized void cancelReservation(int reservation_pk) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("DELETE FROM reservation WHERE reservation_pk=?");

			// Set the parameter indices  
			pt.setInt(1, reservation_pk);
			// Execute the query
			int count = pt.executeUpdate();
			// Validate the change
			if (count == 1) {
				connect.commit();
				LOG.info("The reservation {} is canceled.", reservation_pk);
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
				LOG.info("The reservation {} could NOT be canceled.", reservation_pk);
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	public static List<Transaction> getTransactions() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT transaction.transaction_pk, connector.chargeBoxId, connector.connectorId, transaction.idTag, "
					+ "transaction.startTimestamp, transaction.startValue,  transaction.stopTimestamp, transaction.stopValue "
					+ "FROM transaction JOIN connector ON transaction.connector_pk = connector.connector_pk;");
			rs = pt.executeQuery();

			List<Transaction> list = new ArrayList<Transaction>();
			while ( rs.next() ) {
								
				String chargedValue = "";				
				int stopValue = rs.getInt(8);
				if (stopValue != 0) {
					// rs.getInt(6) is the start value
					chargedValue = String.valueOf(stopValue - rs.getInt(6));
				}
				
				Transaction ta = new Transaction(
						rs.getInt(1),
						rs.getString(2),
						rs.getInt(3),
						rs.getString(4),
						DateTimeUtils.humanize(rs.getTimestamp(5)),
						DateTimeUtils.humanize(rs.getTimestamp(7)),
						chargedValue);

				list.add(ta);
			}
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	public static List<Integer> getActiveTransactionIds(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT transaction.transaction_pk FROM transaction "
					+ "JOIN connector ON transaction.connector_pk = connector.connector_pk "
					+ "WHERE chargeBoxId = ? AND stopTimestamp IS NULL");
			
			pt.setString(1, chargeBoxId);
			rs = pt.executeQuery();

			List<Integer> list = new ArrayList<Integer>();
			while (rs.next()) { list.add(rs.getInt(1)); }
			
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
	}
	
	/**
	 * For OCPP 1.5: Helper method to read idTags from the DB for the operation SendLocalList.
	 * 
	 */
	public static ArrayList<AuthorisationData> getIdTags(ArrayList<String> inputList) {
		
		XMLGregorianCalendar xcal = DateTimeUtils.setExpiryDateTime(Constants.HOURS_TO_EXPIRE);
		Timestamp now = DateTimeUtils.getCurrentDateTimeTS();
		
		ArrayList<AuthorisationData> list = new ArrayList<AuthorisationData>();
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			
			if (inputList == null) {
				// Read ALL idTags
				pt = connect.prepareStatement("SELECT * FROM user");
				
			} else {
				// Read only ENTERED idTags
				pt = connect.prepareStatement("SELECT * FROM user WHERE idTag = ?");				
				for (String inputIdTag : inputList){
					pt.setString(1, inputIdTag);
					pt.addBatch();
				}
			}

			// Execute and get the result of the SQL query
			rs = pt.executeQuery();

			while (rs.next()) {
				// Read the DB row values
				String idTag = rs.getString(1);
				String parentIdTag = rs.getString(2);
				Timestamp expiryDate = rs.getTimestamp(3);
				boolean inTransaction = rs.getBoolean(4);
				boolean blocked = rs.getBoolean(5);

				// Create IdTagInfo of an idTag
				IdTagInfo _returnIdTagInfo = new IdTagInfo();				
				AuthorizationStatus _returnIdTagInfoStatus = null;

				if (inTransaction == true) {
					_returnIdTagInfoStatus = AuthorizationStatus.CONCURRENT_TX;
					
				} else if (blocked == true) {
					_returnIdTagInfoStatus = AuthorizationStatus.BLOCKED;

				} else if (expiryDate != null && now.after(expiryDate)) {
					_returnIdTagInfoStatus = AuthorizationStatus.EXPIRED;

				} else {
					_returnIdTagInfoStatus = AuthorizationStatus.ACCEPTED;
					// When accepted, set the additional fields
					_returnIdTagInfo.setExpiryDate(xcal);
					if ( parentIdTag != null ) _returnIdTagInfo.setParentIdTag(parentIdTag);
				}
				_returnIdTagInfo.setStatus(_returnIdTagInfoStatus);
				
				// Put the information into the list			
				AuthorisationData item = new AuthorisationData();
				item.setIdTag(idTag);
				item.setIdTagInfo(_returnIdTagInfo);
				list.add(item);
			}
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			DBUtils.releaseResources(connect, pt, rs);
		}
		return list;
	}	
	
	/**
	 * Returns DB version of SteVe
	 * 
	 */
	public static String[] getDBVersion() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try { 
			// Prepare Database Access
			connect = DBUtils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM dbVersion");
			rs = pt.executeQuery();
			
			String[] ver = new String[2];
			if (rs.next()) {
				ver[0] = rs.getString(1);
				ver[1] = DateTimeUtils.humanize(rs.getTimestamp(2));
			}
			
			return ver;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			DBUtils.releaseResources(connect, pt, null);
		}
	}
	
	/**
	 * Throws exception, if there are rows whose date/time ranges overlap with the input
	 * @param chargeBoxId 
	 *
	 */
	private static void isOverlapping(Connection connect, PreparedStatement pt, Timestamp start, Timestamp stop, String chargeBoxId) {
		
		ResultSet rs = null;
		try {
			// This WHERE clause covers all three cases
			pt = connect.prepareStatement("SELECT 1 FROM reservation WHERE ? <= expiryDatetime AND ? >= startDatetime AND chargeBoxId = ?");
			pt.setTimestamp(1, start);
			pt.setTimestamp(2, stop);
			pt.setString(3, chargeBoxId);

			rs = pt.executeQuery();
			// If the result set does have an entry, then there are overlaps
			if ( rs.next() ) {
				throw new InputException(ExceptionMessage.OVERLAPPING_RESERVATION);
			}
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			DBUtils.releaseResources(null, pt, rs);
		}
	}
}