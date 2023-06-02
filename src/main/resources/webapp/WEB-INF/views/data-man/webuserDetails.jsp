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
<spring:hasBindErrors name="webuserForm">
    <div class="error">
        Error while trying to update a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<c:if test="${webuserForm.pwerror == true}">
        <div class="error">The password is to short or the password input is not identical.</div>
</c:if>
<div class="content"><div>
    <section><span>Webuser Details</span></section>
    <form:form action="${ctxPath}/manager/webusers/update" modelAttribute="webuserForm">    
        <table class="userInput">
            <thead><tr><th>Webuser</th><th></th></thead>
            <tbody>
                <tr><td>Username:</td><td>${webusername}<form:hidden path="webusername" readonly="true"/></td></tr>
                <tr><td>Password:</td><td><form:password path="password" title="password_title"/></td></tr>
                <tr><td>Password confirmation:</td><td><form:password path="password_comparison" title="pwCompar_title"/></td></tr>
                <tr><td>Roles:</td>
                    <td>
                        <select id="myRoleList" name="roles" path="roles" title="roleList_title">
                            <option value="${webuserForm.roles}" >${webuserForm.roles}</option>
                            <option value="ROLE_USER" >ROLE_USER</option>
                            <option value="ROLE_ADMIN" >ROLE_ADMIN</option>
                            <option value="ROLE_USER; ROLE_ADMIN" >ROLE_USER; ROLE_ADMIN</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Enabled:</td>
                    <td>
                        <select id="myEnabledList" name="enabled" path="enabled" title="enabled_title">
                            <option value="${webuserForm.enabled}" >${webuserForm.enabled}</option>
                            <option value=false >false</option>
                            <option value=true >true</option>
                        </select>
                    </td>
                </tr>                
                <tr><td></td>
                    <td id="add_space">
			<input type="submit" name="update" value="Update">
			<input type="submit" name="backToOverview" value="Back to Overview">
                    <td>
                        <form:form action="${ctxPath}/manager/webusers/delete/${webusername}">
                            <input type="submit" class="redSubmit" value="Delete">
                        </form:form>
                    </td>
                </td>
                </tr>
            </tbody>
        </table>
    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>

