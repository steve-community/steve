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
    <section><span>Security Configuration</span></section>

    <table class="res">
        <thead>
        <tr>
            <th>Setting</th>
            <th>Value</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><strong>Security Profile</strong></td>
            <td>
                ${securityProfile}
                <c:choose>
                    <c:when test="${securityProfile == 0}">
                        (Unsecured - Basic Authentication)
                    </c:when>
                    <c:when test="${securityProfile == 1}">
                        (Basic Authentication)
                    </c:when>
                    <c:when test="${securityProfile == 2}">
                        (TLS with Basic Authentication)
                    </c:when>
                    <c:when test="${securityProfile == 3}">
                        (TLS with Client Certificate)
                    </c:when>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td><strong>TLS Enabled</strong></td>
            <td>
                <c:choose>
                    <c:when test="${tlsEnabled}">
                        <span style="color: green; font-weight: bold;">YES</span>
                    </c:when>
                    <c:otherwise>
                        <span style="color: red; font-weight: bold;">NO</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <c:if test="${tlsEnabled}">
            <tr>
                <td><strong>Keystore Path</strong></td>
                <td><encode:forHtml value="${keystorePath}" /></td>
            </tr>
            <tr>
                <td><strong>Keystore Type</strong></td>
                <td>${keystoreType}</td>
            </tr>
            <tr>
                <td><strong>Truststore Path</strong></td>
                <td><encode:forHtml value="${truststorePath}" /></td>
            </tr>
            <tr>
                <td><strong>Truststore Type</strong></td>
                <td>${truststoreType}</td>
            </tr>
            <tr>
                <td><strong>Client Certificate Required</strong></td>
                <td>
                    <c:choose>
                        <c:when test="${clientAuthRequired}">
                            <span style="color: green; font-weight: bold;">YES</span>
                        </c:when>
                        <c:otherwise>
                            <span style="color: red;">NO</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td><strong>TLS Protocols</strong></td>
                <td>${tlsProtocols}</td>
            </tr>
        </c:if>
        <tr>
            <td><strong>Certificate Signing Service</strong></td>
            <td>
                <c:choose>
                    <c:when test="${signingServiceInitialized}">
                        <span style="color: green; font-weight: bold;">INITIALIZED</span>
                    </c:when>
                    <c:otherwise>
                        <span style="color: red; font-weight: bold;">NOT INITIALIZED</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        </tbody>
    </table>

    <br>
    <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px;">
        <h3>Configuration Notes</h3>
        <ul>
            <li><strong>Security Profile 0:</strong> No security, basic authentication only</li>
            <li><strong>Security Profile 1:</strong> HTTP basic authentication with OCPP transport security</li>
            <li><strong>Security Profile 2:</strong> TLS with server-side certificate and basic authentication</li>
            <li><strong>Security Profile 3:</strong> TLS with mutual authentication (client certificates required)</li>
        </ul>
        <p>Configuration is managed through <code>application.properties</code> or <code>application.yml</code></p>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>