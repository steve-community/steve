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