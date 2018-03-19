<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<div class="content">
    <div class="left-menu">
        <ul>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/ChangeAvailability">Change Availability</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/ChangeConfiguration">Change Configuration</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/ClearCache">Clear Cache</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics">Get Diagnostics</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStartTransaction">Remote Start Transaction</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStopTransaction">Remote Stop Transaction</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/Reset">Reset</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/UnlockConnector">Unlock Connector</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/UpdateFirmware">Update Firmware</a></li>
            <hr>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/ReserveNow">Reserve Now</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/CancelReservation">Cancel Reservation</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/DataTransfer">Data Transfer</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/GetConfiguration">Get Configuration</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/GetLocalListVersion">Get Local List Version</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/SendLocalList">Send Local List</a></li>
            <hr>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/GetCompositeSchedule">Get Composite Schedule</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/ClearChargingProfile">Clear Charging Profile</a></li>
            <li><a href="${ctxPath}/manager/operations/${opVersion}/SetChargingProfile">Set Charging Profile</a></li>
            <li><a class="highlight" href="${ctxPath}/manager/operations/${opVersion}/TriggerMessage">Trigger Message</a></li>
        </ul>
    </div>
    <div class="op16-content">
        <%@ include file="../op-forms/TriggerMessage.jsp" %>
    </div></div>
<%@ include file="../00-footer.jsp" %>