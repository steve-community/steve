<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<div class="tileWrapper">
	<a class="tileRow1" href="${contextPath}/manager/chargepoints">
		Number of<br>Charge Points
		<span class="base formatNumber">${stats.numChargeBoxes}</span>
	</a>
	<a class="tileRow1" href="${contextPath}/manager/users">
		Number of<br>Users
		<span class="base formatNumber">${stats.numUsers}</span>
	</a>
	<a class="tileRow1" href="${contextPath}/manager/reservations">
		Number of<br>Existing Reservations
		<span class="base formatNumber">${stats.numReservs}</span>
	</a>
	<a class="tileRow1" href="${contextPath}/manager/transactions">
		Number of<br>Active Transactions
		<span class="base formatNumber">${stats.numTranses}</span>
	</a>
	<a class="tileRow2" href="${contextPath}/manager/home/heartbeats">
		Received Heartbeats
		<span class="baseTable">
			<span class="baseRow">
				<span class="baseCell">Today :</span>
				<span class="baseCell formatNumber">${stats.heartbeatToday}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Yesterday :</span>
				<span class="baseCell formatNumber">${stats.heartbeatYester}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Earlier :</span>
				<span class="baseCell formatNumber">${stats.heartbeatEarl}</span>
			</span>
		</span>
	</a>
	<a class="tileRow2" href="${contextPath}/manager/home/connectorStatus">
		Connector Status
		<span class="baseTable">
			<span class="baseRow">
				<span class="baseCell">Available :</span>
				<span class="baseCell formatNumber">${stats.connAvail}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Occupied :</span>
				<span class="baseCell formatNumber">${stats.connOcc}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Faulted :</span>
				<span class="baseCell formatNumber">${stats.connFault}</span>
			</span>
			<span class="baseRow">
				<span class="baseCell">Unavailable :</span>
				<span class="baseCell formatNumber">${stats.connUnavail}</span>
			</span>
		</span>	
	</a>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>