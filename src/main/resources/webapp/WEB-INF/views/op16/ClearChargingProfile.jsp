<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/getConnectorIds.js" %>
    });
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="${ctxPath}/manager/operations/v1.6/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/ClearCache">Clear Cache</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/v1.6/ReserveNow">Reserve Now</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/DataTransfer">Data Transfer</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetConfiguration">Get Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/SendLocalList">Send Local List</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/v1.6/SetChargingProfile">Set Charging Profile</a></li>
	<li><a class="highlight" href="${ctxPath}/manager/operations/v1.6/ClearChargingProfile">Clear Charging Profile</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetCompositeSchedule">Get Composite Schedule</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/TriggerMessage">Trigger Message</a></li>
</ul>
</div>
<div class="op16-content">
<form:form action="${ctxPath}/manager/operations/v1.6/ClearChargingProfile" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.6</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
     <!-- TODO: Input values -->
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>