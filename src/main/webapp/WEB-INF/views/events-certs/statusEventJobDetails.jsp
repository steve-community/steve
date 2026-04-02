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
        <%@ include file="../snippets/datePicker-past.js" %>
    });
</script>
<div class="content"><div>
<c:choose>
    <c:when test="${'FirmwareUpdate'.equals(eventType)}">
        <section><span>Firmware Update Job Details</span></section>
        <table class="userInput">
            <tr><td>Job/Request ID:</td><td><a href="${ctxPath}/manager/events/status?eventType=${eventType}&jobId=${details.jobId}">${details.jobId}</a></td></tr>
            <tr><td>Created at:</td><td>${details.createdAt}</td></tr>
            <tr><td>Location:</td><td><encode:forHtml value="${details.firmwareLocation}" /></td></tr>
            <tr><td>Retrieve Date/Time:</td><td>${details.retrieveDatetime}</td></tr>
            <tr><td>Install Date/Time:</td><td>${details.installDatetime}</td></tr>
            <tr><td>Signature:</td><td><encode:forHtml value="${details.signature}" /></td></tr>
            <tr><td>Signing Certificate:</td><td><encode:forHtml value="${details.signingCertificate}" /></td></tr>
        </table>
    </c:when>
    <c:when test="${'LogUpload'.equals(eventType)}">
        <section><span>Log Upload Job Details</span></section>
        <table class="userInput">
            <tr><td>Job/Request ID:</td><td><a href="${ctxPath}/manager/events/status?eventType=${eventType}&jobId=${details.jobId}">${details.jobId}</a></td></tr>
            <tr><td>Created at:</td><td>${details.createdAt}</td></tr>
            <tr><td>Log Type:</td><td>${details.logType}</td></tr>
            <tr><td>Remote Location:</td><td>${details.remoteLocation}</td></tr>
            <tr><td>Oldest timestamp:</td><td>${details.oldestTimestamp}</td></tr>
            <tr><td>Latest timestamp:</td><td>${details.latestTimestamp}</td></tr>
        </table>
    </c:when>
    <%-- We should not go into this branch --%>
    <c:otherwise>
        Something went wrong.
    </c:otherwise>
</c:choose>
</div></div>
<%@ include file="../00-footer.jsp" %>
