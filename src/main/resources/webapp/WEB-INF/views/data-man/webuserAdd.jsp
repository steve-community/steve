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
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/datePicker-past.js" %>
    });
</script>
<spring:hasBindErrors name="webuserForm">
    <div class="error">
        Error while trying to add a webuser:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<c:if test="${webuserForm.pwError == true}">
        <div class="error">The password is to short or the password input is not identical.</div>
</c:if>
<div class="content"><div>
<section><span>Add Webuser</span></section>
    <form:form action="${ctxPath}/manager/webusers/add" modelAttribute="webuserForm">
       <table class="userInput">
            <thead><tr><th>New Webuser</th><th></th></thead>
            <tbody>
            <tr><td>Webusername:</td><td><form:input path="webUsername" title="Web-Users Name"/></td></tr>
            <tr><td>Password:</td><td><form:password path="password" title="Set the password"/></td></tr>
            <tr><td>Password confirmation:</td><td><form:password  path="passwordComparison" title="Confirm the password"/></td></tr>
            <tr><td>Roles:</td>
                <td>
                    <form><select id="myAuthoritiesList" name="authorities" path="authorities" title="List of roles/authoriies the web-user shall have.">
                                    <option value="USER" >USER</option>
                                    <option value="ADMIN" >ADMIN</option>
                                    <option value="USER,ADMIN" >USER, ADMIN</option>
                                </select>
                    </form>
                </td>
            </tr>
            <tr><td>Enabled:</td><td>true<form:hidden path="enabled" value="true"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <c:set var="submitButtonName" value="add" />
                    <c:set var="submitButtonValue" value="Add" />
                    <input type="submit" name="add" value="Add">
                    <input type="submit" name="backToOverview" value="Back to Overview">
                </td>
            </tr>
            </tbody>
        </table>
    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>