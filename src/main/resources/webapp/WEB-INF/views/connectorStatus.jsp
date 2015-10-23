<%@ include file="00-header.jsp" %>
<div class="content"><div>
<section><span>
Connector Status
	<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
		<span>Last status information and corresponding date/time of connectors received from charging stations.
			The OCPP term 'connector' refers to the charging socket of a station.</span>
	</a>
</span></section>
<table class="res" id="connectorStatusTable">
	<thead><tr><th>ChargeBox ID</th><th>Connector ID</th><th>Date/Time</th><th>Status</th><th>Error Code</th></tr></thead>
	<tbody>
		<c:forEach items="${connectorStatusList}" var="connectorStatus">
			<tr>
				<td>${connectorStatus.chargeBoxId}</td>
				<td>${connectorStatus.connectorId}</td>
				<td>${connectorStatus.timeStamp}</td>
				<td>${connectorStatus.status}</td>
				<td>${connectorStatus.errorCode}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div></div>
<%@ include file="00-footer.jsp" %>