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

        <%@ include file="00-address.jsp" %>

        <c:set var="submitButtonName" value="add" />
        <c:set var="submitButtonValue" value="Add" />
        <%@ include file="00-cp-misc.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>