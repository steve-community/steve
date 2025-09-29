<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="SendLocalList" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Send Local List</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/SendLocalList" modelAttribute="params">
    <section><span>
        Send Local List
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>This sends a Send Local List request to the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Version Number:</td>
            <td><form:input path="versionNumber" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Update Type:</td>
            <td>
                <form:select path="updateType">
                    <form:option value="">-- Select --</form:option>
                    <form:option value="Differential">Differential</form:option>
                    <form:option value="Full">Full</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Authorization List (JSON):</td>
            <td><form:textarea path="authorizationList" cssClass="text" rows="5" placeholder="[]" /></td>
        </tr>
        <tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Send Local List"/>
            </td>
        </tr>
    </table>
</form:form>

<%@ include file="../00-footer.jsp" %>