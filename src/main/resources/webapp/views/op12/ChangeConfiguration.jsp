<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form:form action="/steve/manager/operations/v1.2/ChangeConfiguration" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.2</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
    <tr>
    <td>Configuration Key:</td>
    <td>
        <form:select path="confKey">
            <form:options items="${confKey}" itemLabel="text" />
        </form:select>
    </td>
    </tr>
    <tr><td>Value:</td><td><form:input path="value" /></td></tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>