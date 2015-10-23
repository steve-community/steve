<%@ include file="00-header.jsp" %>
<div class="content"><div>
<section><span>
Connection Status for JSON Charge Points
    	<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
            <span>There can be multiple entries for a charge point.
                This indicates that charge point has opened more than one actual connection.</span>
        </a>
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