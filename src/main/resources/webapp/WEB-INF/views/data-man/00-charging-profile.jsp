<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
    <thead><tr><th>Details</th><th></th></thead>
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
</table>
<br><br>
<section><span>Schedule Periods</span></section>
<table class="res" id="periodsTable">
    <thead>
    <tr>
        <th>Start Period (in sec)</th>
        <th>Power Limit (in Charging Rate Unit selected above)</th>
        <th>Number Phases</th>
        <th>
            <input type="button" id="addRow" value="Add Period">
        </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${form.schedulePeriodMap}" var="schedulePeriodMap" varStatus="status">
        <tr id="${schedulePeriodMap.key}">
            <td><form:input path="schedulePeriodMap[${schedulePeriodMap.key}].startPeriodInSeconds"/></td>
            <td><form:input path="schedulePeriodMap[${schedulePeriodMap.key}].powerLimit"/></td>
            <td><form:input path="schedulePeriodMap[${schedulePeriodMap.key}].numberPhases" placeholder="if empty, 3 will be assumed"/></td>
            <td><input type="button" class="removeRow" value="Delete"></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<table class="userInput">
    <tr>
        <td></td>
        <td id="add_space">
            <input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
            <input type="submit" name="backToOverview" value="Back to Overview">
        </td>
    </tr>
</table>