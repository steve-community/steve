<form:form action="${ctxPath}/manager/operations/${opVersion}/DataTransfer" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Vendor ID (String):</td><td><form:input path="vendorId" /></td></tr>
        <tr><td>Message ID (String):</td><td><form:input path="messageId" placeholder="optional" /></td></tr>
        <tr><td>Data (Text):</td><td><form:input path="data" placeholder="optional" /></td></tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>