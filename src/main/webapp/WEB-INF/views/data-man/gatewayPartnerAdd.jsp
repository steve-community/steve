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
<spring:hasBindErrors name="params">
    <div class="error">
        Error while adding gateway partner:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>

    <section><span>Add Gateway Partner</span></section>
    <form:form action="${ctxPath}/manager/gateway/partners/add" modelAttribute="params">

        <table class="userInput">
            <thead><tr><th>Partner Information</th><th></th></thead>
            <tbody>
                <tr>
                    <td>Partner Name: <span class="required">*</span></td>
                    <td><form:input path="name" maxlength="100"/></td>
                </tr>
                <tr>
                    <td>Protocol: <span class="required">*</span></td>
                    <td>
                        <form:select path="protocol">
                            <form:option value="OCPI" label="OCPI (Open Charge Point Interface)"/>
                            <form:option value="OICP" label="OICP (Open InterCharge Protocol)"/>
                        </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Role: <span class="required">*</span></td>
                    <td>
                        <form:select path="role">
                            <form:option value="CPO" label="CPO (Charge Point Operator)"/>
                            <form:option value="EMSP" label="eMSP (e-Mobility Service Provider)"/>
                        </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Party ID:</td>
                    <td>
                        <form:input path="partyId" maxlength="3"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>3-character party identifier (e.g., ABC). Required for OCPI.</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Country Code:</td>
                    <td>
                        <form:input path="countryCode" maxlength="2"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>2-character ISO 3166-1 alpha-2 country code (e.g., DE, US). Required for OCPI.</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Endpoint URL: <span class="required">*</span></td>
                    <td>
                        <form:input path="endpointUrl" maxlength="255" style="width: 400px;"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>Partner's base API endpoint URL (e.g., https://api.partner.com/ocpi/2.2)</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Authentication Token: <span class="required">*</span></td>
                    <td>
                        <form:input path="token" type="password" maxlength="255" style="width: 400px;"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>Authentication token for API calls. Will be securely hashed before storage.</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Enabled:</td>
                    <td><form:checkbox path="enabled"/></td>
                </tr>
                <tr>
                    <td></td>
                    <td id="add_space">
                        <input type="submit" value="Add">
                        <a href="${ctxPath}/manager/gateway/partners"><input type="button" value="Cancel"></a>
                    </td>
                </tr>
            </tbody>
        </table>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>