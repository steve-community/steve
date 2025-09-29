<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="GetDisplayMessages" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Get Display Messages</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/GetDisplayMessages" modelAttribute="params">
    <section><span>
        Get Display Messages
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Retrieve display messages from the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Message IDs (comma-separated, optional):</td>
            <td><form:input path="messageIds" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Priority:</td>
            <td>
                <form:select path="priority">
                    <form:option value="">-- All --</form:option>
                    <form:option value="AlwaysFront">Always Front</form:option>
                    <form:option value="InFront">In Front</form:option>
                    <form:option value="NormalCycle">Normal Cycle</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>State:</td>
            <td>
                <form:select path="state">
                    <form:option value="">-- All --</form:option>
                    <form:option value="Charging">Charging</form:option>
                    <form:option value="Faulted">Faulted</form:option>
                    <form:option value="Idle">Idle</form:option>
                    <form:option value="Unavailable">Unavailable</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Execute"/>
            </td>
        </tr>
    </table>
</form:form>

<%@ include file="../00-footer.jsp" %>
