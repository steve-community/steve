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