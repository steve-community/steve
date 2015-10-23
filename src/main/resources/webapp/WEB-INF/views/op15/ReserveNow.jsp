<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/getConnectorIdsZeroAllowed.js" %>
        <%@ include file="../snippets/dateTimePicker-future.js" %>
    });
</script>
<div class="content">
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
	<li><a class="highlight" href="/steve/manager/operations/v1.5/ReserveNow">Reserve Now</a></li>
	<li><a href="/steve/manager/operations/v1.5/CancelReservation">Cancel Reservation</a></li>
	<li><a href="/steve/manager/operations/v1.5/DataTransfer">Data Transfer</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetConfiguration">Get Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.5/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="/steve/manager/operations/v1.5/SendLocalList">Send Local List</a></li>
</ul>
</div>
<div class="op15-content">
<form:form action="/steve/manager/operations/v1.5/ReserveNow" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.5</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Connector ID:</td>
            <td><form:select path="connectorId" disabled="true" /></td>
        </tr>
        <tr><td>Expiry Date/Time:</td>
            <td>
                <form:input path="expiry" cssClass="dateTimePicker" />
            </td>
        </tr>
        <tr><td>User ID Tag:</td>
        <td>
            <form:select path="idTag">
                <form:options items="${idTagList}" />
            </form:select>
        </td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>