<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2026 SteVe Community Team
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
<%@ include file="00-header.jsp" %>
<div class="content">
<section><span>About SteVe</span></section>
    <c:if test="${releaseReport.moreRecent}">
        <div class="info">A new version (${releaseReport.githubVersion}) is available!
            <a target="_blank" href="${releaseReport.htmlUrl}">Release Info</a> -
            <a target="_blank" href="${releaseReport.downloadUrl}">Download</a>
        </div>
    </c:if>
<table class="userInputFullPage">
	<tr><td>Version:</td><td>${version}</td></tr>
	<tr><td>Database Version:</td><td>${db.version}</td></tr>
	<tr><td>Last Database Update:</td><td>${db.updateTimestamp}</td></tr>
    <tr><td>Log File:</td><td>${logFile}</td></tr>
	<tr><td>GitHub Page:</td><td><a href="https://github.com/steve-community/steve">https://github.com/steve-community/steve</a></td></tr>
    <tr><td>System Time:</td><td>${systemTime}</td></tr>
    <tr><td>System Time Zone:</td><td>${systemTimeZone}</td></tr>
</table>
<section><span>Endpoint Info</span></section>
<table class="userInputFullPage">
    <tr>
        <td>SOAP endpoint for OCPP:</td>
        <td>${endpointInfo.ocppSoap}</td>
    <tr>
        <td>WebSocket/JSON endpoint for OCPP:</td>
        <td>${endpointInfo.ocppWebSocket}</td>
    </tr>
    </tr>
</table>

<form:form action="${ctxPath}/manager/about/export" method="get" modelAttribute="exportForm">
<section><span>Export Data</span></section>
    <table class="userInput">
        <tr>
            <td>Export Type:</td>
            <td><form:select path="exportType">
                    <form:options items="${exportType}" itemLabel="text"/>
                </form:select>
            </td>
        </tr>
        <tr><td><i>
            Master Data contains only the core business-related information.
            Temporal data such as transactions, reservations, connector status values are omitted by design.
            It will export the following tables: ${masterDataTableNames}.
        </i></td><td></td></tr>
        <tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Export ZIP">
            </td>
        </tr>
    </table>
</form:form>

<form:form action="${ctxPath}/manager/about/import" method="post" enctype="multipart/form-data">
<section><span>Import Data</span></section>
    <table class="userInput">
        <tr>
            <tr>
                <td>Select ZIP file:</td>
                <td><input type="file" name="file" /></td>
            </tr>
            <tr><td><i>
                The import function only supports what we exported. Therefore, the file and data structure must follow
                a predetermined format. Arbitrary formats are not allowed. The import process will <b>delete all
                existing data</b> in a table before importing data in order to prevent data conflict issues. Therefore,
                we advise to use this function only with empty databases. Moreover, database version reconciliation is
                not supported (i.e. you cannot import data from an earlier schema version).
            </i></td><td></td></tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Import ZIP">
            </td>
        </tr>
    </table>
</form:form>

</div>
<%@ include file="00-footer.jsp" %>
