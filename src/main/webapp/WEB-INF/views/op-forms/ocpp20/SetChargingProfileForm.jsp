<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<form:form modelAttribute="params">
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
            <td>EVSE ID:</td>
            <td><form:input path="evseId" placeholder="Optional: Specific EVSE (0 for all)"/></td>
        </tr>
        <tr><td colspan="2"><hr></td></tr>
        <tr><td colspan="2"><b>Profile Settings</b></td></tr>
        <tr>
            <td>Profile ID:</td>
            <td><form:input path="profileId" placeholder="Unique profile identifier"/></td>
        </tr>
        <tr>
            <td>Stack Level:</td>
            <td><form:input path="stackLevel" placeholder="Stack priority (0=highest)"/></td>
        </tr>
        <tr>
            <td>Profile Purpose:</td>
            <td>
                <form:select path="profilePurpose">
                    <form:option value="ChargingStationMaxProfile">Charging Station Max Profile</form:option>
                    <form:option value="TxDefaultProfile">Transaction Default Profile</form:option>
                    <form:option value="TxProfile">Transaction Profile</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Profile Kind:</td>
            <td>
                <form:select path="profileKind">
                    <form:option value="Absolute">Absolute</form:option>
                    <form:option value="Recurring">Recurring</form:option>
                    <form:option value="Relative">Relative</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Recurrency Kind:</td>
            <td>
                <form:select path="recurrencyKind">
                    <form:option value="">-- Not recurring --</form:option>
                    <form:option value="Daily">Daily</form:option>
                    <form:option value="Weekly">Weekly</form:option>
                </form:select>
            </td>
        </tr>
        <tr><td colspan="2"><hr></td></tr>
        <tr><td colspan="2"><b>Schedule Settings</b></td></tr>
        <tr>
            <td>Duration (seconds):</td>
            <td><form:input path="duration" placeholder="Schedule duration in seconds"/></td>
        </tr>
        <tr>
            <td>Start Schedule:</td>
            <td><form:input path="startSchedule" placeholder="Optional: ISO 8601 datetime"/></td>
        </tr>
        <tr>
            <td>Charging Rate Unit:</td>
            <td>
                <form:select path="chargingRateUnit">
                    <form:option value="W">Watts (W)</form:option>
                    <form:option value="A">Amperes (A)</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Min Charging Rate:</td>
            <td><form:input path="minChargingRate" placeholder="Optional: Minimum rate"/></td>
        </tr>
        <tr><td colspan="2"><hr></td></tr>
        <tr><td colspan="2"><b>Schedule Period</b></td></tr>
        <tr>
            <td>Start Period (seconds):</td>
            <td><form:input path="startPeriod" placeholder="Start time within schedule"/></td>
        </tr>
        <tr>
            <td>Power/Current Limit:</td>
            <td><form:input path="limit" placeholder="Max power (W) or current (A)"/></td>
        </tr>
        <tr>
            <td>Number of Phases:</td>
            <td>
                <form:select path="numberPhases">
                    <form:option value="1">1 Phase</form:option>
                    <form:option value="3">3 Phases</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>
<%@ include file="../../00-cp-multiple.jsp" %>