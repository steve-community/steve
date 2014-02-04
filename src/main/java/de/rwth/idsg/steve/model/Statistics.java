package de.rwth.idsg.steve.model;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
public class Statistics {
	
	int numChargeBoxes, numUsers, numReservs, numTranses, 
	heartbeatToday, heartbeatYester, heartbeatEarl, 
	connAvail, connOcc, connFault, connUnavail;
	
	public Statistics(int numChargeBoxes, int numUsers, int numReservs,
			int numTranses, int heartbeatToday, int heartbeatYester,
			int heartbeatEarl, int connAvail, int connOcc, int connFault,
			int connUnavail) {
		
		this.numChargeBoxes = numChargeBoxes;
		this.numUsers = numUsers;
		this.numReservs = numReservs;
		this.numTranses = numTranses;
		this.heartbeatToday = heartbeatToday;
		this.heartbeatYester = heartbeatYester;
		this.heartbeatEarl = heartbeatEarl;
		this.connAvail = connAvail;
		this.connOcc = connOcc;
		this.connFault = connFault;
		this.connUnavail = connUnavail;
	}

	public int getNumChargeBoxes() {
		return numChargeBoxes;
	}

	public int getNumUsers() {
		return numUsers;
	}

	public int getNumReservs() {
		return numReservs;
	}

	public int getNumTranses() {
		return numTranses;
	}

	public int getHeartbeatToday() {
		return heartbeatToday;
	}

	public int getHeartbeatYester() {
		return heartbeatYester;
	}

	public int getHeartbeatEarl() {
		return heartbeatEarl;
	}

	public int getConnAvail() {
		return connAvail;
	}

	public int getConnOcc() {
		return connOcc;
	}

	public int getConnFault() {
		return connFault;
	}

	public int getConnUnavail() {
		return connUnavail;
	}

	public void setNumChargeBoxes(int numChargeBoxes) {
		this.numChargeBoxes = numChargeBoxes;
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	public void setNumReservs(int numReservs) {
		this.numReservs = numReservs;
	}

	public void setNumTranses(int numTranses) {
		this.numTranses = numTranses;
	}

	public void setHeartbeatToday(int heartbeatToday) {
		this.heartbeatToday = heartbeatToday;
	}

	public void setHeartbeatYester(int heartbeatYester) {
		this.heartbeatYester = heartbeatYester;
	}

	public void setHeartbeatEarl(int heartbeatEarl) {
		this.heartbeatEarl = heartbeatEarl;
	}

	public void setConnAvail(int connAvail) {
		this.connAvail = connAvail;
	}

	public void setConnOcc(int connOcc) {
		this.connOcc = connOcc;
	}

	public void setConnFault(int connFault) {
		this.connFault = connFault;
	}

	public void setConnUnavail(int connUnavail) {
		this.connUnavail = connUnavail;
	}
}