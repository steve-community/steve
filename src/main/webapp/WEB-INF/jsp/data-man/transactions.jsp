<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<section><span>
Transactions 
<a class="tooltip" href="#"><img src="${contextPath}/images/info.png" style="vertical-align:middle">
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
	<tr><td>${ta.transaction_pk}</td><td>${ta.chargeBoxId}</td><td>${ta.connectorId}</td><td>${ta.idTag}</td><td>${ta.startTimestamp}</td><td>${ta.stopTimestamp}</td><td>${ta.chargedMeterValue}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
<br>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>