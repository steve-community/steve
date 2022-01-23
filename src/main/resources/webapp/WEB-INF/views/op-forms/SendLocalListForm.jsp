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
<form:form action="${ctxPath}/manager/operations/${opVersion}/SendLocalList" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput sll">
        <tr><td>Hash (String):</td><td><i>Optional, omitted for now</i></td></tr>
        <tr><td>List Version (integer):</td><td><form:input path="listVersion"/></td></tr>
        <tr><td>Update Type:</td>
            <td>
                <form:select path="updateType">
                    <form:options items="${updateType}" />
                </form:select>
            </td>
        </tr>
        <tr><td>Add/Update List:</td>
            <td>
                <form:select path="addUpdateList" disabled="true" multiple="true">
                    <form:options items="${idTagList}" />
                </form:select>
            </td>
        </tr>
        <tr><td>Delete List:</td>
            <td>
                <form:select path="deleteList" disabled="true" multiple="true">
                    <form:options items="${idTagList}" />
                </form:select>
            </td>
        </tr>
        <tr><td>Send empty list?:</td><td><form:checkbox path="sendEmptyListWhenFull" id="sendEmptyListWhenFull"/></td></tr>
        <tr><td><i>
            If selected and the update type is <b>FULL</b>, an empty list will be sent.<br>
            As a result, the charge point will remove all idTags from its list.
        </i></td><td></td></tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>