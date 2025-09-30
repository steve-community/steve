<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="ClearVariableMonitoring" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Clear Variable Monitoring</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/ClearVariableMonitoring" modelAttribute="params">
    <section><span>
        Clear Variable Monitoring
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Clear variable monitoring configurations on the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../00-cp-multiple.jsp" %></td>
        </tr>
        <tr>
            <td>Monitoring IDs (comma-separated):</td>
            <td><form:input path="monitoringIds" cssClass="text" placeholder="1,2,3" /></td>
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
