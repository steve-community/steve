<%@ include file="00-header.jsp" %>
<div class="content"><div>
<section><span>
Received Heartbeats <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle"><span>Date/time of the last heartbeat received from charging stations.</span></a>
</span></section>
<table class="res" id="heartbeatTable">
	<thead><tr><th>ChargeBox ID</th><th>Date/Time</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${heartbeatList}" var="heartbeat">
	<tr><td>${heartbeat.chargeBoxId}</td><td>${heartbeat.lastTimestamp}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
</div></div>
<%@ include file="00-footer.jsp" %>