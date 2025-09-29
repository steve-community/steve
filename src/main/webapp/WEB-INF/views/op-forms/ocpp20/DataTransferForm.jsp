<form:form action="${ctxPath}/manager/operations/v2.0/DataTransfer" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Vendor ID:</td>
            <td><form:input path="vendorId" placeholder="VendorName"/></td>
        </tr>
        <tr>
            <td>Message ID (optional):</td>
            <td><form:input path="messageId" placeholder="CustomMessage"/></td>
        </tr>
        <tr>
            <td>Data (optional):</td>
            <td><form:textarea path="data" rows="5" cols="50" placeholder="Custom data..."/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
