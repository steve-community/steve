<form:form action="${ctxPath}/manager/operations/${opVersion}/ChangeConfiguration" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Key Type:</td>
            <td><form:select path="keyType">
                <form:options items="${type}" itemLabel="value"/>
            </form:select>
            </td>
        </tr>
        <tr>
            <td>Configuration Key:</td>
            <td>
                <form:select path="confKey">
                    <form:options items="${ocppConfKeys}" />
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Custom Configuration Key:</td>
            <td><form:input path="customConfKey"/></td>
        </tr>
        <tr><td>Value:</td><td><form:input path="value" /></td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>