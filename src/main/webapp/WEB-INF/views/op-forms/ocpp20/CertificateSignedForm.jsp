<form:form action="${ctxPath}/manager/operations/v2.0/CertificateSigned" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Certificate Chain:</td>
            <td><form:textarea path="certificateChain" rows="10" cols="60" placeholder="Paste PEM encoded X.509 certificate chain"/></td>
        </tr>
        <tr>
            <td>Certificate Type (optional):</td>
            <td><form:input path="certificateType" placeholder="ChargingStationCertificate|V2GRootCertificate"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
