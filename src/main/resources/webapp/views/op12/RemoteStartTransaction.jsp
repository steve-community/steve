<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="../../static/js/snippets/getConnectorIdsZeroAllowed.js" %>
});
</script>
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form method="POST" action="/steve/manager/operations/v1.2/RemoteStartTransaction">
<%@ include file="00-cp-single.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
<tr><td>Connector ID:</td>
	<td><select name="connectorId" id="connectorId" required disabled></select></td>
</tr>
<tr><td>User ID Tag:</td>
	<td>
		<select name="idTag" required>
		<option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
		<%-- Start --%>
		<c:forEach items="${userList}" var="user">
		<option value="${user.idTag}">${user.idTag}</option>
		</c:forEach>
		<%-- End --%>
		</select>
	</td>
</tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="../00-footer.jsp" %>