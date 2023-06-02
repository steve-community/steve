<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2023 SteVe Community Team
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
    <section><span>Webuser Overview</span></section>
    <form:form action="${ctxPath}/manager/webusers/query" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>Username:</td>
                <td><form:input path="webusername" title="webUsername_title" /></td>
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
                <th data-sort="string">Username</th>
                <th>Roles</th>
                <th>Enabled</th>
                <th>
                    <form:form action="${ctxPath}/manager/webusers/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New">
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${webuserList}" var="cr">
            <tr><td><a href="${ctxPath}/manager/webusers/details/${cr.webusername}">${cr.webusername}</a></td>
                <td>${cr.roles}</td>
                <td>${cr.enabled}</td>
                <td>
                    <form:form action="${ctxPath}/manager/webusers/delete/${cr.webusername}/${cr.roles}">
                        <input type="submit" class="redSubmit" value="Delete">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>