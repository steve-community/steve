<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="GetInstalledCertificateIds" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Get Installed Certificate IDs</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/GetInstalledCertificateIds" modelAttribute="params">
    <section><span>
        Get Installed Certificate IDs
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Retrieve installed certificate IDs from the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td>Certificate Type (Optional):</td>
            <td>
                <form:select path="certificateType">
                    <form:option value="">-- All Types --</form:option>
                    <form:option value="V2GRootCertificate">V2G Root Certificate</form:option>
                    <form:option value="MORootCertificate">MO Root Certificate</form:option>
                    <form:option value="CSMSRootCertificate">CSMS Root Certificate</form:option>
                    <form:option value="ManufacturerRootCertificate">Manufacturer Root Certificate</form:option>
                    <form:option value="V2GCertificateChain">V2G Certificate Chain</form:option>
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
