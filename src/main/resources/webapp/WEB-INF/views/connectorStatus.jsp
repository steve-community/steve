<%@ include file="00-header.jsp" %>
<script type="text/javascript">
	$(document).ready(function() {
		<%@ include file="snippets/sortable.js" %>
	});
</script>
<div class="content"><div>
<section><span>
Connector Status
	<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
		<span>Last status information and corresponding date/time of connectors received from charging stations.
			The OCPP term 'connector' refers to the charging socket of a station.</span>
	</a>
</span></section>
<table class="res" id="connectorStatusTable">
	<thead>
		<tr>
			<th data-sort="string">ChargeBox ID</th>
			<th data-sort="int">Connector ID</th>
			<th data-sort="date">Date/Time</th>
			<th data-sort="string">Status</th>
			<th data-sort="string">Error Code</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${connectorStatusList}" var="cs">
			<tr>
				<td><a href="${ctxPath}/manager/chargepoints/details/${cs.chargeBoxPk}">${cs.chargeBoxId}</a></td>
				<td>${cs.connectorId}</td>
				<td data-sort-value="${cs.statusTimestamp.millis}">${cs.timeStamp}</td>
				<td>${cs.status}</td>
				<td>${cs.errorCode}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div></div>
<%@ include file="00-footer.jsp" %>