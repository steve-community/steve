package de.rwth.idsg.steve.common;

import java.sql.Timestamp;

/**
 * This class holds the column contents for an idTag row. 
 * It serves as an intermediate after DB access, and before 
 * version-specific OCPP IdTagInfo. Based on this instances content, 
 * an IdTagInfo will be created by version-specific OCPP service implementation.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
public class SQLIdTagData {
	
	public String parentIdTag;
	public Timestamp expiryDate;
	public boolean inTransaction;
	public boolean blocked;
	
}
