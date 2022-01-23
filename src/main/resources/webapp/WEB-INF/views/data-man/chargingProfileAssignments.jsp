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
        <%@ include file="../snippets/sortable.js" %>
    });
</script>
<div class="content"><div>
    <section><span>Charging Profile Assignments</span></section>

    <form:form action="${ctxPath}/manager/chargingProfiles/assignments" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>ChargeBox ID:</td>
                <td><form:select path="chargeBoxId">
                    <option value="" selected>All</option>
                    <form:options items="${cpList}"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Charging Profile ID:</td>
                <td><form:select path="chargingProfilePk">
                    <option value="" selected>All</option>
                    <form:options items="${profileList}" itemLabel="itemDescription" itemValue="chargingProfilePk"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Charging Profile Description:</td>
                <td><form:input path="chargingProfileDescription"/></td>
            </tr>
            <tr>
                <td></td>
                <td id="add_space">
                    <input type="submit" value="Get">
                </td>
            </tr>
        </table>
    </form:form>
    <br>

    <table class="res" id="chargingProfileAssignmentTable">
        <thead>
        <tr>
            <th data-sort="string">ChargeBox ID</th>
            <th data-sort="int">Connector ID</th>
            <th data-sort="int">Charging Profile ID</th>
            <th data-sort="string">Charging Profile Description</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${assignments}" var="a">
            <tr>
                <td><a href="${ctxPath}/manager/chargepoints/details/${a.chargeBoxPk}">${a.chargeBoxId}</a></td>
                <td>${a.connectorId}</td>
                <td><a href="${ctxPath}/manager/chargingProfiles/details/${a.chargingProfilePk}">${a.chargingProfilePk}</a></td>
                <td>${a.chargingProfileDescription}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>
