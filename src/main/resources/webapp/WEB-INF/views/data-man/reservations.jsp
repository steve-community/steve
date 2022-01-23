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
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
	$(document).ready(function() {
		<%@ include file="../snippets/dateTimePicker.js" %>
		<%@ include file="../snippets/periodTypeSelect.js" %>
		<%@ include file="../snippets/sortable.js" %>
	});
</script>
<div class="content">
	<section><span>
		Reservations
			<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
				<span>
				Status definitions:
				WAITING (Waiting for charge point to respond to a reservation request),
				ACCEPTED (Charge point accepted - The only status for active, usable reservations),
				USED (Reservation used by the user for a transaction),
				CANCELLED (Reservation cancelled by the user)
				</span>
			</a>
	</span></section>
	<form:form action="${ctxPath}/manager/reservations/query" method="get" modelAttribute="params">
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
				<td>OCPP ID Tag:</td>
				<td><form:select path="ocppIdTag">
					    <option value="" selected>All</option>
                        <form:options items="${idTagList}"/>
                    </form:select>
				</td>
			</tr>
            <tr>
                <td>Reservation Status:</td>
                <td><form:select path="status">
                        <option value="" selected>All</option>
                        <form:options items="${statusList}"/>
                    </form:select>
                </td>
            </tr>
			<tr>
				<td>Period Type:</td>
				<td><form:select path="periodType" id="periodTypeSelect">
						<form:options items="${periodType}" itemLabel="value"/>
					</form:select>
				</td>
			</tr>
			<tr>
				<td>From:</td>
				<td><form:input path="from" id="intervalPeriodTypeFrom" cssClass="dateTimePicker"/></td>
			</tr>
			<tr>
				<td>To:</td>
				<td><form:input path="to" id="intervalPeriodTypeTo" cssClass="dateTimePicker"/></td>
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

<table class="res">
	<thead>
		<tr>
			<th data-sort="int">Reservation ID</th>
			<th data-sort="int">Transaction ID</th>
			<th data-sort="string">OCPP ID Tag</th>
			<th data-sort="string">ChargeBox ID</th>
			<th data-sort="int">Connector ID</th>
			<th data-sort="date">Start Date/Time</th>
			<th data-sort="date">Expiry Date/Time</th>
			<th data-sort="string">Status</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${reservList}" var="res">
		<tr>
			<td>${res.id}</td>
			<td>
				<c:if test="${not empty res.transactionId}">
					<a href="${ctxPath}/manager/transactions/query?type=ALL&transactionPk=${res.transactionId}">${res.transactionId}</a>
				</c:if>
			</td>
			<td><a href="${ctxPath}/manager/ocppTags/details/${res.ocppTagPk}">${res.ocppIdTag}</a></td>
			<td><a href="${ctxPath}/manager/chargepoints/details/${res.chargeBoxPk}">${res.chargeBoxId}</a></td>
			<td>${res.connectorId}</td>
			<td data-sort-value="${res.startDatetimeDT.millis}">${res.startDatetime}</td>
			<td data-sort-value="${res.expiryDatetimeDT.millis}">${res.expiryDatetime}</td>
			<td>${res.status}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>