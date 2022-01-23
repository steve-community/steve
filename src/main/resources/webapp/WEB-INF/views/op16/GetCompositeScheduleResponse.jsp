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
<%@ include file="../00-header.jsp" %>
<div class="content">
    <div>
        <center>
            <table id='details' class='cpd'>
                <thead><tr><th>GetCompositeScheduleResponse</th><th></th></tr></thead>
                <tr><td>ChargeBox ID:</td><td>${chargeBoxId}</td></tr>
                <tr><td>Connector ID:</td><td>${response.connectorId}</td></tr>
                <tr><td>Schedule Start:</td><td>${response.scheduleStart}</td></tr>
                <tr><td>Duration (in seconds):</td><td>${response.chargingSchedule.duration}</td></tr>
                <tr><td>Start Schedule:</td><td>${response.chargingSchedule.startSchedule}</td></tr>
                <tr><td>Charging Rate Unit:</td><td>${response.chargingSchedule.chargingRateUnit}</td></tr>
                <tr><td>Min Charging Rate:</td><td>${response.chargingSchedule.minChargingRate}</td></tr>
            </table>
        </center>
        <br>
        <section><span>Schedule Periods</span></section>
        <table class="res">
            <thead>
            <tr>
                <th>Start Period (in sec)</th>
                <th>Power Limit (in amperes)</th>
                <th>Number Phases</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${response.chargingSchedule.chargingSchedulePeriod}" var="element">
                <tr>
                    <td>${element.startPeriod}</td>
                    <td>${element.limit}</td>
                    <td>${element.numberPhases}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>
