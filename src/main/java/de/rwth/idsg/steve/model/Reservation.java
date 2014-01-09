package de.rwth.idsg.steve.model;

/**
* Reservation information for Web interface
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class Reservation {
	private int id;
	private String idTag, chargeBoxId, startDatetime, expiryDatetime;
	
	public Reservation(int id, String idTag, String chargeBoxId,
			String startDatetime, String expiryDatetime) {

		this.id = id;
		this.idTag = idTag;
		this.chargeBoxId = chargeBoxId;
		this.startDatetime = startDatetime;
		this.expiryDatetime = expiryDatetime;
	}

	public int getId() {
		return id;
	}
	public String getIdTag() {
		return idTag;
	}
	public String getChargeBoxId() {
		return chargeBoxId;
	}
	public String getStartDatetime() {
		return startDatetime;
	}
	public String getExpiryDatetime() {
		return expiryDatetime;
	}	
}
