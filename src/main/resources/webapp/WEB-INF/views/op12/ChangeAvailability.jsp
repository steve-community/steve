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
	<li><a class="highlight" href="${ctxPath}/manager/operations/${opVersion}/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ClearCache">Clear Cache</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
    <%@ include file="../op-forms/ChangeAvailabilityForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>