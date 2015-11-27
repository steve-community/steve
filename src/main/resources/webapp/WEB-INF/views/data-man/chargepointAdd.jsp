<%@ include file="../00-header.jsp" %>
<spring:hasBindErrors name="chargePointForm">
    <div class="error">
        Error while trying to add a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>
<section><span>Add Charge Point</span></section>
    <form:form action="/steve/manager/chargepoints/add" modelAttribute="chargePointForm">

        <table class="userInput">
            <thead><tr><th>OCPP</th><th></th></thead>
            <tbody>
                <tr>
                    <td>ChargeBox ID:</td>
                    <td>
                        <form:input path="chargeBoxId"/>
                        <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
                            <span>This field is set when adding a charge point, and cannot be changed later</span>
                        </a>
                    </td>
                </tr>
            </tbody>
        </table>

        <table class="userInput">
            <thead><tr><th>Address</th><th></th></thead>
            <tr><td>Street and House Number:</td><td><form:input path="address.streetAndHouseNumber"/></td></tr>
            <tr><td>Zip code:</td><td><form:input path="address.zipCode"/></td></tr>
            <tr><td>City:</td><td><form:input path="address.city"/></td></tr>
            <tr><td>Country:</td><td><form:input path="address.country"/></td></tr>
        </table>

        <table class="userInput">
            <thead><tr><th>Misc.</th><th></th></thead>
            <tr><td>Description:</td><td><form:input path="description"/></td></tr>
            <tr><td>Latitude:</td><td><form:input path="locationLatitude"/></td></tr>
            <tr><td>Longitude:</td><td><form:input path="locationLongitude"/></td></tr>
            <tr><td>Additional Note:</td><td><form:input path="note"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" name="add" value="Add">
                    <input type="submit" name="backToOverview" value="Back to Overview">
                </td></tr>
        </table>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>