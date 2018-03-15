<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/getConnectorIdsZeroAllowed.js" %>
    });
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a class="highlight" href="${ctxPath}/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
    <%@ include file="../op-forms/ChangeAvailabilityForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>