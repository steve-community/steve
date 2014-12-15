<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="../../static/js/snippets/getTransactionIds.js" %>
});
</script>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form method="POST" action="/steve/manager/operations/v1.2/RemoteStopTransaction">
<%@ include file="00-cp-single.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
<tr><td>ID of the Active Transaction:</td><td><select name="transactionId" id="transactionId" required disabled></select></td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="../00-footer.jsp" %>