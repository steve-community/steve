<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-past.js" %>
    });
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ChangeAvailability">Change Availability</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ClearCache">Clear Cache</a></li>
	<li><a class="highlight" href="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/Reset">Reset</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/UnlockConnector">Unlock Connector</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ReserveNow">Reserve Now</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/CancelReservation">Cancel Reservation</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/DataTransfer">Data Transfer</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/GetConfiguration">Get Configuration</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/GetLocalListVersion">Get Local List Version</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/SendLocalList">Send Local List</a></li>
	<hr>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/TriggerMessage">Trigger Message</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/GetCompositeSchedule">Get Composite Schedule</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/ClearChargingProfile">Clear Charging Profile</a></li>
	<li><a href="${ctxPath}/manager/operations/${opVersion}/SetChargingProfile">Set Charging Profile</a></li>
</ul>
</div>
<div class="op16-content">
    <%@ include file="../op-forms/GetDiagnosticsForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>