<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<form:form action="${ctxPath}/manager/operations/v2.0/GetBaseReport" modelAttribute="params">
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
            <td>Report Base:</td>
            <td>
                <form:select path="reportBase">
                    <form:option value="ConfigurationInventory">Configuration Inventory</form:option>
                    <form:option value="FullInventory">Full Inventory</form:option>
                    <form:option value="SummaryInventory">Summary Inventory</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>