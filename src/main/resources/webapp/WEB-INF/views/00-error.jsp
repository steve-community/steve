<%@ include file="00-header.jsp" %>
<div class="error">
    <h3>Error:</h3>
    <p>${exception}</p>
    <c:if test="${not empty exception.cause}">
        <h3>Reason:</h3>
        <p>${exception.cause}</p>
    </c:if>
    <br>
    <p>You can <a href="javascript:window.history.back()">go back</a> or, for more detail, <a href="${ctxPath}/manager/log">view the log</a></p>
</div>
<%@ include file="00-footer.jsp" %>