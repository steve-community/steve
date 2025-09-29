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
            <h2>OCPP 2.0 Trusted Networks</h2>
            <p>Connections from trusted networks skip authentication requirements.</p>

            <c:if test="${not empty success}">
                <div class="success">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <section><span>Add Trusted Network</span></section>
            <form action="${ctxPath}/manager/ocpp20/auth/trusted-networks/add" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <table class="userInput">
                    <tr>
                        <td>Network (CIDR):</td>
                        <td>
                            <input type="text" name="cidr" placeholder="192.168.1.0/24 or 10.0.0.5/32" required/>
                            <div class="info">Enter IP address or network in CIDR notation</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Description:</td>
                        <td>
                            <input type="text" name="description" placeholder="Office network" size="40"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <input type="submit" value="Add Network"/>
                        </td>
                    </tr>
                </table>
            </form>

            <section><span>Current Trusted Networks</span></section>
            <table class="res">
                <thead>
                    <tr>
                        <th>Network CIDR</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${networks}" var="network">
                        <tr>
                            <td>${network.networkCidr}</td>
                            <td>${network.description}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${network.enabled}">
                                        <span style="color: green;">Enabled</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: red;">Disabled</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="${ctxPath}/manager/ocpp20/auth/trusted-networks/remove/${network.networkCidr}"
                                      method="post" style="display:inline;">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <input type="submit" value="Remove" onclick="return confirm('Remove this trusted network?')"/>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty networks}">
                        <tr>
                            <td colspan="4" style="text-align: center;">No trusted networks configured</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <section><span>Examples</span></section>
            <div class="info">
                <ul>
                    <li><b>Single Host:</b> 192.168.1.100/32</li>
                    <li><b>Class C Network:</b> 192.168.1.0/24 (256 addresses)</li>
                    <li><b>Class B Network:</b> 172.16.0.0/16 (65,536 addresses)</li>
                    <li><b>Class A Network:</b> 10.0.0.0/8 (16,777,216 addresses)</li>
                    <li><b>IPv6 Single Host:</b> 2001:db8::1/128</li>
                    <li><b>IPv6 Network:</b> 2001:db8::/32</li>
                </ul>
            </div>
        </div>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>