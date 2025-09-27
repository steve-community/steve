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
    <div>
    <section><span>
    Gateway Status
    <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
        <span>Monitor the status of roaming gateway protocols. Protocols are enabled when at least one active partner is configured.</span>
    </a>
    </span></section>
    <div>
        <table class="userInput">
            <thead><tr><th>Protocol</th><th>Status</th><th>Description</th></tr></thead>
            <tbody>
                <tr>
                    <td><strong>OCPI (Open Charge Point Interface)</strong></td>
                    <td>
                        <c:choose>
                            <c:when test="${ocpiEnabled}">
                                <span style="color: green; font-weight: bold;">● ENABLED</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: red; font-weight: bold;">● DISABLED</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>Protocol for roaming between charge point networks. Version 2.2.</td>
                </tr>
                <tr>
                    <td><strong>OICP (Open InterCharge Protocol)</strong></td>
                    <td>
                        <c:choose>
                            <c:when test="${oicpEnabled}">
                                <span style="color: green; font-weight: bold;">● ENABLED</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: red; font-weight: bold;">● DISABLED</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>Hubject's roaming protocol for EV charging interoperability. Version 2.3.</td>
                </tr>
            </tbody>
        </table>

        <br>

        <table class="userInput">
            <thead><tr><th>Configuration</th><th></th></tr></thead>
            <tbody>
                <tr>
                    <td>Configuration File:</td>
                    <td><code>application-prod.properties</code></td>
                </tr>
                <tr>
                    <td>Gateway Feature:</td>
                    <td>
                        <code>steve.gateway.enabled</code>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>Gateway must be enabled in configuration for protocols to function</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Partner Management:</td>
                    <td><a href="${ctxPath}/manager/gateway/partners">Configure Gateway Partners</a></td>
                </tr>
            </tbody>
        </table>

        <br>

        <section><span>Usage Information</span></section>
        <table class="userInput">
            <thead><tr><th>Item</th><th>Details</th></tr></thead>
            <tbody>
                <tr>
                    <td>OCPI Endpoints:</td>
                    <td>
                        <ul style="margin: 5px 0;">
                            <li><code>/ocpi/versions</code> - Version information</li>
                            <li><code>/ocpi/2.2/locations</code> - Charge point locations</li>
                            <li><code>/ocpi/2.2/sessions</code> - Charging sessions</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>OICP Endpoints:</td>
                    <td>
                        <ul style="margin: 5px 0;">
                            <li><code>/oicp/2.3/evse-data</code> - Charge point data</li>
                            <li><code>/oicp/2.3/evse-status</code> - Connector status</li>
                            <li><code>/oicp/2.3/authorize-start</code> - Start authorization</li>
                            <li><code>/oicp/2.3/authorize-stop</code> - Stop authorization</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>Security:</td>
                    <td>All tokens are BCrypt-hashed (strength 12) before storage</td>
                </tr>
            </tbody>
        </table>
    </div>
</div></div>
<%@ include file="../00-footer.jsp" %>