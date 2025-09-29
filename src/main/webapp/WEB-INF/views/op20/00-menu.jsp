<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<div class="left-menu">
<ul>
	<li><a ${param.menuItem == 'ChangeAvailability' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ChangeAvailability">Change Availability</a></li>
	<li><a ${param.menuItem == 'CancelReservation' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/CancelReservation">Cancel Reservation</a></li>
	<li><a ${param.menuItem == 'ClearCache' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ClearCache">Clear Cache</a></li>
	<li><a ${param.menuItem == 'GetLog' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetLog">Get Log</a></li>
	<li><a ${param.menuItem == 'RequestStartTransaction' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/RequestStartTransaction">Request Start Transaction</a></li>
	<li><a ${param.menuItem == 'RequestStopTransaction' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/RequestStopTransaction">Request Stop Transaction</a></li>
	<li><a ${param.menuItem == 'Reset' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/Reset">Reset</a></li>
	<li><a ${param.menuItem == 'ReserveNow' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ReserveNow">Reserve Now</a></li>
	<li><a ${param.menuItem == 'SendLocalList' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SendLocalList">Send Local List</a></li>
	<li><a ${param.menuItem == 'UnlockConnector' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/UnlockConnector">Unlock Connector</a></li>
	<li><a ${param.menuItem == 'UpdateFirmware' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/UpdateFirmware">Update Firmware</a></li>
	<hr>
	<li><a ${param.menuItem == 'DataTransfer' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/DataTransfer">Data Transfer</a></li>
	<li><a ${param.menuItem == 'GetVariables' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetVariables">Get Variables</a></li>
	<li><a ${param.menuItem == 'SetVariables' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetVariables">Set Variables</a></li>
	<li><a ${param.menuItem == 'GetBaseReport' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetBaseReport">Get Base Report</a></li>
	<li><a ${param.menuItem == 'GetReport' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetReport">Get Report</a></li>
	<li><a ${param.menuItem == 'SetNetworkProfile' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetNetworkProfile">Set Network Profile</a></li>
	<hr>
	<li><a ${param.menuItem == 'TriggerMessage' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/TriggerMessage">Trigger Message</a></li>
	<li><a ${param.menuItem == 'GetCompositeSchedule' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetCompositeSchedule">Get Composite Schedule</a></li>
	<li><a ${param.menuItem == 'ClearChargingProfile' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ClearChargingProfile">Clear Charging Profile</a></li>
	<li><a ${param.menuItem == 'GetChargingProfiles' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetChargingProfiles">Get Charging Profiles</a></li>
	<li><a ${param.menuItem == 'SetChargingProfile' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetChargingProfile">Set Charging Profile</a></li>
</ul>
</div>