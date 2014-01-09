<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="${servletPath}/ChangeAvailability">Change Availability</a></li>
	<li><a href="${servletPath}/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${servletPath}/ClearCache">Clear Cache</a></li>
	<li><a href="${servletPath}/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${servletPath}/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${servletPath}/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${servletPath}/Reset">Reset</a></li>
	<li><a href="${servletPath}/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${servletPath}/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${servletPath}/ReserveNow">Reserve Now</a></li>
	<li><a href="${servletPath}/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${servletPath}/DataTransfer">Data Transfer</a></li>
	<li><a class="highlight" href="${servletPath}/GetConfiguration">Get Configuration</a></li>
	<li><a href="${servletPath}/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${servletPath}/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form method="POST" action="${servletPath}/GetConfiguration">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table>
<tr><td style="vertical-align:top"><input type="button" value="Select All" onClick="selectAll(document.getElementById('confKeys'))"><input type="button" value="Select None" onClick="selectNone(document.getElementById('confKeys'))">
<div class="info"><b>Info:</b> If none selected, the charge point returns a list of <b>all</b> configuration settings.</div>
</td>
<td>
<select name="confKeys" id="confKeys" size="14" multiple>
<option value="HeartBeatInterval">HeartBeatInterval</option>
<option value="ConnectionTimeOut">ConnectionTimeOut</option>
<option value="ProximityContactRetries">ProximityContactRetries</option>
<option value="ProximityLockRetries">ProximityLockRetries</option>
<option value="ResetRetries">ResetRetries</option>
<option value="BlinkRepeat">BlinkRepeat</option>
<option value="LightIntensity">LightIntensity</option>
<option value="ChargePointId">ChargePointId</option>
<option value="MeterValueSampleInterval">MeterValueSampleInterval</option>
<option value="ClockAlignedDataInterval">ClockAlignedDataInterval</option>
<option value="MeterValuesSampledData">MeterValuesSampledData</option>
<option value="MeterValuesAlignedData">MeterValuesAlignedData</option>
<option value="StopTxnSampledData">StopTxnSampledData</option>
<option value="StopTxnAlignedData">StopTxnAlignedData</option>
</select>
</td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>