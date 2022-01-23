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
    $(document).ready(function () {
        <%@ include file="../snippets/dateTimePicker.js" %>
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