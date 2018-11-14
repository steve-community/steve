<%@ include file="../00-header.jsp" %>
<div class="content">
    <div>
        <center>
            <table id='details' class='cpd'>
                <thead><tr><th>GetCompositeScheduleResponse from ${chargeBoxId}</th><th></th></tr></thead>
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
