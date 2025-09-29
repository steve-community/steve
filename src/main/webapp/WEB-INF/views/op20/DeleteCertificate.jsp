<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="DeleteCertificate" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Delete Certificate</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/DeleteCertificate" modelAttribute="params">
    <section><span>
        Delete Certificate
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Delete a certificate from the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Issuer Name Hash:</td>
            <td><form:input path="issuerNameHash" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Issuer Key Hash:</td>
            <td><form:input path="issuerKeyHash" cssClass="text" /></td>
        </tr>
        <tr>
            <td>Serial Number:</td>
            <td><form:input path="serialNumber" cssClass="text" /></td>
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
