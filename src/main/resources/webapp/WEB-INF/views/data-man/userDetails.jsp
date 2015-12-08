<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/datePicker-past.js" %>
    });
</script>
<spring:hasBindErrors name="userForm">
    <div class="error">
        Error while trying to update a user:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
    <section><span>User Details</span></section>
    <form:form action="/steve/manager/users/update" modelAttribute="userForm">

        <form:hidden path="userPk" readonly="true"/>
        <%@ include file="00-user-profile.jsp" %>

        <form:hidden path="address.addressPk" readonly="true"/>
        <%@ include file="00-address.jsp" %>

        <c:set var="submitButtonName" value="update" />
        <c:set var="submitButtonValue" value="Update" />
        <%@ include file="00-user-ocpp.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>