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
<%@ include file="00-header.jsp" %>
<div class="content">
<c:if test="${not task.isFinished()}">
    <script>
        $(document).ready(
                setTimeout(function(){
                    window.location.reload(1);
                }, 5000));
    </script>
    <div class="info"><b>Info:</b> This page automatically reloads every 5 seconds until the task is finished.</div>
</c:if>
    <center>
        <table id='details' class='cpd'>
            <thead><tr><th>Task Details</th><th></th></tr></thead>
            <tr><td>Operation name</td><td>${task.ocppVersion.value} / ${task.operationName}</td></tr>
            <tr><td>Origin</td><td>${task.origin} (${task.caller})</td></tr>
            <tr><td>Start timestamp</td><td>${task.startTimestamp}</td></tr>
            <tr><td>End timestamp</td><td>${task.endTimestamp}</td></tr>
            <tr><td># of charge point requests</td><td>${task.resultMap.size()}</td></tr>
            <tr><td># of responses</td><td>${task.responseCount}</td></tr>
            <tr><td># of errors</td><td>${task.errorCount}</td></tr>
        </table>
    </center>
    <br>
    <section><span>Task Result</span></section>
    <table class="res">
        <thead><tr><th>ChargeBox ID</th><th>Response</th><th>Error</th></tr></thead>
        <tbody>
        <c:forEach items="${task.resultMap}" var="result">
            <tr>
                <td>${result.key}</td>
                <td>
                    <c:choose>
                        <c:when test="${result.value.details == null}">
                            ${result.value.response}
                        </c:when>
                        <c:otherwise>
                            ${result.value.response} (<a href="${ctxPath}/manager/operations/tasks/${taskId}/details/${result.key}/">Details</a>)
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${result.value.errorMessage}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="00-footer.jsp" %>