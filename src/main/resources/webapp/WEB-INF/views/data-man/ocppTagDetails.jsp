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

    <table class="userInput">
        <thead><tr><th>See Operations</th><th></th></thead>
        <tbody>
        <tr>
            <td>Transactions:</td>
            <td>
                <a href="${ctxPath}/manager/transactions/query?ocppIdTag=${ocppTagForm.idTag}&amp;type=ACTIVE">ACTIVE</a>
                /
                <a href="${ctxPath}/manager/transactions/query?ocppIdTag=${ocppTagForm.idTag}&amp;type=ALL">ALL</a>
            </td>
        </tr>
        <tr>
            <td>Reservations:</td>
            <td>
                <a href="${ctxPath}/manager/reservations/query?ocppIdTag=${ocppTagForm.idTag}&amp;periodType=ACTIVE">ACTIVE</a>
            </td>
        </tr>
        </tbody>
    </table>

    <form:form action="${ctxPath}/manager/ocppTags/update" modelAttribute="ocppTagForm">

        <form:hidden path="ocppTagPk" readonly="true"/>

        <c:set var="isUpdateForm" value="true" />

        <c:set var="submitButtonName" value="update" />
        <c:set var="submitButtonValue" value="Update" />
        <%@ include file="00-ocppTag.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>