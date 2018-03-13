<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
	$(document).ready(function() {
		<%@ include file="../snippets/confKeySelect.js" %>
	});
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="${ctxPath}/manager/operations/v1.5/ChangeAvailability">Change Availability</a></li>
	<li><a class="highlight" href="${ctxPath}/manager/operations/v1.5/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/ClearCache">Clear Cache</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/v1.5/ReserveNow">Reserve Now</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/DataTransfer">Data Transfer</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/GetConfiguration">Get Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form:form action="${ctxPath}/manager/operations/v1.5/ChangeConfiguration" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.5</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Key Type:</td>
            <td><form:select path="keyType">
                <form:options items="${type}" itemLabel="value"/>
            </form:select>
            </td>
        </tr>
        <tr>
            <td>Configuration Key:</td>
            <td>
                <form:select path="confKey">
                    <form:options items="${confKey}" itemLabel="text" />
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Custom Configuration Key:</td>
            <td><form:input path="customConfKey"/></td>
        </tr>
    <tr><td>Value:</td><td><form:input path="value" /></td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>