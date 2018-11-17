<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function () {
        <%@ include file="../snippets/dateTimePicker.js" %>
        <%@ include file="../snippets/schedulePeriodsTable.js" %>
    });
</script>
<spring:hasBindErrors name="form">
    <div class="error">
        Error while trying to update a charging profile:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content">
    <div>
        <section><span>Charging Profile Details</span></section>

        <table class="userInput">
            <thead><tr><th>Related Data Pages</th><th></th></thead>
            <tbody>
            <tr>
                <td>Stations using this profile:</td>
                <td>
                    <a href="${ctxPath}/manager/chargingProfiles/assignments?chargingProfilePk=${form.chargingProfilePk}">ALL</a>
                </td>
            </tr>
            </tbody>
        </table>

        <form:form action="${ctxPath}/manager/chargingProfiles/update" modelAttribute="form">

            <form:hidden path="chargingProfilePk" readonly="true"/>

            <c:set var="submitButtonName" value="update"/>
            <c:set var="submitButtonValue" value="Update"/>
            <%@ include file="00-charging-profile.jsp" %>

        </form:form>
    </div>
</div>
<%@ include file="../00-footer.jsp" %>