package de.rwth.idsg.steve.common;

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

import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;


/**
 * This class has helper methods for database access that are used by the OCPP client, or the HTTP servlet.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
public class ClientDBAccess {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientDBAccess.class);

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
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}
	}
	
	public static synchronized List<String> getChargePoints() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId FROM chargebox");
			rs = pt.executeQuery();
			
			List<String> list = new ArrayList<String>();
			while (rs.next()) { list.add(rs.getString(1)); }
			
			return list;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void addChargePoint(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("INSERT IGNORE INTO chargebox (chargeBoxId) VALUES (?)");
			pt.setString(1, chargeBoxId);
			pt.executeUpdate();
		
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void deleteChargePoint(String chargeBoxId) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
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
			Utils.releaseResources(connect, pt, null);
		}
	}
		
	public static synchronized void addUser(String idTag, String parentIdTag, Timestamp expiryTimestamp) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("INSERT IGNORE INTO user (idTag, parentIdTag, expiryDate) VALUES (?,?,?)");
			pt.setString(1, idTag);
			pt.setString(2, parentIdTag);
			pt.setTimestamp(3, expiryTimestamp);			
			pt.executeUpdate();
			
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void deleteUser(String idTag) {
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
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
			Utils.releaseResources(connect, pt, null);
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
			connect = Utils.getConnectionFromPool();
			
			// Check overlapping
			isOverlapping(connect, pt, startTimestamp, expiryTimestamp, chargeBoxId);
			
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
			Utils.releaseResources(connect, pt, null);
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
			connect = Utils.getConnectionFromPool();
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
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	/**
	 * For OCPP 1.5: Helper method to read idTags from the DB for the operation SendLocalList.
	 * 
	 */
	public static synchronized ArrayList<AuthorisationData> getIdTags(ArrayList<String> inputList) {
		
		XMLGregorianCalendar xcal = Utils.setExpiryDateTime(Constants.HOURS_TO_EXPIRE);
		Timestamp now = Utils.getCurrentDateTimeTS();
		
		ArrayList<AuthorisationData> list = new ArrayList<AuthorisationData>();
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			
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
			Utils.releaseResources(connect, pt, rs);
		}
		return list;
	}	
	
	/**
	 * Returns DB version of SteVe
	 * 
	 */
	public static synchronized String getDBVersion() {
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT version FROM dbVersion");
			rs = pt.executeQuery();
			
			String ver = null;
			if (rs.next()) ver = rs.getString(1);
			
			return ver;
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
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
			Utils.releaseResources(null, pt, rs);
		}
	}
}