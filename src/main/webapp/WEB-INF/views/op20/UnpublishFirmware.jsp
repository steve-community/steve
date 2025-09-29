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
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/getChargepointList.js" %>
    });
</script>
<div class="content">
    <div class="left-menu">
        <ul>
            <li><a href="${ctxPath}/manager/operations/v2.0">OCPP 2.0</a></li>
        </ul>
    </div>
    <div class="main">
        <div class="container">
            <form:form action="${ctxPath}/manager/operations/v2.0/UnpublishFirmware" modelAttribute="params">
                <section><span>Charge Points</span></section>
                <table class="userInput">
                    <tr>
                        <td>Charge Point:</td>
                        <td>
                            <form:select path="chargePointSelectList" id="cp" multiple="false" size="1" cssClass="select-one">
                                <option value="" selected>-- Choose --</option>
                                <form:options items="${cpList}" itemLabel="chargeBoxId" itemValue="chargeBoxPk"/>
                            </form:select>
                        </td>
                    </tr>
                </table>
                <section><span>Parameters</span></section>
                <table class="userInput">
                    <tr>
                        <td>Checksum (MD5)*:</td>
                        <td><form:input path="checksum" placeholder="32 character MD5 hash of firmware to unpublish"/></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div class="info">* Required field - Enter the MD5 checksum of the firmware to unpublish</div>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div class="submit-button">
                                <input type="submit" value="Perform"/>
                            </div>
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>