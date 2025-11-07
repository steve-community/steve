<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
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
<div class="left-menu">
<ul>
    <li><a ${activePage == 'ChangeAvailability'         ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ChangeAvailability">Change Availability</a></li>
    <li><a ${activePage == 'ChangeConfiguration'        ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ChangeConfiguration">Change Configuration</a></li>
    <li><a ${activePage == 'ClearCache'                 ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ClearCache">Clear Cache</a></li>
    <li><a ${activePage == 'GetDiagnostics'             ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetDiagnostics">Get Diagnostics</a></li>
    <li><a ${activePage == 'RemoteStartTransaction'     ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/RemoteStartTransaction">Remote Start Transaction</a></li>
    <li><a ${activePage == 'RemoteStopTransaction'      ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/RemoteStopTransaction">Remote Stop Transaction</a></li>
    <li><a ${activePage == 'Reset'                      ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/Reset">Reset</a></li>
    <li><a ${activePage == 'UnlockConnector'            ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/UnlockConnector">Unlock Connector</a></li>
    <li><a ${activePage == 'UpdateFirmware'             ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/UpdateFirmware">Update Firmware</a></li>
    <hr>
    <li><a ${activePage == 'ReserveNow'                 ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ReserveNow">Reserve Now</a></li>
    <li><a ${activePage == 'CancelReservation'          ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/CancelReservation">Cancel Reservation</a></li>
    <li><a ${activePage == 'DataTransfer'               ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/DataTransfer">Data Transfer</a></li>
    <li><a ${activePage == 'GetConfiguration'           ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetConfiguration">Get Configuration</a></li>
    <li><a ${activePage == 'GetLocalListVersion'        ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetLocalListVersion">Get Local List Version</a></li>
    <li><a ${activePage == 'SendLocalList'              ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/SendLocalList">Send Local List</a></li>
    <hr>
    <li><a ${activePage == 'TriggerMessage'             ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/TriggerMessage">Trigger Message</a></li>
    <li><a ${activePage == 'GetCompositeSchedule'       ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetCompositeSchedule">Get Composite Schedule</a></li>
    <li><a ${activePage == 'ClearChargingProfile'       ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ClearChargingProfile">Clear Charging Profile</a></li>
    <li><a ${activePage == 'SetChargingProfile'         ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/SetChargingProfile">Set Charging Profile</a></li>
    <li><a ${activePage == 'ExtendedTriggerMessage'     ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/ExtendedTriggerMessage"><i>Extended Trigger Message</i></a></li>
    <li><a ${activePage == 'GetLog'                     ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetLog"><i>Get Log</i></a></li>
    <li><a ${activePage == 'SignedUpdateFirmware'       ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/SignedUpdateFirmware"><i>Signed Update Firmware</i></a></li>
    <li><a ${activePage == 'GetInstalledCertificateIds' ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/GetInstalledCertificateIds"><i>Get Installed Certificate Ids</i></a></li>
    <li><a ${activePage == 'InstallCertificate'         ? 'class="highlight"' : ''} href="${ctxPath}/manager/operations/${opVersion}/InstallCertificate"><i>Install Certificate</i></a></li>
</ul>
</div>
