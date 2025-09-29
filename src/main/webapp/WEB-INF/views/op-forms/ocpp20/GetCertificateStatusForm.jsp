<form:form action="${ctxPath}/manager/operations/v2.0/GetCertificateStatus" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>OCSP Request Data:</td>
            <td><form:textarea path="ocspRequestData" rows="10" cols="60" placeholder="Enter OCSP request data (Base64 encoded)"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
