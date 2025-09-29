<%@ include file="../00-header.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>

<div class="content">
<div class="left-menu">
<%@ include file="00-menu.jsp" %>
<jsp:param name="menuItem" value="GetLocalListVersion" />
</div>

<div class="main-content">
<section><span>
Paths:
<a href="${ctxPath}/manager/home">Home</a> &gt;
<a href="${ctxPath}/manager/operations">Operations</a> &gt;
<strong>OCPP v2.0 Get Local List Version</strong>
</span></section>

<form:form action="${ctxPath}/manager/operations/v2.0/GetLocalListVersion" modelAttribute="params">
    <section><span>
        Get Local List Version
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>This retrieves the version of the local authorization list from the selected charge point(s)</span>
        </a>
    </span></section>

    <table class="userInput">
        <tr>
            <td>Charge Point Selection:</td>
            <td><%@ include file="../snippets/chargePointSelectList.jsp" %></td>
        </tr>
        <tr>
            <td></td>
            <td id="add_space">
                <input type="submit" value="Get Version"/>
            </td>
        </tr>
    </table>
</form:form>

<%@ include file="../00-footer.jsp" %>