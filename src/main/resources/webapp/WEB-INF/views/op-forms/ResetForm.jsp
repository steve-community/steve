<form:form action="${ctxPath}/manager/operations/${opVersion}/Reset" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Reset Type:</td>
            <td>
                <form:select path="resetType">
                    <form:options items="${resetType}" />
                </form:select>
            </td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>