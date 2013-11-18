package de.rwth.idsg.steve.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class has helper methods for database access that are used by the OCPP service.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
public class ServiceDBAccess {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceDBAccess.class);
	
	public static synchronized boolean updateChargebox(
			String endpoint_address,
			String ocppVersion,
			String vendor,
			String model,
			String pointSerial,
			String boxSerial,
			String fwVersion,
			String iccid,
			String imsi,
			String meterType,
			String meterSerial,
			String chargeBoxIdentity) {
		
		boolean isRegistered = false;
		
		Connection connect = null;
		PreparedStatement pt = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);

			// One DB call with two functions:
			//
			// 1. Update all fields with the exception of chargeBoxId, so that initially it is enough to register 
			// a chargebox with its ID in DB. During boot, the chargebox provides missing information which might be updated (for ex: firmware)
			//
			// 2. If the chargebox not registered => no chargeboxes to update => updated/returned row count = 0
			pt = connect.prepareStatement("UPDATE chargebox SET endpoint_address=?, ocppVersion=?, chargePointVendor=?, chargePointModel=?,"
					+ "chargePointSerialNumber=?, chargeBoxSerialNumber=?, fwVersion=?, "
					+ "iccid=?, imsi=?, meterType=?, meterSerialNumber=? WHERE chargeBoxId = ?");

			// Set the parameter indices
			pt.setString(1, endpoint_address);
			pt.setString(2, ocppVersion);
			pt.setString(3, vendor);
			pt.setString(4, model);
			pt.setString(5, pointSerial);
			pt.setString(6, boxSerial);
			pt.setString(7, fwVersion);
			pt.setString(8, iccid);
			pt.setString(9, imsi);
			pt.setString(10, meterType);
			pt.setString(11, meterSerial);
			pt.setString(12, chargeBoxIdentity);

			// Execute the SQL query
			int count = pt.executeUpdate();
						
			// Validate the change
			if (count == 1) {
				connect.commit();
				LOG.info("The chargebox {} is registered and its boot acknowledged.", chargeBoxIdentity);
				isRegistered = true;
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
				LOG.error("The chargebox {} is NOT registered and its boot NOT acknowledged.", chargeBoxIdentity);
			}
			connect.setAutoCommit(true);						
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);	
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
		return isRegistered;
	}
	
	public static synchronized void updateChargeboxFirmwareStatus(
			String chargeBoxIdentity, 
			String firmwareStatus){
		
		Connection connect = null;
		PreparedStatement pt = null;		
		try {		
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);

			// PreparedStatements can use parameter indices as question marks
			pt = connect.prepareStatement("UPDATE chargebox SET fwUpdateStatus = ?, fwUpdateTimestamp = ? WHERE chargeBoxId = ?");

			// Set the parameter indices
			pt.setString(1, firmwareStatus);
			pt.setTimestamp(2, Utils.getCurrentDateTimeTS());
			pt.setString(3, chargeBoxIdentity);

			// Update the Firmware Status of the Chargebox with the new one
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
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}	
	
	public static synchronized void updateChargeboxDiagnosticsStatus(
			String chargeBoxIdentity, 
			String status){
		
		Connection connect = null;
		PreparedStatement pt = null;
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);
			
			// PreparedStatements can use parameter indices as question marks
			pt = connect.prepareStatement("UPDATE chargebox SET diagnosticsStatus = ?, diagnosticsTimestamp = ? WHERE chargeBoxId = ?");
			// Set the parameter indices
			pt.setString(1, status);
			pt.setTimestamp(2, Utils.getCurrentDateTimeTS());
			pt.setString(3, chargeBoxIdentity);
			// Perform update
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
		}  finally {
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void insertConnectorStatus(
			String chargeBoxIdentity, 
			int connectorId, 
			String status, 
			Timestamp timeStamp,
			String errorCode,
			String errorInfo,			
			String vendorId,
			String vendorErrorCode){
		
		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);

			// For the first boot of the chargebox: Insert its connectors in DB
			// For next boots: IGNORE.
			pt = connect.prepareStatement("INSERT IGNORE INTO connector (chargeBoxId, connectorId) VALUES (?,?)");

			// Set the parameter indices  
			pt.setString(1, chargeBoxIdentity);
			pt.setInt(2, connectorId); 
			// Insert the new status
			int count = pt.executeUpdate();
			// Validate the change
			if (count >= 1) {
				LOG.info("The connector {}/{} is NEW, and inserted into DB.", chargeBoxIdentity, connectorId);
			}else{
				LOG.info("The connector {}/{} is ALREADY known to DB.", chargeBoxIdentity, connectorId);
			}

			Utils.releaseResources(null, pt, null);

			// We store a log of connector statuses
			pt = connect.prepareStatement("INSERT INTO connector_status (connector_pk, statusTimestamp, status, errorCode, errorInfo, vendorId, vendorErrorCode) "
					+ "SELECT connector_pk , ? , ? , ? , ? , ? , ? FROM connector WHERE chargeBoxId = ? AND connectorId = ?");

			// Set the parameter indices             			
			if (timeStamp == null ) {
				pt.setTimestamp(1, Utils.getCurrentDateTimeTS());
			} else {
				pt.setTimestamp(1, timeStamp);
			}
			pt.setString(2, status);
			pt.setString(3, errorCode);    
			pt.setString(4, errorInfo);
			pt.setString(5, vendorId);
			pt.setString(6, vendorErrorCode); 			
			pt.setString(7, chargeBoxIdentity);
			pt.setInt(8, connectorId); 

			// Insert the new status
			count = pt.executeUpdate();	
			// Validate the change
			if (count >= 1) {
				// Now we can commit everything
				connect.commit();
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
	}
	
	public static synchronized void insertMeterValues12(
			String chargeBoxIdentity, 
			int connectorId, 
			List<ocpp.cs._2010._08.MeterValue> list){

		Connection connect = null;
		PreparedStatement pt = null;	
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			// Disable the auto commit since we are making batch execution. By default, it is always true.
			connect.setAutoCommit(false);
			
			// Select the connector primary key
			pt = connect.prepareStatement("SELECT connector_pk FROM connector WHERE chargeBoxId = ? AND connectorId = ?");
			pt.setString(1, chargeBoxIdentity);
			pt.setInt(2, connectorId);
			ResultSet rs = pt.executeQuery();
			int connector_pk = -1;
			if (rs.next() == true) connector_pk = rs.getInt(1); else throw new SQLException();
			Utils.releaseResources(null, pt, rs);
			
			// We store a log of connector meter values with their timestamps.
			pt = connect.prepareStatement("INSERT INTO connector_metervalue (connector_pk, valueTimestamp, value) VALUES (?,?,?)");

			// OCPP 1.2 allows multiple "values" elements
			for(ocpp.cs._2010._08.MeterValue valuesElement : list){
				// Set the parameter indices for batch execution
				pt.setInt(1, connector_pk);
				pt.setTimestamp(2, Utils.convertToTimestamp(valuesElement.getTimestamp()));
				pt.setString(3, String.valueOf(valuesElement.getValue()));	
				pt.addBatch();
			}

			// Execute the batch.
			int[] count = pt.executeBatch();
			// Validate the change
			if (Utils.validateDMLChanges(count)) {
				// Now we can commit everything
				connect.commit();
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}

	public static synchronized void insertMeterValues15(
			String chargeBoxIdentity, 
			int connectorId,
			Integer transactionId,
			List<ocpp.cs._2012._06.MeterValue> list){
		
		Connection connect = null;
		PreparedStatement pt = null;
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			// Disable the auto commit since we are making batch execution. By default, it is always true.
			connect.setAutoCommit(false);
			
			// Select the connector primary key
			pt = connect.prepareStatement("SELECT connector_pk FROM connector WHERE chargeBoxId = ? AND connectorId = ?");
			pt.setString(1, chargeBoxIdentity);
			pt.setInt(2, connectorId);
			ResultSet rs = pt.executeQuery();
			int connector_pk = -1;
			if (rs.next() == true) connector_pk = rs.getInt(1); else throw new SQLException();
			Utils.releaseResources(null, pt, rs);
			
			// We store a log of connector meter values with their timestamps.
			pt = connect.prepareStatement("INSERT INTO connector_metervalue (connector_pk, transaction_pk, valueTimestamp, value, readingContext, format, measurand, location, unit) "
					+ "VALUES (?,?,?,?,?,?,?,?,?)");
			
			// if transactionId is NOT present, write NULL to the field ...
			if (transactionId == null){	

				// OCPP 1.5 allows multiple "values" elements
				for (ocpp.cs._2012._06.MeterValue valuesElement : list) {
					Timestamp timestamp = Utils.convertToTimestamp(valuesElement.getTimestamp());

					// OCPP 1.5 allows multiple "value" elements under each "values" element.
					List<ocpp.cs._2012._06.MeterValue.Value> valueList = valuesElement.getValue();
					for (ocpp.cs._2012._06.MeterValue.Value valueElement : valueList){

						// Set the parameter indices for batch execution
						pt.setInt(1, connector_pk);
						pt.setNull(2, java.sql.Types.INTEGER);
						pt.setTimestamp(3, timestamp);
						pt.setString(4, valueElement.getValue());					
						/** Start: OCPP 1.5 allows for each "value" element to have optional attributes **/
						if (valueElement.getContext() != null) pt.setString(5, valueElement.getContext().value()); else pt.setNull(5, java.sql.Types.VARCHAR);
						if (valueElement.getFormat() != null) pt.setString(6, valueElement.getFormat().value()); else pt.setNull(6, java.sql.Types.VARCHAR);
						if (valueElement.getMeasurand() != null) pt.setString(7, valueElement.getMeasurand().value()); else pt.setNull(7, java.sql.Types.VARCHAR);
						if (valueElement.getLocation() != null) pt.setString(8, valueElement.getLocation().value()); else pt.setNull(8, java.sql.Types.VARCHAR);
						if (valueElement.getUnit() != null) pt.setString(9, valueElement.getUnit().value()); else pt.setNull(9, java.sql.Types.VARCHAR);
						/** Finish **/
						pt.addBatch();
					}
				}
			// ... Otherwise write the value of the transactionId
			} else {
				// OCPP 1.5 allows multiple "values" elements
				for (ocpp.cs._2012._06.MeterValue valuesElement : list) {
					Timestamp timestamp = Utils.convertToTimestamp(valuesElement.getTimestamp());
					
					// OCPP 1.5 allows multiple "value" elements under each "values" element.
					List<ocpp.cs._2012._06.MeterValue.Value> valueList = valuesElement.getValue();
					for (ocpp.cs._2012._06.MeterValue.Value valueElement : valueList){
						// Set the parameter indices for batch execution
						pt.setInt(1, connector_pk);
						pt.setInt(2, transactionId.intValue());
						pt.setTimestamp(3, timestamp);
						pt.setString(4, valueElement.getValue());					
						/** Start: OCPP 1.5 allows for each "value" element to have optional attributes **/
						if (valueElement.getContext() != null) pt.setString(5, valueElement.getContext().value()); else pt.setNull(5, java.sql.Types.VARCHAR);
						if (valueElement.getFormat() != null) pt.setString(6, valueElement.getFormat().value()); else pt.setNull(6, java.sql.Types.VARCHAR);
						if (valueElement.getMeasurand() != null) pt.setString(7, valueElement.getMeasurand().value()); else pt.setNull(7, java.sql.Types.VARCHAR);
						if (valueElement.getLocation() != null) pt.setString(8, valueElement.getLocation().value()); else pt.setNull(8, java.sql.Types.VARCHAR);
						if (valueElement.getUnit() != null) pt.setString(9, valueElement.getUnit().value()); else pt.setNull(9, java.sql.Types.VARCHAR);
						/** Finish **/
						pt.addBatch();
					}
				}
			}

			// Execute the batch.
			int[] count = pt.executeBatch();
			// Validate the change
			if (Utils.validateDMLChanges(count)) {
				// Now we can commit everything
				connect.commit();
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		}  finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}

	public static synchronized int insertTransaction(
			String chargeBoxIdentity, 
			int connectorId, 
			String idTag,
			Timestamp startTimestamp, 
			String startMeterValue,
			Integer reservationId){

		// Initialize with an invalid id
		int transactionId = -1;
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);

			// PreparedStatements can use parameter indices as question marks
			// After insert, a DB trigger sets the user.inTransaction field to 1
			pt = connect.prepareStatement("INSERT INTO transaction (connector_pk, idTag, startTimestamp, startValue) "
					+ "SELECT connector_pk , ? , ? , ? FROM connector WHERE chargeBoxId = ? AND connectorId = ?", PreparedStatement.RETURN_GENERATED_KEYS);

			// Set the parameter indices
			pt.setString(1, idTag);
			pt.setTimestamp(2, startTimestamp);
			pt.setString(3, startMeterValue);
			pt.setString(4, chargeBoxIdentity);
			pt.setInt(5, connectorId); 				

			// Insert the transaction into DB
			int countTrans = pt.executeUpdate();

			// Get the generated key in order to obtain the auto-incremented transaction_pk
			rs = pt.getGeneratedKeys();
			if (rs.next()) {
				transactionId = rs.getInt(1); // transaction_pk is the 1. column
			}

			// Validate the change
			if (countTrans == 1) {
				// For OCPP 1.5: a startTransaction may be related to a reservation
				if (reservationId != null) {
					// Okay, now end the reservation
					Utils.releaseResources(null, pt, rs);
					pt = connect.prepareStatement("DELETE FROM reservation WHERE reservation_pk=?");
					pt.setInt(1, reservationId.intValue());
					// Execute the query
					int countRes = pt.executeUpdate();
					// Validate the change
					if (countRes == 1) {
						// Both operations successful, now we can commit
						connect.commit();
					} else {
						// Ending the reservation failed, dismiss both changes
						LOG.error("Transaction is being rolled back.");
						connect.rollback();
						transactionId = -1;
					}					
				} else {
					// No reservationId, we can commit
					connect.commit();
				}
			} else {
				// startTransaction failed, dismiss the change
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
				transactionId = -1;
			}
			connect.setAutoCommit(true);
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}
		return transactionId;
	}
	
	public static synchronized void updateTransaction(
			String chargeBoxIdentity,
			int transactionId,
			Timestamp stopTimestamp, 
			String stopMeterValue){
			
		Connection connect = null;
		PreparedStatement pt = null;
		try {			
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);

			// PreparedStatements can use parameter indices as question marks
			// After update, a DB trigger sets the user.inTransaction field to 0
			pt = connect.prepareStatement("UPDATE transaction SET stopTimestamp = ?, stopValue = ? WHERE transaction_pk = ?");

			// Set the parameter indices
			pt.setTimestamp(1, stopTimestamp);
			pt.setString(2, stopMeterValue);
			pt.setInt(3, transactionId);

			// Insert the transaction into DB
			int count = pt.executeUpdate();
			// Validate the change
			if (count == 1) {
				// Now we can commit
				connect.commit();
			} else {
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);			
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, null);	
		}
	}
	
	/**
	 * Helper method to read the columns of an idTag row.
	 * Returns null if the idTag is not found.
	 * 
	 */
	public static synchronized SQLIdTagData getIdTagColumns(String idTag) {
		
		SQLIdTagData sqlAuthData = null;
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();			
			// PreparedStatements can use parameter indices as question marks
			pt = connect.prepareStatement("SELECT parentIdTag, expiryDate, inTransaction, blocked FROM user WHERE idTag = ?");
			// Set the parameter indices
			pt.setString(1, idTag.toLowerCase());
			// Execute and get the result of the SQL query
			rs = pt.executeQuery();

			if (rs.next() == true) {	
				// Read the DB row values
				sqlAuthData = new SQLIdTagData();
				sqlAuthData.parentIdTag = rs.getString(1);
				sqlAuthData.expiryDate = rs.getTimestamp(2);
				sqlAuthData.inTransaction = rs.getBoolean(3);
				sqlAuthData.blocked = rs.getBoolean(4);		
			}			
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}
		return sqlAuthData;
	}
	
	/**
	 * Helper method to get the connector id of a charge point
	 * where a transaction started.
	 * 
	 */
	public static synchronized int getConnectorId (int transactionId){
		
		int connectorId = -1;
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {			
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			
			pt = connect.prepareStatement("SELECT connectorId FROM connector WHERE connector_pk = (SELECT connector_pk FROM transaction WHERE transaction_pk = ?)");
			pt.setInt(1, transactionId);
			
			// Execute and get the result of the SQL query
			rs = pt.executeQuery();			
			if (rs.next() == true) connectorId = rs.getInt(1);
			
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);	
		}	
		return connectorId;
	}
}