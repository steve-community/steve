<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/datePicker-past.js" %>
    });
</script>
<spring:hasBindErrors name="userForm">
    <div class="error">
        Error while trying to add a user:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
<section><span>Add User</span></section>
    <form:form action="${ctxPath}/manager/users/add" modelAttribute="userForm">

        <%@ include file="00-user-profile.jsp" %>

        <%@ include file="00-address.jsp" %>

        <c:set var="submitButtonName" value="add" />
        <c:set var="submitButtonValue" value="Add" />
        <%@ include file="00-user-ocpp.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>