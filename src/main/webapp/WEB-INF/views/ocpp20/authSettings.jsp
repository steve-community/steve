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
    <div class="left-menu">
        <ul>
            <li><a href="${ctxPath}/manager/home">Main Menu</a></li>
            <li><a href="${ctxPath}/manager/ocpp20/auth">Authentication Settings</a></li>
            <li><a href="${ctxPath}/manager/ocpp20/auth/trusted-networks">Trusted Networks</a></li>
            <li><a href="${ctxPath}/manager/ocpp20/auth/ip-whitelist">IP Whitelist</a></li>
            <li><a href="${ctxPath}/manager/ocpp20/auth/ip-blacklist">IP Blacklist</a></li>
            <li><a href="${ctxPath}/manager/ocpp20/auth/certificates">Client Certificates</a></li>
        </ul>
    </div>
    <div class="main">
        <div class="container">
            <h2>OCPP 2.0 Authentication Settings</h2>

            <c:if test="${not empty success}">
                <div class="success">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <form action="${ctxPath}/manager/ocpp20/auth/settings" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <section><span>Authentication Mode</span></section>
                <table class="userInput">
                    <tr>
                        <td>Authentication Mode:</td>
                        <td>
                            <select name="authMode">
                                <option value="NONE" ${authMode == 'NONE' ? 'selected' : ''}>None - No Authentication</option>
                                <option value="BASIC" ${authMode == 'BASIC' ? 'selected' : ''}>Basic - Username/Password</option>
                                <option value="CERTIFICATE" ${authMode == 'CERTIFICATE' ? 'selected' : ''}>Certificate - Client Certificates Only</option>
                                <option value="COMBINED" ${authMode == 'COMBINED' ? 'selected' : ''}>Combined - Basic + Certificate</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div class="info">
                                <ul>
                                    <li><b>None:</b> No authentication required (not recommended for production)</li>
                                    <li><b>Basic:</b> Traditional username/password authentication</li>
                                    <li><b>Certificate:</b> Client certificate authentication only</li>
                                    <li><b>Combined:</b> Either basic auth or certificate can be used</li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </table>

                <section><span>Security Options</span></section>
                <table class="userInput">
                    <tr>
                        <td>Allow No Authentication:</td>
                        <td>
                            <input type="checkbox" name="allowNoAuth" value="true" ${allowNoAuth ? 'checked' : ''}/>
                            <span class="info">Allow connections without any authentication (for testing)</span>
                        </td>
                    </tr>
                    <tr>
                        <td>Require Certificate:</td>
                        <td>
                            <input type="checkbox" name="requireCertificate" value="true" ${requireCertificate ? 'checked' : ''}/>
                            <span class="info">Require client certificate when in COMBINED mode</span>
                        </td>
                    </tr>
                    <tr>
                        <td>Validate Certificate Chain:</td>
                        <td>
                            <input type="checkbox" name="validateCertChain" value="true" ${validateCertChain ? 'checked' : ''}/>
                            <span class="info">Validate the full certificate chain to trusted CA</span>
                        </td>
                    </tr>
                    <tr>
                        <td>Trusted Network Auth Bypass:</td>
                        <td>
                            <input type="checkbox" name="trustedNetworkAuthBypass" value="true" ${trustedNetworkAuthBypass ? 'checked' : ''}/>
                            <span class="info">Skip authentication for connections from trusted networks</span>
                        </td>
                    </tr>
                </table>

                <section><span>Quick Links</span></section>
                <table class="userInput">
                    <tr>
                        <td colspan="2">
                            <a href="${ctxPath}/manager/ocpp20/auth/trusted-networks" class="button">Manage Trusted Networks</a>
                            <a href="${ctxPath}/manager/ocpp20/auth/ip-whitelist" class="button">Manage IP Whitelist</a>
                            <a href="${ctxPath}/manager/ocpp20/auth/ip-blacklist" class="button">Manage IP Blacklist</a>
                            <a href="${ctxPath}/manager/ocpp20/auth/certificates" class="button">View Client Certificates</a>
                        </td>
                    </tr>
                </table>

                <div class="submit-button">
                    <input type="submit" value="Save Settings"/>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>