<%@ include file="../00-header.jsp" %>
<div class="content"><div>
    <section><span>
        Charge Point Overview
        <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
            <span>Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</span>
        </a>
    </span></section>
    <form:form action="/steve/manager/chargepoints/query" method="get" modelAttribute="params">
        <table class="userInput">
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
    <table class="res action">
        <thead>
            <tr>
                <th>ChargeBox ID</th>
                <th>Description</th>
                <th>OCPP Protocol</th>
                <th>Last Heartbeat</th>
                <th>
                    <form:form action="/steve/manager/chargepoints/add" method="get">
                        <input type="submit" value="Add New">
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${cpList}" var="cp">
            <tr><td><a href="/steve/manager/chargepoints/details/${cp.chargeBoxId}">${cp.chargeBoxId}</a></td>
                <td>${cp.description}</td>
                <td>${cp.ocppProtocol}</td>
                <td>${cp.lastHeartbeatTimestamp}</td>
                <td>
                    <form:form action="/steve/manager/chargepoints/delete/${cp.chargeBoxId}">
                        <input type="submit" value="Delete">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>