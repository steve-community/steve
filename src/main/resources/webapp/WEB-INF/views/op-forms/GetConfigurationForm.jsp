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
<form:form action="${ctxPath}/manager/operations/${opVersion}/GetConfiguration" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td style="vertical-align:top"><input type="button" value="Select All" onClick="selectAll(document.getElementById('confKeyList'))"><input type="button" value="Select None" onClick="selectNone(document.getElementById('confKeyList'))">
            <div class="info"><b>Info:</b> If none selected, the charge point returns a list of <b>all</b> configuration settings.</div>
        </td>
            <td>
                <form:select path="confKeyList" multiple="true" size="14" >
                    <form:options items="${ocppConfKeys}" />
                </form:select>
            </td>
        </tr>
        <tr><td>Custom Configuration Keys:</td><td><form:input path="commaSeparatedCustomConfKeys" placeholder="optional comma separated list" /></td></tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>