<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<form:form action="${ctxPath}/manager/operations/v2.0/GetReport" modelAttribute="params">
    <section><span>Charge Points</span></section>
    <table class="userInput">
        <tr>
            <td>ChargeBox ID:</td>
            <td>
                <form:select path="chargePointSelectList" multiple="true" size="5" cssClass="multi-select">
                    <form:options items="${cpList}" itemLabel="chargeBoxId" itemValue="chargeBoxPk"/>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="Unique request identifier"/></td>
        </tr>
        <tr>
            <td>Component Name:</td>
            <td><form:input path="componentName" placeholder="Optional: Filter by component name"/></td>
        </tr>
        <tr>
            <td>Variable Name:</td>
            <td><form:input path="variableName" placeholder="Optional: Filter by variable name"/></td>
        </tr>
        <tr>
            <td>Component Criteria:</td>
            <td>
                <form:select path="componentCriteria">
                    <form:option value="">-- Not specified --</form:option>
                    <form:option value="Active">Active</form:option>
                    <form:option value="Available">Available</form:option>
                    <form:option value="Enabled">Enabled</form:option>
                    <form:option value="Problem">Problem</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>