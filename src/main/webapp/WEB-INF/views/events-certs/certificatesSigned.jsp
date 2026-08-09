<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2026 SteVe Community Team
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
    <section><span>Signed Certificates</span></section>
    <form:form action="${ctxPath}/manager/certificates/signed" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>ChargeBox ID:</td>
                <td><form:select path="chargeBoxId">
                    <option value="" selected>All</option>
                    <form:options items="${cpList}"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Serial Number:</td>
                <td><form:input path="serialNumber"/></td>
            </tr>
            <tr>
                <td>Issuer Name:</td>
                <td><form:input path="issuerName"/></td>
            </tr>
            <tr>
                <td>Subject Name:</td>
                <td><form:input path="subjectName"/></td>
            </tr>
            <tr>
                <td>Organization Name:</td>
                <td><form:input path="organizationName"/></td>
            </tr>
            <tr>
                <td>Accepted?:</td>
                <td><form:select path="accepted">
                    <form:options items="${accepted}" itemLabel="value"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td><i>Responded At</i> Filter:</td>
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
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="string">Serial Number</th>
            <th data-sort="string">Issuer Name</th>
            <th data-sort="string">Subject Name</th>
            <th data-sort="string">Org. Name</th>
            <th data-sort="date">Valid From</th>
            <th data-sort="date">Valid To</th>
            <th data-sort="string">Accepted?</th>
            <th data-sort="date">Responded At</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${certificates}" var="cert">
            <tr>
                <td><a href="${ctxPath}/manager/chargepoints/details/${cert.chargeBoxPk}">${cert.chargeBoxId}</a></td>
                <td><encode:forHtml value="${cert.serialNumber}" /></td>
                <td><encode:forHtml value="${cert.issuerName}" /></td>
                <td><encode:forHtml value="${cert.subjectName}" /></td>
                <td><encode:forHtml value="${cert.organizationName}" /></td>
                <td data-sort-value="${cert.validFrom.millis}">${cert.validFrom}</td>
                <td data-sort-value="${cert.validTo.millis}">${cert.validTo}</td>
                <td><${cert.accepted}</td>
                <td data-sort-value="${cert.respondedAt.millis}">${cert.respondedAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="../00-footer.jsp" %>
