<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-future.js" %>
    });
</script>
<spring:hasBindErrors name="batchInsertForm">
    <div class="error">
        Error while trying to add OCPP Tag list:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<spring:hasBindErrors name="ocppTagForm">
    <div class="error">
        Error while trying to add an OCPP Tag:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
<section><span>
    Add OCPP Tag List
    <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
        <span>Insert multiple OCPP Tags at once by entering one ID Tag per line. This operation will leave other fields empty, which can be set later.</span>
    </a>
</span></section>
    <form:form action="/steve/manager/ocppTags/add/batch" modelAttribute="batchInsertForm">
        <table class="userInput">
            <tr>
                <td>ID Tags:</td><td><form:textarea path="idList"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" value="Add All">
                </td>
            </tr>
        </table>
    </form:form>
<section><span id="single">Add OCPP Tag</span></section>
    <form:form action="/steve/manager/ocppTags/add/single" modelAttribute="ocppTagForm">

        <c:set var="isUpdateForm" value="false" />

        <c:set var="submitButtonName" value="add" />
        <c:set var="submitButtonValue" value="Add" />
        <%@ include file="00-ocppTag.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>