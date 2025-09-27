<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
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
<div class="content">
    <section><span>Security Events</span></section>
    <form:form action="${ctxPath}/manager/security/events" method="get">
        <table class="userInput">
            <tr>
                <td>ChargeBox ID:</td>
                <td>
                    <select name="chargeBoxId">
                        <option value="">All</option>
                        <c:forEach items="${chargeBoxIdList}" var="id">
                            <option value="${id}" ${id == selectedChargeBoxId ? 'selected' : ''}>${id}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Limit:</td>
                <td>
                    <select name="limit">
                        <option value="50" ${limit == 50 ? 'selected' : ''}>50</option>
                        <option value="100" ${limit == 100 ? 'selected' : ''}>100</option>
                        <option value="250" ${limit == 250 ? 'selected' : ''}>250</option>
                        <option value="500" ${limit == 500 ? 'selected' : ''}>500</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td id="add_space">
                    <input type="submit" value="Filter">
                </td>
            </tr>
        </table>
    </form:form>
    <br>
    <table class="res">
        <thead>
        <tr>
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="string">Event Type</th>
            <th data-sort="date">Timestamp</th>
            <th data-sort="string">Technical Info</th>
            <th data-sort="string">Severity</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${events}" var="event">
            <tr>
                <td>${event.chargeBoxId}</td>
                <td>${event.eventType}</td>
                <td data-sort-value="${event.timestamp.millis}">${event.timestamp}</td>
                <td><encode:forHtml value="${event.techInfo}" /></td>
                <td>
                    <c:choose>
                        <c:when test="${event.severity == 'CRITICAL' || event.severity == 'HIGH'}">
                            <span style="color: red; font-weight: bold;">${event.severity}</span>
                        </c:when>
                        <c:when test="${event.severity == 'MEDIUM'}">
                            <span style="color: orange; font-weight: bold;">${event.severity}</span>
                        </c:when>
                        <c:otherwise>
                            ${event.severity}
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="../00-footer.jsp" %>