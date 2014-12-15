<%@ include file="../00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.5/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.5/ChangeConfiguration">Change Configuration</a></li>
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
	<li><a class="highlight" href="/steve/manager/operations/v1.5/DataTransfer">Data Transfer</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetConfiguration">Get Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="/steve/manager/operations/v1.5/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form method="POST" action="/steve/manager/operations/v1.5/DataTransfer">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
<tr><td>Vendor ID (String):</td><td><input type="text" name="vendorId" required></td></tr>
<tr><td>Message ID (String):</td><td><input type="text" name="messageId" placeholder="optional"></td></tr>
<tr><td>Data (Text):</td><td><input type="text" name="data" placeholder="optional"></td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="../00-footer.jsp" %>