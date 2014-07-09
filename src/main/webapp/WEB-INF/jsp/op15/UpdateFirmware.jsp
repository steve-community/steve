<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="/WEB-INF/jsp/00-js-snippets/datepicker-future.js" %>
});
</script>
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
	<li><a class="highlight" href="${servletPath}/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${servletPath}/ReserveNow">Reserve Now</a></li>
	<li><a href="${servletPath}/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${servletPath}/DataTransfer">Data Transfer</a></li>
	<li><a href="${servletPath}/GetConfiguration">Get Configuration</a></li>
	<li><a href="${servletPath}/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${servletPath}/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form method="POST" action="${servletPath}/UpdateFirmware">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
	<tr><td>Location (URI):</td><td><input type="text" name="location" required></td></tr>
	<tr><td>Retries (integer):</td><td><input type="number" min="0" name="retries" placeholder="optional"></td></tr>
	<tr><td>Retry Interval (integer):</td><td><input type="number" min="0" name="retryInterval" placeholder="optional"></td></tr>
	<tr><td>Retrieve Date/Time (ex: 2011-12-21 at 11:30):</td>
		<td>
			<input type="text" name="retrieveDate" class="datepicker" required> at 
			<input type="text" name="retrieveTime" class="timepicker" placeholder="optional">
		</td>
	</tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>