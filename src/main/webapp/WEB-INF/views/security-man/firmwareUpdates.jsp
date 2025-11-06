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
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-past.js" %>
        <%@ include file="../snippets/periodTypeSelect.js" %>
        <%@ include file="../snippets/sortable.js" %>
    });
</script>
<div class="content">
    <section>
        <span>
            Signed Firmware Updates
            <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>
            <em>Improved security for OCPP 1.6-J</em> supports signed firmware updates to ensure the integrity and authenticity of firmware installed on charge points.
            Use the <em>OPERATIONS > OCPP v1.6 > Signed Update Firmware</em> menu to initiate a signed firmware update request to a charge point.
            </span>
            </a>
        </span>
    </section>
    <form:form action="${ctxPath}/manager/security/firmwareUpdates" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>Job ID:</td>
                <td><form:input path="jobId"/></td>
            </tr>
            <tr>
                <td>ChargeBox ID:</td>
                <td><form:select path="chargeBoxId">
                        <option value="" selected>All</option>
                        <form:options items="${cpList}"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td>Period Type:</td>
                <td><form:select path="periodType" id="periodTypeSelect">
                        <form:options items="${periodType}" itemLabel="value"/>
                    </form:select>
                </td>
            </tr>
            <tr>
                <td>From:</td>
                <td><form:input path="from" id="intervalPeriodTypeFrom" cssClass="dateTimePicker"/></td>
            </tr>
            <tr>
                <td>To:</td>
                <td><form:input path="to" id="intervalPeriodTypeTo" cssClass="dateTimePicker"/></td>
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
            <th data-sort="int">Job/Request ID</th>
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="string">Status</th>
            <th data-sort="date">Timestamp</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${events}" var="event">
            <tr>
                <td><a href="${ctxPath}/manager/security/firmwareUpdates/${event.jobId}">${event.jobId}</a></td>
                <td><a href="${ctxPath}/manager/chargepoints/details/${event.chargeBoxPk}">${event.chargeBoxId}</a></td>
                <td>${event.status}</td>
                <td data-sort-value="${event.timestamp.millis}">${event.timestamp}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
<%@ include file="../00-footer.jsp" %>
