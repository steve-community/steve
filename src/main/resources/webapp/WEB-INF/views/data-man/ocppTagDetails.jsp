<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
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