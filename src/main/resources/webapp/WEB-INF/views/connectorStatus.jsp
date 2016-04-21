<%@ include file="00-header.jsp" %>
<div class="content"><div>
<section><span>
Connector Status
	<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
		<span>Last status information and corresponding date/time of connectors received from charging stations.
			The OCPP term 'connector' refers to the charging socket of a station.</span>
	</a>
</span></section>
<table class="res" id="connectorStatusTable">
	<thead><tr><th>ChargeBox ID</th><th>Connector ID</th><th>Date/Time</th><th>Status</th><th>Error Code</th></tr></thead>
	<tbody>
		<c:forEach items="${connectorStatusList}" var="cs">
			<tr>
				<td><a href="${ctxPath}/manager/chargepoints/details/${cs.chargeBoxPk}">${cs.chargeBoxId}</a></td>
				<td>${cs.connectorId}</td>
				<td>${cs.timeStamp}</td>
				<td>${cs.status}</td>
				<td>${cs.errorCode}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div></div>
<%@ include file="00-footer.jsp" %>