<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function () {
        <%@ include file="../snippets/dateTimePicker-future.js" %>
        <%@ include file="../snippets/schedulePeriodsTable.js" %>
    });
</script>
<spring:hasBindErrors name="form">
    <div class="error">
        Error while trying to add a charging profile:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content">
    <div>
        <section><span>Add Charging Profile</span></section>
        <form:form action="${ctxPath}/manager/chargingProfiles/add" modelAttribute="form">

            <c:set var="submitButtonName" value="add"/>
            <c:set var="submitButtonValue" value="Add"/>
            <%@ include file="00-charging-profile.jsp" %>

        </form:form>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>