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
<%@ include file="../00-header.jsp" %>
<div class="content">
    <section><span>Signed Firmware Updates</span></section>
    <form:form action="${ctxPath}/manager/security/firmware" method="get">
        <table class="userInput">
            <tr>
                <td>ChargeBox ID:</td>
                <td>
                    <select name="chargeBoxId">
                        <option value="">Select...</option>
                        <c:forEach items="${chargeBoxIdList}" var="id">
                            <option value="${id}" ${id == selectedChargeBoxId ? 'selected' : ''}>${id}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td id="add_space">
                    <input type="submit" value="Get Status">
                </td>
            </tr>
        </table>
    </form:form>

    <c:if test="${not empty currentUpdate}">
        <br>
        <section><span>Current Firmware Update for ${selectedChargeBoxId}</span></section>
        <table class="res">
            <thead>
            <tr>
                <th>Property</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><strong>Firmware Location</strong></td>
                <td><encode:forHtml value="${currentUpdate.firmwareLocation}" /></td>
            </tr>
            <tr>
                <td><strong>Retrieve Date</strong></td>
                <td>${currentUpdate.retrieveDate}</td>
            </tr>
            <tr>
                <td><strong>Install Date</strong></td>
                <td>${currentUpdate.installDate}</td>
            </tr>
            <tr>
                <td><strong>Status</strong></td>
                <td>
                    <c:choose>
                        <c:when test="${currentUpdate.status == 'Downloaded'}">
                            <span style="color: blue; font-weight: bold;">DOWNLOADED</span>
                        </c:when>
                        <c:when test="${currentUpdate.status == 'Installed'}">
                            <span style="color: green; font-weight: bold;">INSTALLED</span>
                        </c:when>
                        <c:when test="${currentUpdate.status == 'InstallationFailed'}">
                            <span style="color: red; font-weight: bold;">INSTALLATION FAILED</span>
                        </c:when>
                        <c:otherwise>
                            ${currentUpdate.status}
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td><strong>Firmware Signature</strong></td>
                <td style="font-family: monospace; font-size: 10px; word-break: break-all;">
                    <encode:forHtml value="${currentUpdate.firmwareSignature}" />
                </td>
            </tr>
            <tr>
                <td><strong>Signing Certificate</strong></td>
                <td style="font-family: monospace; font-size: 10px;">
                    <pre><encode:forHtml value="${currentUpdate.signingCertificate}" /></pre>
                </td>
            </tr>
            </tbody>
        </table>
    </c:if>

    <c:if test="${not empty selectedChargeBoxId && empty currentUpdate}">
        <br>
        <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px;">
            <p><strong>No firmware update found for ${selectedChargeBoxId}</strong></p>
        </div>
    </c:if>

    <br>
    <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px;">
        <h3>About Signed Firmware Updates</h3>
        <p>OCPP 1.6 Security Edition supports signed firmware updates to ensure the integrity and authenticity of firmware installed on charge points.</p>
        <p>Use the <strong>OPERATIONS → OCPP v1.6</strong> menu to initiate a signed firmware update request to a charge point.</p>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>