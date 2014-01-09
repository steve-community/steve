<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="${servletPath}/ChangeAvailability">Change Availability</a></li>
	<li><a href="${servletPath}/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${servletPath}/ClearCache">Clear Cache</a></li>
	<li><a class="highlight" href="${servletPath}/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${servletPath}/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${servletPath}/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${servletPath}/Reset">Reset</a></li>
	<li><a href="${servletPath}/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${servletPath}/UpdateFirmware">Update Firmware</a></li>
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
<form method="POST" action="${servletPath}/GetDiagnostics">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table>
<tr><td>Location (directory URI):</td><td><input type="text" name="location"></td></tr>
<tr><td>Retries (integer):</td><td><input type="number" min="0" name="retries"></td></tr>
<tr><td>Retry Interval (integer):</td><td><input type="number" min="0" name="retryInterval"></td></tr>
<tr><td>Start time (ex: 2011-12-21 11:33):</td><td><input type="datetime" name="startTime"></td></tr>
<tr><td>Stop time (ex: 2011-12-21 11:33):</td><td><input type="datetime" name="stopTime"></td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>