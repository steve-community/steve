package de.rwth.idsg.steve.model;

/**
* User information for Web interface
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class User {
	private String idTag, parentIdTag, expiryDate;
	private boolean inTransaction, blocked;
	
	public User(String idTag, String parentIdTag, String expiryDate, 
			boolean inTransaction, boolean blocked) {
		
		this.idTag = idTag;
		this.parentIdTag = parentIdTag;
		this.expiryDate = expiryDate;
		this.inTransaction = inTransaction;
		this.blocked = blocked;
	}
	
	public String getIdTag() {
		return idTag;
	}
	public String getParentIdTag() {
		return parentIdTag;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public boolean isInTransaction() {
		return inTransaction;
	}
	public boolean isBlocked() {
		return blocked;
	}
}
