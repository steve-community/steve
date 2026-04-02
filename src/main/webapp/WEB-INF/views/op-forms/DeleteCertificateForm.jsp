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
<form:form action="${ctxPath}/manager/operations/${opVersion}/DeleteCertificate" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <div class="info"><b>Info:</b>
    To delete a certificate, we first need to know about it.
    Therefore, the station must <a href="${ctxPath}/manager/operations/v1.6/GetInstalledCertificateIds">send its certificates</a> to us so we can store them in our database.
    You can view the stored certificates <a href="${ctxPath}/manager/certificates/installed">here</a>.
    This page allows you to delete a certificate from a station as well.
    </div>
    <table class="userInput">
        <tr><td>ID of the Installed Certificate:</td><td><form:select path="installedCertificateId" disabled="true"/></td></tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
