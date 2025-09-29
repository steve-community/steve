<form:form action="${ctxPath}/manager/operations/v2.0/UnpublishFirmware" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>MD5 Checksum:</td>
            <td><form:input path="checksum" placeholder="MD5 checksum of firmware to unpublish"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
