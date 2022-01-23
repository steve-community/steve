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
        $("#batch").click(function () {
            $("#batchInsertForm, #ocppTagForm").slideToggle(250);
        });

        <%-- According to the error type show/hide the corresponding view --%>
        if ($("#batchError").length) {
            $("#batchInsertForm").show();
            $("#ocppTagForm").hide();
        }
        if ($("#singleError").length) {
            $("#batchInsertForm").hide();
            $("#ocppTagForm").show();
        }
    });
</script>
<spring:hasBindErrors name="batchInsertForm">
    <div class="error" id="batchError">
        Error while trying to add OCPP Tag list:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<spring:hasBindErrors name="ocppTagForm">
    <div class="error" id="singleError">
        Error while trying to add an OCPP Tag:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content"><div>

    <section><span id="batch" style="cursor: pointer">
        Add OCPP Tag List
        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
            <span>Insert multiple OCPP Tags at once by entering one ID Tag per line. This operation will leave other fields empty, which can be set later.</span>
        </a>
    </span></section>
    <form:form action="${ctxPath}/manager/ocppTags/add/batch" modelAttribute="batchInsertForm" style="display: none">
        <table class="userInput">
            <tr>
                <td>ID Tags:</td><td><form:textarea path="idList"/></td></tr>
            <tr><td></td>
                <td id="add_space">
                    <input type="submit" value="Add All">
                </td>
            </tr>
        </table>
    </form:form>

    <section><span id="single">Add OCPP Tag</span></section>
    <form:form action="${ctxPath}/manager/ocppTags/add/single" modelAttribute="ocppTagForm">

        <c:set var="isUpdateForm" value="false" />

        <c:set var="submitButtonName" value="add" />
        <c:set var="submitButtonValue" value="Add" />
        <%@ include file="00-ocppTag.jsp" %>

    </form:form>
</div></div>
<%@ include file="../00-footer.jsp" %>