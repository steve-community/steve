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
            </td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>