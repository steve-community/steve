<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
    <tr><td>Description:</td><td><form:input path="description"/></td></tr>
    <tr><td>Stack Level (integer):</td><td><form:input path="stackLevel"/></td></tr>
    <tr><td>Charging Profile Purpose:</td>
        <td>
            <form:select path="chargingProfilePurpose">
                <form:options items="${chargingProfilePurpose}"/>
            </form:select>
        </td>
    </tr>
    <tr><td>Charging Profile Kind:</td>
        <td>
            <form:select path="chargingProfileKind">
                <form:options items="${chargingProfileKind}"/>
            </form:select>
        </td>
    </tr>
    <tr><td>Recurrency Kind:</td>
        <td>
            <form:select path="recurrencyKind">
                <option value="" selected>-- Empty --</option>
                <form:options items="${recurrencyKind}"/>
            </form:select>
        </td>
    </tr>
    <tr><td>Valid From:</td><td><form:input path="validFrom" class="dateTimePicker"/></td></tr>
    <tr><td>Valid To:</td><td><form:input path="validTo" class="dateTimePicker"/></td></tr>
    <tr><td>Duration (in seconds):</td><td><form:input path="durationInSeconds"/></td></tr>
    <tr><td>Start Schedule:</td><td><form:input path="startSchedule" class="dateTimePicker"/></td></tr>
    <tr><td>Charging Rate Unit:</td>
        <td>
            <form:select path="chargingRateUnit">
                <form:options items="${chargingRateUnit}"/>
            </form:select>
        </td>
    </tr>
    <tr><td>Min Charging Rate (decimal, multiple of 0.1):</td><td><form:input path="minChargingRate"/></td></tr>

    <tr><td>Additional Note:</td><td><form:textarea path="note"/></td></tr>

    <tr><td></td>
        <td id="add_space">
            <input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
            <input type="submit" name="backToOverview" value="Back to Overview">
        </td></tr>
</table>