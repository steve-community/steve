<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<form:form action="${ctxPath}/manager/operations/v2.0/SetNetworkProfile" modelAttribute="params">
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
            <td>Configuration Slot:</td>
            <td><form:input path="configurationSlot" placeholder="Configuration slot number"/></td>
        </tr>
        <tr>
            <td>OCPP Interface:</td>
            <td>
                <form:select path="ocppInterface">
                    <form:option value="Wired0">Wired0</form:option>
                    <form:option value="Wired1">Wired1</form:option>
                    <form:option value="Wired2">Wired2</form:option>
                    <form:option value="Wired3">Wired3</form:option>
                    <form:option value="Wireless0">Wireless0</form:option>
                    <form:option value="Wireless1">Wireless1</form:option>
                    <form:option value="Wireless2">Wireless2</form:option>
                    <form:option value="Wireless3">Wireless3</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>OCPP Transport:</td>
            <td>
                <form:select path="ocppTransport">
                    <form:option value="JSON">JSON</form:option>
                    <form:option value="SOAP">SOAP</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>OCPP CSMS URL:</td>
            <td><form:input path="ocppCsmsUrl" placeholder="ws://host:port/path"/></td>
        </tr>
        <tr>
            <td>Message Timeout (seconds):</td>
            <td><form:input path="messageTimeout" placeholder="Default: 60"/></td>
        </tr>
        <tr>
            <td>Security Profile:</td>
            <td>
                <form:select path="securityProfile">
                    <form:option value="0">0 - No Security</form:option>
                    <form:option value="1">1 - Basic Authentication</form:option>
                    <form:option value="2">2 - TLS with Basic Authentication</form:option>
                    <form:option value="3">3 - TLS with Client Side Certificates</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>OCPP Version:</td>
            <td>
                <form:select path="ocppVersion">
                    <form:option value="2.0.1">OCPP 2.0.1</form:option>
                    <form:option value="2.1">OCPP 2.1</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>