<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="ReserveNow" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Reserve Now</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/ReserveNow" modelAttribute="params">
    <section><span>
        Reserve Now
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>This sends a Reserve Now request to the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Reservation ID:</td>
            <td><form:input path="id" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Expiry Date/Time:</td>
            <td><form:input path="expiryDateTime" cssClass="text" placeholder="yyyy-MM-ddTHH:mm" /></td>
        </tr>
        <tr>
            <td>ID Token:</td>
            <td><form:input path="idToken" cssClass="text" /></td>
        </tr>
        <tr>
            <td>ID Token Type:</td>
            <td>
                <form:select path="idTokenType">
                    <form:option value="">-- Select --</form:option>
                    <form:option value="Central">Central</form:option>
                    <form:option value="eMAID">eMAID</form:option>
                    <form:option value="ISO14443">ISO14443</form:option>
                    <form:option value="ISO15693">ISO15693</form:option>
                    <form:option value="KeyCode">KeyCode</form:option>
                    <form:option value="Local">Local</form:option>
                    <form:option value="MacAddress">MacAddress</form:option>
                    <form:option value="NoAuthorization">NoAuthorization</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>EVSE ID (Optional):</td>
            <td><form:input path="evseId" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Group ID Token (Optional):</td>
            <td><form:input path="groupIdToken" cssClass="text" /></td>
        </tr>
        <tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Reserve Now"/>
            </td>
        </tr>
    </table>
</form:form>

<%@ include file="../00-footer.jsp" %>