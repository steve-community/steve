<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
		<%@ include file="../snippets/dateTimePicker.js" %>
    });
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="${ctxPath}/manager/operations/v1.6/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/ClearCache">Clear Cache</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/v1.6/ReserveNow">Reserve Now</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/DataTransfer">Data Transfer</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetConfiguration">Get Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/SendLocalList">Send Local List</a></li>
	<hr>
	<li><a class="highlight" href="${ctxPath}/manager/operations/v1.6/SetChargingProfile">Set Charging Profile</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/ClearChargingProfile">Clear Charging Profile</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/GetCompositeSchedule">Get Composite Schedule</a></li>
	<li><a href="${ctxPath}/manager/operations/v1.6/TriggerMessage">Trigger Message</a></li>
</ul>
</div>
<div class="op16-content">
<form:form action="${ctxPath}/manager/operations/v1.6/SetChargingProfile" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.6</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
    <tr><td>Connector ID :</td>
        <td><form:input path="connectorId" placeholder="required" /></td>
    </tr>
	<tr><td><b>Charging Profile</b></td></tr>
	<tr><td>Transaction ID :</td><td><i>Disabled for now</i></td></tr>
	<tr><td>Charging Profile ID (integer):</td><td><form:input path="chargingProfileId" placeholder="required" /></td></tr>
	<tr><td>Stack Level (integer):</td><td><form:input path="stackLevel" placeholder="required" /></td></tr>
	<tr>
		<td>Charging Profile Purpose :</td>
		<td>
			<form:select path="chargingProfilePurpose" >
				<form:options items="${chargingProfilePurpose}" itemLabel="text" />
			</form:select>
		</td>
	</tr>
	<tr>
		<td>Charging Profile Kind :</td>
		<td>
			<form:select path="chargingProfileKind" >
				<form:options items="${chargingProfileKind}" itemLabel="text" />
			</form:select>
		</td>
	</tr>
	<tr>
		<td>Recurrency Kind :</td>
		<td>
			<form:select path="recurrencyKind" >
				<form:option value="" label="optional"/>
				<form:options items="${recurrencyKind}" itemLabel="text" />
			</form:select>
		</td>
	</tr>
	<tr><td>Valid From :</td>
            <td>
                <form:input path="validFrom" cssClass="dateTimePicker" placeholder="optional" />
            </td>
        </tr>
	<tr><td>Valid To :</td>
		<td>
			<form:input path="validTo" cssClass="dateTimePicker" placeholder="optional" />
		</td>
	</tr>
	<tr><td><b>Charging Schedule</b></td></tr>
	<tr><td>Duration (integer) :</td><td><form:input path="duration" placeholder="optional" /></td></tr>
	<tr><td>Start Schedule :</td><td><form:input path="startSchedule" cssClass="dateTimePicker" placeholder="optional" /></td></tr>
	<td>Charging Rate Unit :</td>
	<td>
		<form:select path="chargingRateUnit" >
			<form:options items="${chargingRateUnit}" itemLabel="text" />
		</form:select>
	</td>
	<tr><td>Minimum Charging Rate (decimal) :</td><td><form:input path="minChargingRate" placeholder="optional" /></td></tr>
	<tr><td><b>Charging Schedule Period</b></td></tr>
	<tr><td>Start Period (integer) :</td><td><form:input path="startPeriod" placeholder="required" /></td></tr>
	<tr><td>Limit (decimal) :</td><td><form:input path="limit" placeholder="required" /></td></tr>
	<tr><td>Number of Phases (integer) :</td><td><form:input path="numberPhases" placeholder="optional" /></td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>