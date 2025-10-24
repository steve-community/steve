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
    <section><span>Installed Certificates</span></section>
    <form:form action="${ctxPath}/manager/security/certificates" method="get">
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
                <td>Certificate Type:</td>
                <td>
                    <select name="certificateType">
                        <option value="">All</option>
                        <option value="ChargePointCertificate" ${selectedCertificateType == 'ChargePointCertificate' ? 'selected' : ''}>Charge Point</option>
                        <option value="CentralSystemRootCertificate" ${selectedCertificateType == 'CentralSystemRootCertificate' ? 'selected' : ''}>Central System Root</option>
                        <option value="ManufacturerRootCertificate" ${selectedCertificateType == 'ManufacturerRootCertificate' ? 'selected' : ''}>Manufacturer Root</option>
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
    <table class="res action">
        <thead>
        <tr>
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="string">Certificate Type</th>
            <th data-sort="string">Serial Number</th>
            <th data-sort="string">Issuer</th>
            <th data-sort="string">Subject</th>
            <th data-sort="date">Valid From</th>
            <th data-sort="date">Valid To</th>
            <th data-sort="string">Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${certificates}" var="cert">
            <tr>
                <td>${cert.chargeBoxId}</td>
                <td>${cert.certificateType}</td>
                <td><encode:forHtml value="${cert.serialNumber}" /></td>
                <td><encode:forHtml value="${cert.issuerName}" /></td>
                <td><encode:forHtml value="${cert.subjectName}" /></td>
                <td data-sort-value="${cert.validFrom.millis}">${cert.validFrom}</td>
                <td data-sort-value="${cert.validTo.millis}">${cert.validTo}</td>
                <td>
                    <c:choose>
                        <c:when test="${cert.status == 'EXPIRED'}">
                            <span style="color: red; font-weight: bold;">EXPIRED</span>
                        </c:when>
                        <c:when test="${cert.status == 'REVOKED'}">
                            <span style="color: red;">REVOKED</span>
                        </c:when>
                        <c:otherwise>
                            <span style="color: green;">${cert.status}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <form:form action="${ctxPath}/manager/security/certificates/delete/${cert.certificateId}" method="post">
                        <input type="submit" class="redSubmit" value="Delete" onclick="return confirm('Are you sure you want to delete this certificate?');">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="../00-footer.jsp" %>