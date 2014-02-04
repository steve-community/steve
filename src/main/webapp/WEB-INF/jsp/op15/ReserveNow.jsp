<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="/WEB-INF/jsp/00-js-snippets/datepicker.js" %>
<%@ include file="/WEB-INF/jsp/00-js-snippets/getConnectorIdsZeroAllowed.js" %>
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
	<li><a href="${servletPath}/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a class="highlight" href="${servletPath}/ReserveNow">Reserve Now</a></li>
	<li><a href="${servletPath}/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${servletPath}/DataTransfer">Data Transfer</a></li>
	<li><a href="${servletPath}/GetConfiguration">Get Configuration</a></li>
	<li><a href="${servletPath}/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${servletPath}/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form method="POST" action="${servletPath}/ReserveNow">
<%@ include file="00-cp-single.jsp" %>
<section><span>Parameters</span></section>
<table class="userInput">
	<tr><td>Connector ID:</td>
		<td><select name="connectorId" id="connectorId" required disabled></select></td>
	</tr>
	<tr><td>Expiry Date/Time (ex: 2011-12-21 at 11:30):</td>
		<td>
			<input type="text" name="expiryDate" class="datepicker" required> at 
			<input type="text" name="expiryTime" class="timepicker" placeholder="optional">
		</td>
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
	</td></tr>
	<tr><td>Parent ID Tag:</td>
	<td>
		<select name="parentIdTag">
		<option value="" selected="selected">-- Empty --</option>
		<%-- Start --%>
		<c:forEach items="${userList}" var="user">
		<option value="${user.idTag}">${user.idTag}</option>
		</c:forEach>
		<%-- End --%>
		</select>
	</td></tr>
</table>
<div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>