<form:form action="${ctxPath}/manager/operations/v2.0/DeleteCertificate" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Hash Algorithm:</td>
            <td><form:input path="certificateHashData.hashAlgorithm" placeholder="SHA256|SHA384|SHA512"/></td>
        </tr>
        <tr>
            <td>Issuer Name Hash:</td>
            <td><form:input path="certificateHashData.issuerNameHash" placeholder="Enter issuer name hash"/></td>
        </tr>
        <tr>
            <td>Issuer Key Hash:</td>
            <td><form:input path="certificateHashData.issuerKeyHash" placeholder="Enter issuer key hash"/></td>
        </tr>
        <tr>
            <td>Serial Number:</td>
            <td><form:input path="certificateHashData.serialNumber" placeholder="Enter certificate serial number"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
