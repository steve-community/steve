<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../00-header.jsp" %>
<div class="content">
<section><span>
Transactions 
<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
<span>If stop date/time and value are empty, this means that a transaction is still active 
(i.e. it has started but not stopped yet or the charging station did not inform SteVe about the stopped transaction yet).<br>
Value column is the meter difference between the start and stop of a transaction, and therefore represents the charged value.</span>
</a>
</span></section>
<table class="res">
	<thead><tr><th>Transaction ID</th><th>ChargeBox ID</th><th>Connector ID</th><th>User ID Tag</th><th>Start Date/Time</th><th>Stop Date/Time</th><th>Value</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${transList}" var="ta">
	<tr><td>${ta.id}</td><td>${ta.chargeBoxId}</td><td>${ta.connectorId}</td><td>${ta.idTag}</td><td>${ta.startTimestamp}</td><td>${ta.stopTimestamp}</td><td>${ta.chargedMeterValue}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>