<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="InstallCertificate" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Install Certificate</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/InstallCertificate" modelAttribute="params">
    <section><span>
        Install Certificate
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Install a certificate on the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Certificate Type:</td>
            <td>
                <form:select path="certificateType">
                    <form:option value="">-- Select --</form:option>
                    <form:option value="V2GRootCertificate">V2G Root Certificate</form:option>
                    <form:option value="MORootCertificate">MO Root Certificate</form:option>
                    <form:option value="CSMSRootCertificate">CSMS Root Certificate</form:option>
                    <form:option value="ManufacturerRootCertificate">Manufacturer Root Certificate</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>Certificate (PEM):</td>
            <td><form:textarea path="certificate" cssClass="text" rows="10" /></td>
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
