<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
	$(document).ready(function() {
		<%@ include file="../snippets/dateTimePicker.js" %>
		<%@ include file="../snippets/periodTypeSelect.js" %>
	});
</script>
<div class="content">
	<section><span>
		Reservations
			<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
				<span>
				Status definitions:
				WAITING (Waiting for charge point to respond to a reservation request),
				ACCEPTED (Charge point accepted - The only status for active, usable reservations),
				USED (Reservation used by the user for a transaction),
				CANCELLED (Reservation cancelled by the user)
				</span>
			</a>
	</span></section>
	<form:form action="/steve/manager/reservations/query" method="get" modelAttribute="params">
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
			<th>Reservation ID</th>
			<th>Transaction ID</th>
			<th>OCPP ID Tag</th>
			<th>ChargeBox ID</th>
			<th>Start Date/Time</th>
			<th>Expiry Date/Time</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${reservList}" var="res">
		<tr>
			<td>${res.id}</td>
			<td>
				<c:if test="${not empty res.transactionId}">
					<a href="/steve/manager/transactions/query?type=ALL&transactionPk=${res.transactionId}">${res.transactionId}</a>
				</c:if>
			</td>
			<td><a href="/steve/manager/ocppTags/details/${res.ocppTagPk}">${res.ocppIdTag}</a></td>
			<td><a href="/steve/manager/chargepoints/details/${res.chargeBoxPk}">${res.chargeBoxId}</a></td>
			<td>${res.startDatetime}</td>
			<td>${res.expiryDatetime}</td>
			<td>${res.status}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>