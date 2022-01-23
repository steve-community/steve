<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
        $("#unknown").click(function () {
            $("#unknownTable, #overview").slideToggle(250);
        });
    });
</script>
<div class="content">
    <div>
    <section><span id="unknown" style="cursor: pointer">
    Unknown Charge Points
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>A list of charge points that attempted to connect and send a boot notification but were not present in database.</span>
    </a>
    </span></section>
    <div id="unknownTable" style="display: none">
        <table class="res add-margin-bottom">
            <thead>
            <tr>
                <th data-sort="string">ChargeBox ID</th>
                <th data-sort="int"># of Attempts</th>
                <th data-sort="date">Last Attempt</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${unknownList}" var="item">
                <tr>
                    <td>${item.key}</td>
                    <td>${item.numberOfAttempts}</td>
                    <td data-sort-value="${item.lastAttemptTimestamp.millis}">${item.lastAttemptTimestamp}</td>
                    <td>
                        <form:form cssClass="inline" action="${ctxPath}/manager/chargepoints/unknown/add/${item.key}/" method="post">
                            <input type="submit" class="blueSubmit" value="Add"/>
                        </form:form>
                        &nbsp;
                        <form:form cssClass="inline" action="${ctxPath}/manager/chargepoints/unknown/remove/${item.key}/" method="post">
                            <input type="submit" class="redSubmit" value="Forget"/>
                        </form:form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <section><span>
    Charge Point Overview
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</span>
    </a>
    </span></section>
    <div id="overview">
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
        <table class="res action">
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
    </div>
</div></div>
<%@ include file="../00-footer.jsp" %>