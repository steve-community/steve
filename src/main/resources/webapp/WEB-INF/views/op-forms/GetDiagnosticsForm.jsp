<form:form action="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Location (directory URI):</td><td><form:input path="location" /></td></tr>
        <tr><td>Retries (integer):</td><td><form:input path="retries" placeholder="optional" /></td></tr>
        <tr><td>Retry Interval (integer):</td><td><form:input path="retryInterval" placeholder="optional" /></td></tr>
        <tr><td>Start Date/Time:</td>
            <td>
                <form:input path="start" placeholder="optional" cssClass="dateTimePicker"/>
            </td>
        </tr>
        <tr><td>Stop Date/Time:</td>
            <td>
                <form:input path="stop" placeholder="optional" cssClass="dateTimePicker"/>
            </td>
        </tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>