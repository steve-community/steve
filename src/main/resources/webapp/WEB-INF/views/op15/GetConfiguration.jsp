<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="${ctxPath}/manager/operations/v1.5/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/ChangeConfiguration">Change Configuration</a></li>
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
	<li><a class="highlight" href="${ctxPath}/manager/operations/v1.5/GetConfiguration">Get Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.5/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form:form action="${ctxPath}/manager/operations/v1.5/GetConfiguration" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.5</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
    <tr><td style="vertical-align:top"><input type="button" value="Select All" onClick="selectAll(document.getElementById('confKeyList'))"><input type="button" value="Select None" onClick="selectNone(document.getElementById('confKeyList'))">
    <div class="info"><b>Info:</b> If none selected, the charge point returns a list of <b>all</b> configuration settings.</div>
    </td>
    <td>
        <form:select path="confKeyList" multiple="true" size="14" >
            <form:options items="${ocpp15ConfKeys}" />
        </form:select>
    </td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>