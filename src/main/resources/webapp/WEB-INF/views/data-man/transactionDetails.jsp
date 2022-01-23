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
    });
</script>
<div class="content"><div>
    <center>
        <table id="details" class="cpd">
            <thead><tr><th>Transaction Overview</th><th></th></tr></thead>
            <tr><td>Transaction ID</td><td>${details.transaction.id}</td></tr>
            <tr><td>ChargeBox ID</td><td><a href="${ctxPath}/manager/chargepoints/details/${details.transaction.chargeBoxPk}">${details.transaction.chargeBoxId}</a></td></tr>
            <tr><td>Connector ID</td><td>${details.transaction.connectorId}</td></tr>
            <tr><td>OCPP ID Tag</td><td><a href="${ctxPath}/manager/ocppTags/details/${details.transaction.ocppTagPk}">${details.transaction.ocppIdTag}</a></td></tr>
            <tr><td>Start Date/Time</td><td>${details.transaction.startTimestamp}</td></tr>
            <tr><td>Start Value</td><td>${details.transaction.startValue}</td></tr>
            <tr><td>Stop Date/Time</td><td>${details.transaction.stopTimestamp}</td></tr>
            <tr><td>Stop Value</td><td>${details.transaction.stopValue}</td></tr>
            <tr><td>Stop Reason</td><td>${details.transaction.stopReason}</td></tr>
            <tr><td>Stop Event Actor</td><td>${details.transaction.stopEventActor}</td></tr>
        </table>
    </center>
    <br>
    <section><span>Intermediate Meter Values</span></section>
    <table class="res">
        <thead>
        <tr>
            <th data-sort="date">Value Timestamp</th>
            <th data-sort="int">Value</th>
            <th>Reading Context</th>
            <th>Format</th>
            <th>Measurand</th>
            <th>Location</th>
            <th>Unit</th>
            <th>Phase</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${details.values}" var="v">
            <tr>
                <td data-sort-value="${v.valueTimestamp.millis}">${v.valueTimestamp}</td>
                <td>${v.value}</td>
                <td>${v.readingContext}</td>
                <td>${v.format}</td>
                <td>${v.measurand}</td>
                <td>${v.location}</td>
                <td>${v.unit}</td>
                <td>${v.phase}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>
