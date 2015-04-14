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
            <tr><td>${result.key}</td>
                <td>${result.value.response}</td>
                <td>${result.value.errorMessage}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="00-footer.jsp" %>