<form:form modelAttribute="params">
    <section><span>Charge Points</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>

    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="numeric value"/></td>
        </tr>
        <tr>
            <td>Firmware Location (URL):</td>
            <td><form:input path="location" placeholder="http:// or https:// URL"/></td>
        </tr>
        <tr>
            <td>Retries:</td>
            <td><form:input path="retries" placeholder="numeric value (optional)"/></td>
        </tr>
        <tr>
            <td>Retry Interval (seconds):</td>
            <td><form:input path="retryInterval" placeholder="numeric value (optional)"/></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div class="submit-button"><input type="submit" value="Perform"></div>
            </td>
        </tr>
    </table>
</form:form>
