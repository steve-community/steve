<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-future.js" %>
    });
</script>
<spring:hasBindErrors name="ocppTagForm">
    <div class="error">
        Error while trying to update an OCPP tag:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
    <section><span>OCPP Tag Details</span></section>
    <form:form action="${ctxPath}/manager/ocppTags/update" modelAttribute="ocppTagForm">

        <form:hidden path="ocppTagPk" readonly="true"/>

        <c:set var="isUpdateForm" value="true" />

        <c:set var="submitButtonName" value="update" />
        <c:set var="submitButtonValue" value="Update" />
        <%@ include file="00-ocppTag.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>