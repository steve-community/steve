<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<div class="left-menu">
<ul>
	<li><a href="${servletPath}/ChangeAvailability">Change Availability</a></li>
	<li><a href="${servletPath}/ChangeConfiguration">Change Configuration</a></li>
	<li><a class="highlight" href="${servletPath}/ClearCache">Clear Cache</a></li>
	<li><a href="${servletPath}/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${servletPath}/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${servletPath}/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${servletPath}/Reset">Reset</a></li>
	<li><a href="${servletPath}/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${servletPath}/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form method="POST" action="${servletPath}/ClearCache">
<%@ include file="00-cp-multiple.jsp" %>
<section><span>Parameters</span></section>
<center><i>No parameters required.</i></center><div class="submit-button"><input type="submit" value="Perform"></div>
</form>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>