<form:form action="${ctxPath}/manager/operations/${opVersion}/TriggerMessage" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.6</span></section>
    <%@ include file="../00-cp-single.jsp" %>
	<section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Requested Message :</td>
            <td>
				<form:select path="triggerMessage">
                    <form:options items="${triggerMessage}" itemLabel="text" />
                </form:select>
            </td>
        </tr>
		<tr>
			<td>Connector ID (integer > 0):</td>
			<td><form:input path="connectorId" placeholder="optional"/></td>
		</tr>
		<tr>
            <td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>