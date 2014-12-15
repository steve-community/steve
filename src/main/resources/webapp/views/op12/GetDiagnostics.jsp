<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="../../static/js/snippets/datepicker-past.js" %>
});
</script>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form method="POST" action="/steve/manager/operations/v1.2/GetDiagnostics">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
	<tr><td>Location (directory URI):</td><td><input type="text" name="location" required></td></tr>
	<tr><td>Retries (integer):</td><td><input type="number" min="0" name="retries" placeholder="optional"></td></tr>
	<tr><td>Retry Interval (integer):</td><td><input type="number" min="0" name="retryInterval" placeholder="optional"></td></tr>
	<tr><td>Start Date/Time (ex: 2011-12-21 at 11:30):</td>
		<td>
			<input type="text" name="startDate" id="startDate" class="datepicker" placeholder="optional"> at
			<input type="text" name="startTime" class="timepicker" placeholder="optional">
		</td>
	</tr>
	<tr><td>Stop Date/Time (ex: 2011-12-21 at 11:30):</td>
		<td>
			<input type="text" name="stopDate" id="stopDate" class="datepicker" placeholder="optional"> at
			<input type="text" name="stopTime" class="timepicker" placeholder="optional">
		</td>
	</tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="../00-footer.jsp" %>