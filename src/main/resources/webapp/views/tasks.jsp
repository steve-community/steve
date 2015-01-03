<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="00-header.jsp" %>
<div class="content">
    <section><span>
        Request Tasks <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle"><span>Click on Task IDs for detailed task information.</span></a>
    </span></section>
    <form:form action="/steve/manager/operations/tasks">
        <input type="submit" value="Delete Finished"/>
    </form:form>
    <table class="res">
        <thead><tr><th>Task ID</th><th>Start Timestamp</th><th>End Timestamp</th><th>Responses / Requests</th></tr></thead>
        <tbody>
        <c:forEach items="${taskList}" var="task">
            <tr><td><a href="/steve/manager/operations/tasks/${task.taskId}">${task.taskId}</a></td>
                <td>${task.start}</td>
                <td>${task.end}</td>
                <td>${task.responseCount} / ${task.requestCount}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="00-footer.jsp" %>