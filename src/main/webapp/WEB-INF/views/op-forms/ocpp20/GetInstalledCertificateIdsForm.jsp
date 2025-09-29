<form:form action="${ctxPath}/manager/operations/v2.0/GetInstalledCertificateIds" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Certificate Type (optional):</td>
            <td><form:input path="certificateType" placeholder="CSMSRootCertificate|V2GRootCertificate|ManufacturerRootCertificate (leave empty for all)"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
