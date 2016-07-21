<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>
<div class="content"><div>
    <section><span>
        Charge Point Overview
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</span>
        </a>
    </span></section>
    <form:form action="${ctxPath}/manager/chargepoints/query" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>ChargeBox ID:</td>
                <td><form:input path="chargeBoxId"/></td>
            </tr>
            <tr>
                <td>Description:</td>
                <td><form:input path="description"/></td>
            </tr>
            <tr>
                <td>Ocpp Version:</td>
                <td><form:select path="ocppVersion">
                    <option value="" selected>All</option>
                    <form:options items="${ocppVersion}" itemLabel="value"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Heartbeat Period:</td>
                <td><form:select path="heartbeatPeriod">
                        <form:options items="${heartbeatPeriod}" itemLabel="value"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td id="add_space">
                    <input type="submit" value="Get">
                </td>
            </tr>
        </table>
    </form:form>
    <br>
    <table class="res">
        <thead>
            <tr>
                <th data-sort="string">ChargeBox ID</th>
                <th data-sort="string">Description</th>
                <th data-sort="string">OCPP Protocol</th>
                <th data-sort="date">Last Heartbeat</th>
                <th>
                    <form:form action="${ctxPath}/manager/chargepoints/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New">
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${cpList}" var="cp">
            <tr><td><a href="${ctxPath}/manager/chargepoints/details/${cp.chargeBoxPk}">${cp.chargeBoxId}</a></td>
                <td>${cp.description}</td>
                <td>${cp.ocppProtocol}</td>
                <td data-sort-value="${cp.lastHeartbeatTimestampDT.millis}">${cp.lastHeartbeatTimestamp}</td>
                <td>
                    <form:form action="${ctxPath}/manager/chargepoints/delete/${cp.chargeBoxPk}">
                        <input type="submit" class="redSubmit" value="Delete">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>