<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
<form:form action="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Location (directory URI):</td><td><form:input path="location" /></td></tr>
        <tr><td>Retries (integer):</td><td><form:input path="retries" placeholder="optional" /></td></tr>
        <tr><td>Retry Interval (integer):</td><td><form:input path="retryInterval" placeholder="optional" /></td></tr>
        <tr><td>Start Date/Time:</td>
            <td>
                <form:input path="start" placeholder="optional" cssClass="dateTimePicker"/>
            </td>
        </tr>
        <tr><td>Stop Date/Time:</td>
            <td>
                <form:input path="stop" placeholder="optional" cssClass="dateTimePicker"/>
            </td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>