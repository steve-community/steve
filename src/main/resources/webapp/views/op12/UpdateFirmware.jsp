<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../../static/js/snippets/dateTimePicker-future.js" %>
    });
</script>
<div class="content">
<div class="left-menu">
<ul>
	<li><a href="/steve/manager/operations/v1.2/ChangeAvailability">Change Availability</a></li>
	<li><a href="/steve/manager/operations/v1.2/ChangeConfiguration">Change Configuration</a></li>
	<li><a href="/steve/manager/operations/v1.2/ClearCache">Clear Cache</a></li>
	<li><a href="/steve/manager/operations/v1.2/GetDiagnostics">Get Diagnostics</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStartTransaction">Remote Start Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/RemoteStopTransaction">Remote Stop Transaction</a></li>
	<li><a href="/steve/manager/operations/v1.2/Reset">Reset</a></li>
	<li><a href="/steve/manager/operations/v1.2/UnlockConnector">Unlock Connector</a></li>
	<li><a class="highlight" href="/steve/manager/operations/v1.2/UpdateFirmware">Update Firmware</a></li>
</ul>
</div>
<div class="op-content">
<form:form action="/steve/manager/operations/v1.2/UpdateFirmware" modelAttribute="params">
    <section><span>Charge Points with OCPP v1.2</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Location (directory URI):</td><td><form:input path="location" /></td></tr>
        <tr><td>Retries (integer):</td><td><form:input path="retries" placeholder="optional" /></td></tr>
        <tr><td>Retry Interval (integer):</td><td><form:input path="retryInterval" placeholder="optional" /></td></tr>
        <tr><td>Retrieve Date/Time:</td>
            <td>
                <form:input path="retrieve" cssClass="dateTimePicker"/>
            </td>
        </tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>