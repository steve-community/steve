<%@ include file="../00-header.jsp" %>
<div class="content">
<section><span>
Transactions 
<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
<span>If stop date/time and stop value are empty, this means that a transaction is still active
(i.e. it has started but not stopped yet or the charging station did not inform SteVe about the stopped transaction yet).</span>
</a>
</span></section>
    <form:form method="get" action="/steve/manager/transactions/csv">
        <input type="submit" value="Get CSV"/>
    </form:form>
    <table class="res">
        <thead><tr><th>Transaction ID</th><th>ChargeBox ID</th><th>Connector ID</th><th>User ID Tag</th><th>Start Date/Time</th><th>Start Value</th><th>Stop Date/Time</th><th>Stop Value</th></tr></thead>
        <tbody>
        <%-- Start --%>
        <c:forEach items="${transList}" var="ta">
        <tr><td>${ta.id}</td><td>${ta.chargeBoxId}</td><td>${ta.connectorId}</td><td>${ta.idTag}</td><td>${ta.startTimestamp}</td><td>${ta.startValue}</td><td>${ta.stopTimestamp}</td><td>${ta.stopValue}</td></tr>
        </c:forEach>
        <%-- End --%>
        </tbody>
    </table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>