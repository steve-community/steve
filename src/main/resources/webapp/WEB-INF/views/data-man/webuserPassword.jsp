<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2024 SteVe Community Team
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
<spring:hasBindErrors name="webuserForm">
    <div class="error">
        Error while trying to change password of webuser:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
<section><span>Webuser change password</span></section>
    <form:form method="post" action="${ctxPath}/manager/webusers/password/${webuserForm.webUsername}" modelAttribute="webuserForm">
       <table class="userInput">
            <thead><tr><th>Webuser</th><th></th></tr></thead>
            <tbody>
            <tr><td>Webusername:</td><td>${webuserForm.webUsername}
                    <form:hidden path="webUsername" value="${webuserForm.webUsername}"/>
                    <form:hidden path="webUserPk" value="${webuserForm.webUserPk}"/>
            </td></tr>
            <tr><td>Password:</td><td><form:password path="password" title="Set the password"/></td></tr>
            <tr><td>Password confirmation:</td><td><form:password  path="passwordComparison" title="Confirm the password"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" name="change" value="Change">
                    <input type="submit" name="backToOverview" value="Back to Details">
                </td>
            </tr>
            </tbody>
        </table>
    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>
