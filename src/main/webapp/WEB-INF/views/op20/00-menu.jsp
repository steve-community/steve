<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" />
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
	<li><a ${param.menuItem == 'GetLocalListVersion' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetLocalListVersion">Get Local List Version</a></li>
	<li><a ${param.menuItem == 'GetTransactionStatus' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetTransactionStatus">Get Transaction Status</a></li>
	<li><a ${param.menuItem == 'UnlockConnector' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/UnlockConnector">Unlock Connector</a></li>
	<li><a ${param.menuItem == 'UpdateFirmware' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/UpdateFirmware">Update Firmware</a></li>
	<li><a ${param.menuItem == 'PublishFirmware' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/PublishFirmware">Publish Firmware</a></li>
	<li><a ${param.menuItem == 'UnpublishFirmware' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/UnpublishFirmware">Unpublish Firmware</a></li>
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
	<hr>
	<li><a ${param.menuItem == 'InstallCertificate' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/InstallCertificate">Install Certificate</a></li>
	<li><a ${param.menuItem == 'GetInstalledCertificateIds' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetInstalledCertificateIds">Get Installed Certificate IDs</a></li>
	<li><a ${param.menuItem == 'DeleteCertificate' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/DeleteCertificate">Delete Certificate</a></li>
	<li><a ${param.menuItem == 'CertificateSigned' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/CertificateSigned">Certificate Signed</a></li>
	<li><a ${param.menuItem == 'GetCertificateStatus' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetCertificateStatus">Get Certificate Status</a></li>
	<li><a ${param.menuItem == 'Get15118EVCertificate' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/Get15118EVCertificate">Get ISO 15118 EV Certificate</a></li>
	<hr>
	<li><a ${param.menuItem == 'CustomerInformation' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/CustomerInformation">Customer Information</a></li>
	<li><a ${param.menuItem == 'CostUpdated' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/CostUpdated">Cost Updated</a></li>
	<li><a ${param.menuItem == 'GetDisplayMessages' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetDisplayMessages">Get Display Messages</a></li>
	<li><a ${param.menuItem == 'SetDisplayMessage' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetDisplayMessage">Set Display Message</a></li>
	<li><a ${param.menuItem == 'ClearDisplayMessage' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ClearDisplayMessage">Clear Display Message</a></li>
	<hr>
	<li><a ${param.menuItem == 'SetMonitoringBase' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetMonitoringBase">Set Monitoring Base</a></li>
	<li><a ${param.menuItem == 'GetMonitoringReport' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/GetMonitoringReport">Get Monitoring Report</a></li>
	<li><a ${param.menuItem == 'SetMonitoringLevel' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetMonitoringLevel">Set Monitoring Level</a></li>
	<li><a ${param.menuItem == 'SetVariableMonitoring' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/SetVariableMonitoring">Set Variable Monitoring</a></li>
	<li><a ${param.menuItem == 'ClearVariableMonitoring' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/v2.0/ClearVariableMonitoring">Clear Variable Monitoring</a></li>
</ul>
</div>
