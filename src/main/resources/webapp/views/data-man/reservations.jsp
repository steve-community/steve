<%@ include file="../00-header.jsp" %>
<div class="content">
<section><span>Existing Reservations</span></section>
<table class="res">
	<thead><tr><th>Reservation ID</th><th>User ID Tag</th><th>ChargeBox ID</th><th>Start Date/Time</th><th>Expiry Date/Time</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${reservList}" var="res">
	<tr><td>${res.id}</td><td>${res.idTag}</td><td>${res.chargeBoxId}</td><td>${res.startDatetime}</td><td>${res.expiryDatetime}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>