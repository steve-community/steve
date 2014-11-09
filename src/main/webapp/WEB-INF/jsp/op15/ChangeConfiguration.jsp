<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.5/ChangeAvailability">Change Availability</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.5/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.5/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.5/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.5/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.5/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.5/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.5/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="/steve/manager/operations/v1.5/ReserveNow">Reserve Now</a></li>
	<li><a href="/steve/manager/operations/v1.5/CancelReservation">Cancel Reservation</a></li>
	<li><a href="/steve/manager/operations/v1.5/DataTransfer">Data Transfer</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetConfiguration">Get Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="/steve/manager/operations/v1.5/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form method="POST" action="/steve/manager/operations/v1.5/ChangeConfiguration">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
<tr><td>Configuration Key:</td><td>
<select name="confKey" required>
<option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
<option value="HeartBeatInterval">HeartBeatInterval (in seconds)</option>
<option value="ConnectionTimeOut">ConnectionTimeOut (in seconds)</option>
<option value="ProximityContactRetries">ProximityContactRetries (in times)</option>
<option value="ProximityLockRetries">ProximityLockRetries (in times)</option>
<option value="ResetRetries">ResetRetries (in times)</option>
<option value="BlinkRepeat">BlinkRepeat (in times)</option>
<option value="LightIntensity">LightIntensity (in %)</option>
<option value="ChargePointId">ChargePointId (string)</option>
<option value="MeterValueSampleInterval">MeterValueSampleInterval (in seconds)</option>
<option value="ClockAlignedDataInterval">ClockAlignedDataInterval (in seconds)</option>
<option value="MeterValuesSampledData">MeterValuesSampledData (comma seperated list)</option>
<option value="MeterValuesAlignedData">MeterValuesAlignedData (comma seperated list)</option>
<option value="StopTxnSampledData">StopTxnSampledData (comma seperated list)</option>
<option value="StopTxnAlignedData">StopTxnAlignedData (comma seperated list)</option>
</select>
</td></tr>
<tr><td>Value:</td><td><input type="text" name="value" required></td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>