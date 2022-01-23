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
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="snippets/sortable.js" %>
    });
</script>
<div class="content">
    <section><span>
        Request Tasks <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle"><span>Click on Task IDs for detailed task information.</span></a>
    </span></section>
    <form:form action="${ctxPath}/manager/operations/tasks">
        <input type="submit" value="Delete Finished"/>
    </form:form>
    <br>
    <table class="res">
        <thead>
            <tr>
                <th data-sort="int">Task ID</th>
                <th data-sort="string">Origin</th>
                <th data-sort="date">Start Timestamp</th>
                <th data-sort="date">End Timestamp</th>
                <th>Responses / Requests</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${taskList}" var="task">
            <tr><td><a href="${ctxPath}/manager/operations/tasks/${task.taskId}">${task.taskId}</a></td>
                <td>${task.origin}</td>
                <td data-sort-value="${task.start.millis}">${task.start}</td>
                <td data-sort-value="${task.end.millis}">${task.end}</td>
                <td>${task.responseCount} / ${task.requestCount}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="00-footer.jsp" %>