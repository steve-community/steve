<%@ include file="../00-header.jsp" %>
<div class="content">
    <c:choose>
        <c:when test="${showActive == true}">
            <section><span>Active Reservations</span></section>
            <form:form method="get" action="/steve/manager/reservations/all">
                <input type="submit" value="Show All"/>
            </form:form>
        </c:when>
        <c:otherwise>
            <section><span>All Reservations</span></section>
            <form:form method="get" action="/steve/manager/reservations/active">
                <input type="submit" value="Show Active"/>
            </form:form>
        </c:otherwise>
    </c:choose>
<table class="res">
	<thead><tr><th>Reservation ID</th><th>Transaction ID</th><th>User ID Tag</th><th>ChargeBox ID</th><th>Start Date/Time</th><th>Expiry Date/Time</th><th>Status</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${reservList}" var="res">
	<tr><td>${res.id}</td><td>${res.transactionId}</td><td>${res.idTag}</td><td>${res.chargeBoxId}</td><td>${res.startDatetime}</td><td>${res.expiryDatetime}</td><td>${res.status}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>