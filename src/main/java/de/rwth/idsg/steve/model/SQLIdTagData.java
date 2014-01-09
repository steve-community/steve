package de.rwth.idsg.steve.model;

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
	
	private String parentIdTag;
	private Timestamp expiryDate;
	private boolean inTransaction;
	private boolean blocked;
	
	public SQLIdTagData(String parentIdTag, Timestamp expiryDate, 
			boolean inTransaction, boolean blocked) {
		
		this.parentIdTag = parentIdTag;
		this.expiryDate = expiryDate;
		this.inTransaction = inTransaction;
		this.blocked = blocked;
	}

	public String getParentIdTag() {
		return parentIdTag;
	}

	public Timestamp getExpiryDate() {
		return expiryDate;
	}

	public boolean isInTransaction() {
		return inTransaction;
	}

	public boolean isBlocked() {
		return blocked;
	}	
}
