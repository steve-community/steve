<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<section><span>Existing Reservations</span></section>
<table class="res">
	<thead><tr><th>Reservation Id</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>expiryDatetime</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${reservList}" var="res">
	<tr><td>${res.id}</td><td>${res.idTag}</td><td>${res.chargeBoxId}</td><td>${res.startDatetime}</td><td>${res.expiryDatetime}</td></tr>
	</c:forEach>
	<%-- End --%>
	</tbody>
</table>
<br>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>