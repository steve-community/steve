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
    <section><span>Installed Certificates</span></section>
    <form:form action="${ctxPath}/manager/certificates/installed" method="get" modelAttribute="params">
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
                <td>Certificate Type:</td>
                <td><form:select path="certificateType">
                        <option value="" selected>All</option>
                        <form:options items="${certificateType}" itemLabel="value"/>
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
    <table class="res action">
        <thead>
        <tr>
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="string">Certificate Type</th>
            <th data-sort="string">Hash Algorithm</th>
            <th data-sort="string">Issuer Name Hash</th>
            <th data-sort="string">Issuer Key Hash</th>
            <th data-sort="string">Serial Number</th>
            <th data-sort="date">Responded At</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${certificates}" var="cert">
            <tr>
                <td><a href="${ctxPath}/manager/chargepoints/details/${cert.chargeBoxPk}">${cert.chargeBoxId}</a></td>
                <td><encode:forHtml value="${cert.certificateType}" /></td>
                <td><encode:forHtml value="${cert.hashAlgorithm}" /></td>
                <td><encode:forHtml value="${cert.issuerNameHash}" /></td>
                <td><encode:forHtml value="${cert.issuerKeyHash}" /></td>
                <td><encode:forHtml value="${cert.serialNumber}" /></td>
                <td data-sort-value="${cert.respondedAt.millis}"><encode:forHtml value="${cert.respondedAt}" /></td>
                <td>
                    <form:form action="${ctxPath}/manager/certificates/installed/${cert.chargeBoxId}/delete/${cert.id}" method="post">
                        <input <c:if test="${not connectedCpList.contains(cert.chargeBoxId)}">disabled="disabled" title="This certificate cannot be deleted, because the station is not connected at the moment."</c:if>
                               type="submit"
                               class="redSubmit"
                               value="Delete"
                               onclick="return confirm('Are you sure you want to delete this certificate? It will delete the certificate at the station and then in database.');">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="../00-footer.jsp" %>
