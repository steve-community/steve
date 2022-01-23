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
        <%@ include file="../snippets/sortable.js" %>
        <%@ include file="../snippets/dateTimePicker.js" %>
    });
</script>
<div class="content">
    <section><span>Charging Profile Overview</span></section>
    <div id="overview">
        <form:form action="${ctxPath}/manager/chargingProfiles/query" method="get" modelAttribute="params">
            <table class="userInput">
                <tr>
                    <td>Charging Profile ID (integer):</td>
                    <td><form:input path="chargingProfilePk"/></td>
                </tr>
                <tr>
                    <td>Stack Level (integer):</td>
                    <td><form:input path="stackLevel"/></td>
                </tr>
                <tr>
                    <td>Description:</td>
                    <td><form:input path="description"/></td>
                </tr>
                <tr>
                    <td>Profile Purpose:</td>
                    <td><form:select path="profilePurpose">
                        <option value="" selected>-- Empty --</option>
                        <form:options items="${profilePurpose}"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Profile Kind:</td>
                    <td><form:select path="profileKind">
                        <option value="" selected>-- Empty --</option>
                        <form:options items="${profileKind}"/>
                    </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Recurrency Kind:</td>
                    <td><form:select path="recurrencyKind">
                        <option value="" selected>-- Empty --</option>
                        <form:options items="${recurrencyKind}"/>
                    </form:select>
                    </td>
                </tr>
                <tr><td>Valid From:</td><td><form:input path="validFrom" class="dateTimePicker"/></td></tr>
                <tr><td>Valid To:</td><td><form:input path="validTo" class="dateTimePicker"/></td></tr>
                <tr>
                    <td></td>
                    <td id="add_space">
                        <input type="submit" value="Get">
                    </td>
                </tr>
            </table>
        </form:form>
        <br>
        <table class="res action">
            <thead>
            <tr>
                <th data-sort="string">Charging Profile ID</th>
                <th data-sort="string">Stack Level</th>
                <th data-sort="string">Description</th>
                <th data-sort="string">Profile Purpose</th>
                <th data-sort="string">Profile Kind</th>
                <th data-sort="string">Recurrency Kind</th>
                <th data-sort="date">Valid From</th>
                <th data-sort="date">Valid To</th>
                <th>
                    <form:form action="${ctxPath}/manager/chargingProfiles/add" method="get">
                        <input type="submit" class="blueSubmit" value="Add New">
                    </form:form>
                </th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${profileList}" var="cp">
                <tr>
                    <td><a href="${ctxPath}/manager/chargingProfiles/details/${cp.chargingProfilePk}">${cp.chargingProfilePk}</a></td>
                    <td>${cp.stackLevel}</td>
                    <td>${cp.description}</td>
                    <td>${cp.profilePurpose}</td>
                    <td>${cp.profileKind}</td>
                    <td>${cp.recurrencyKind}</td>
                    <td data-sort-value="${cp.validFrom.millis}">${cp.validFrom}</td>
                    <td data-sort-value="${cp.validTo.millis}">${cp.validTo}</td>
                    <td>
                        <form:form action="${ctxPath}/manager/chargingProfiles/delete/${cp.chargingProfilePk}">
                            <input type="submit" class="redSubmit" value="Delete">
                        </form:form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</div>
<%@ include file="../00-footer.jsp" %>