<%@ include file="00-header.jsp" %>
<div class="content"><div>
<section><span>
Connection Status for JSON Charge Points
</span></section>
    <table class="res">
        <thead><tr><th>ChargeBox ID</th><th>OCPP Version</th><th>Connected Since</th><th>Connection Duration</th></tr></thead>
        <tbody>
        <c:forEach items="${ocppJsonStatusList}" var="status">
            <tr><td>${status.chargeBoxId}</td>
                <td>${status.version.value}</td>
                <td>${status.connectedSince}</td>
                <td>${status.connectionDuration}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="00-footer.jsp" %>