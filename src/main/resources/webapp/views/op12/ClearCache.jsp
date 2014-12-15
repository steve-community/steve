<%@ include file="../00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form method="POST" action="/steve/manager/operations/v1.2/ClearCache">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<center><i>No parameters required.</i></center><div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="../00-footer.jsp" %>