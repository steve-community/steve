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
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>