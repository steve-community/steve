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
        Error while trying to update a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
    <section><span>Webuser Details</span></section>
    <form:form action="${ctxPath}/manager/webusers/update" modelAttribute="webuserForm">    
        <table class="userInput">
            <thead><tr><th>Webuser</th><th></th></tr></thead>
            <tbody>
                <tr><td>Username:</td><td>${webuserForm.webUsername} 
                        <form:hidden path="webUsername" readonly="true"/>
                        <form:hidden path="webUserPk" readonly="true"/>
                </td></tr>
                <tr><td></td>
                    <td><a href="${ctxPath}/manager/webusers/password/${webuserForm.webUsername}">
                        <B>Change Password</B></a>
                    </td>
                </tr>
                <tr><td></td>
                    <td><a href="${ctxPath}/manager/webusers/apipassword/${webuserForm.webUsername}">
                        <B>Change API Password</B></a>
                    </td>
                </tr>
                <tr><td>Roles:</td>
                    <td>
                        <select id="myRoleList" name="authorities" path="authorities" title="List of roles/authoriies the web-user has.">
                            <c:forEach items="${webuserForm.authorities.values()}" var="auth">
                                <option value="${auth}" ${auth == webuserForm.authorities ? 'selected' : ''} >"${auth.value}"</option>
                            </c:forEach>
                        </select>     
                    </td>
                </tr>
                <tr>
                    <td>Enabled:</td>
                    <td>
                        <select id="myEnabledList" name="enabled" path="enabled" title="Enabled/Disable the user">
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
                    </td>
                </tr>
            </tbody>
        </table>
    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>

