<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="CustomerInformation" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Customer Information</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/CustomerInformation" modelAttribute="params">
    <section><span>
        Customer Information
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Request customer information from the selected charge point(s)</span>
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
            <td>Report:</td>
            <td><form:checkbox path="report" /></td>
        </tr>
        <tr>
            <td>Clear:</td>
            <td><form:checkbox path="clear" /></td>
        </tr>
        <tr>
            <td>Customer Identifier (Optional):</td>
            <td><form:input path="customerIdentifier" cssClass="text" /></td>
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
