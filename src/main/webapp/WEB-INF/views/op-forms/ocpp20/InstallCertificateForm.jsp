<form:form action="${ctxPath}/manager/operations/v2.0/InstallCertificate" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Certificate Type:</td>
            <td><form:input path="certificateType" placeholder="CSMSRootCertificate|ManufacturerRootCertificate|V2GRootCertificate"/></td>
        </tr>
        <tr>
            <td>Certificate:</td>
            <td><form:textarea path="certificate" rows="10" cols="60" placeholder="Paste PEM encoded X.509 certificate"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
