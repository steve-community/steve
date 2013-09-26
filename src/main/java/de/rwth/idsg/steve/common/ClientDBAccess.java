package de.rwth.idsg.steve.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.html.Common;
import de.rwth.idsg.steve.html.InputException;

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
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
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
				throw new InputException(Common.EXCEPTION_INVALID_DATETIME);
			}
		} else {
			DateTime now = new DateTime();
			// Continue only if: now < startDatetime < expiryDatetime
			if ( !(now.isBefore(startDatetime) && startDatetime.isBefore(expiryDatetime)) ) {
				throw new InputException(Common.EXCEPTION_INVALID_DATETIME);
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
			isOverlapping(connect, pt, startTimestamp, expiryTimestamp);
			
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
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
		return reservationId;
	}
	
	/**
	 * Ends a reservation
	 *
	 */
	public static synchronized boolean endReservation(int reservation_pk) {
		
		boolean ended = false;
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("UPDATE reservation SET ended = 1 WHERE reservation_pk=?");

			// Set the parameter indices 
			pt.setInt(1, reservation_pk);
			// Execute the query
			int count = pt.executeUpdate();
			// Validate the change
			if (count == 1) {
				connect.commit();
				ended = true;
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
		return ended;
	}

	/**
	 * Returns true, if the reservation is canceled.
	 *
	 */
	public static synchronized boolean cancelReservation(int reservation_pk) {

		boolean canceled = false;
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
				canceled = true;
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
		return canceled;
	}
	
	/**
	 * Throws exception, if there are rows whose date/time ranges overlap with the input
	 *
	 */
	private static void isOverlapping(Connection connect, PreparedStatement pt, Timestamp start, Timestamp stop) {
		
		ResultSet rs = null;
		try {
			// This WHERE clause covers all three cases
			pt = connect.prepareStatement("SELECT 1 FROM reservation WHERE ? <= expiryDatetime AND ? >= startDatetime");
			pt.setTimestamp(1, start);
			pt.setTimestamp(2, stop);

			rs = pt.executeQuery();
			// If the result set does have an entry, then there are overlaps
			if ( rs.next() ) {
				throw new InputException(Common.EXCEPTION_OVERLAPPING_RESERVATION);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(null, pt, rs);
		}
	}

}
