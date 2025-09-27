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
    <div>
    <section><span>
    Gateway Partners
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>Manage roaming partners for OCPI and OICP protocols. Configure eMSPs and CPOs for charge point interoperability.</span>
    </a>
    </span></section>
    <div>
        <table class="res action">
            <thead>
            <tr>
                <th data-sort="string">Name</th>
                <th data-sort="string">Protocol</th>
                <th data-sort="string">Role</th>
                <th data-sort="string">Party ID</th>
                <th data-sort="string">Country Code</th>
                <th data-sort="string">Endpoint URL</th>
                <th data-sort="string">Status</th>
                <th>
                    <form:form action="${ctxPath}/manager/gateway/partners/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New">
                    </form:form>
                </th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${partnerList}" var="partner">
                <tr>
                    <td><a href="${ctxPath}/manager/gateway/partners/details/${partner.id}">${partner.name}</a></td>
                    <td>${partner.protocol}</td>
                    <td>${partner.role}</td>
                    <td>${partner.partyId}</td>
                    <td>${partner.countryCode}</td>
                    <td>${partner.endpointUrl}</td>
                    <td>
                        <c:choose>
                            <c:when test="${partner.enabled}">
                                <span style="color: green;">Enabled</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: red;">Disabled</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form:form action="${ctxPath}/manager/gateway/partners/delete/${partner.id}" method="post">
                            <input type="submit" class="redSubmit" value="Delete"
                                   onclick="return confirm('Are you sure you want to delete partner ${partner.name}?');">
                        </form:form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div></div>
<%@ include file="../00-footer.jsp" %>