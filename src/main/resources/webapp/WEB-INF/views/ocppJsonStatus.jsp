<%@ include file="00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="snippets/sortable.js" %>
    });
</script>
<div class="content"><div>
<section><span>
Connection Status for JSON Charge Points
    	<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>There can be multiple entries for a charge point.
                This indicates that charge point has opened more than one actual connection.</span>
        </a>
</span></section>
    <table class="res">
        <thead>
            <tr>
                <th data-sort="string">ChargeBox ID</th>
                <th data-sort="string">OCPP Version</th>
                <th data-sort="date">Connected Since</th>
                <th data-sort="string">Connection Duration</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${ocppJsonStatusList}" var="s">
            <tr><td><a href="${ctxPath}/manager/chargepoints/details/${s.chargeBoxPk}">${s.chargeBoxId}</a></td>
                <td>${s.version.value}</td>
                <td data-sort-value="${s.connectedSinceDT.millis}">${s.connectedSince}</td>
                <td>${s.connectionDuration}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="00-footer.jsp" %>