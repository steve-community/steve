<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        $("#batch").click(function () {
            $("#batchChargePointForm, #chargePointForm").slideToggle(250);
        });

        <%-- According to the error type show/hide the corresponding view --%>
        if ($("#batchError").length) {
            $("#batchChargePointForm").show();
            $("#chargePointForm").hide();
        }
        if ($("#singleError").length) {
            $("#batchChargePointForm").hide();
            $("#chargePointForm").show();
        }
    });
</script>
<spring:hasBindErrors name="batchChargePointForm">
    <div class="error" id="batchError">
        Error while trying to add charge point list:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<spring:hasBindErrors name="chargePointForm">
    <div class="error" id="singleError">
        Error while trying to add a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>

    <section><span id="batch" style="cursor: pointer">
        Add Charge Point List
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Insert multiple charge points at once by entering one ID per line. This operation will leave other fields empty, which can be set later.</span>
        </a>
    </span></section>
    <form:form action="${ctxPath}/manager/chargepoints/add/batch" modelAttribute="batchChargePointForm" style="display: none">
        <table class="userInput">
            <tr>
                <td>ChargeBox IDs:</td><td><form:textarea path="idList"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" value="Add All">
                </td>
            </tr>
        </table>
    </form:form>

    <section><span>Add Charge Point</span></section>
    <form:form action="${ctxPath}/manager/chargepoints/add/single" modelAttribute="chargePointForm">

        <table class="userInput">
            <thead><tr><th>OCPP</th><th></th></thead>
            <tbody>
                <tr>
                    <td>ChargeBox ID:</td>
                    <td>
                        <form:input path="chargeBoxId"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>This field is set when adding a charge point, and cannot be changed later</span>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>Insert connector status after start/stop transaction:
                    </td>
                    <td>
                        <form:checkbox path="insertConnectorStatusAfterTransactionMsg"/>
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
                            <span>After a transaction start/stop message, a charging station might send a connector status notification, but it is not required. If this is enabled, SteVe will update the connector status no matter what.</span>
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