<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="00-header.jsp" %>
<div class="tileWrapper">
	<a class="tileRow1" href="/steve/manager/chargepoints">
		Number of<br>Charge Points
		<span class="base formatNumber">${stats.numChargeBoxes}</span>
	</a>
	<a class="tileRow1" href="/steve/manager/users">
		Number of<br>Users
		<span class="base formatNumber">${stats.numUsers}</span>
	</a>
	<a class="tileRow1" href="/steve/manager/reservations">
		Number of<br>Existing Reservations
		<span class="base formatNumber">${stats.numReservations}</span>
	</a>
	<a class="tileRow1" href="/steve/manager/transactions">
		Number of<br>Active Transactions
		<span class="base formatNumber">${stats.numTransactions}</span>
	</a>
	<a class="tileRow2" href="/steve/manager/home/heartbeats">
		Received Heartbeats
		<span class="baseTable">
			<span class="baseRow">
				<span class="baseCell">Today :</span>
				<span class="baseCell formatNumber">${stats.heartbeatToday}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Yesterday :</span>
				<span class="baseCell formatNumber">${stats.heartbeatYesterday}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Earlier :</span>
				<span class="baseCell formatNumber">${stats.heartbeatEarlier}</span>
			</span>
		</span>
	</a>
	<a class="tileRow2" href="/steve/manager/home/connectorStatus">
		Connector Status
		<span class="baseTable">
			<span class="baseRow">
				<span class="baseCell">Available :</span>
				<span class="baseCell formatNumber">${stats.connAvailable}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Occupied :</span>
				<span class="baseCell formatNumber">${stats.connOccupied}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Faulted :</span>
				<span class="baseCell formatNumber">${stats.connFaulted}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Unavailable :</span>
				<span class="baseCell formatNumber">${stats.connUnavailable}</span>
			</span>
		</span>	
	</a>
</div>
<%@ include file="00-footer.jsp" %>