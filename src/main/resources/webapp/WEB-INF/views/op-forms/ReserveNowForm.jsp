<form:form action="${ctxPath}/manager/operations/${opVersion}/ReserveNow" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Connector ID:</td>
            <td><form:select path="connectorId" disabled="true" /></td>
        </tr>
        <tr><td>Expiry Date/Time:</td>
            <td>
                <form:input path="expiry" cssClass="dateTimePicker" />
            </td>
        </tr>
        <tr><td>OCPP ID Tag:</td>
            <td>
                <form:select path="idTag">
                    <form:options items="${idTagList}" />
                </form:select>
            </td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>