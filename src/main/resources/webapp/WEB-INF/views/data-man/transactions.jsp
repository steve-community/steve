<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/dateTimePicker-past.js" %>
        <%@ include file="../snippets/periodTypeSelect.js" %>
    });
</script>
<div class="content">
<section><span>
Transactions
<a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
<span>If stop date/time and stop value are empty, this means that a transaction is still active
(i.e. it has started but not stopped yet or the charging station did not inform SteVe about the stopped transaction yet).</span>
</a>
</span></section>
    <form:form action="/steve/manager/transactions/query" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>Transaction ID:</td>
                <td><form:input path="transactionPk"/></td>
            </tr>
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
                <td>Transaction Type:</td>
                <td><form:select path="type">
                        <form:options items="${type}" itemLabel="value"/>
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
                <td>Get as CSV?:</td>
                <td><form:checkbox path="returnCSV"/></td>
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
                <th>Transaction ID</th>
                <th>ChargeBox ID</th>
                <th>Connector ID</th>
                <th>OCPP ID Tag</th>
                <th>Start Date/Time</th>
                <th>Start Value</th>
                <th>Stop Date/Time</th>
                <th>Stop Value</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${transList}" var="ta">
            <tr>
                <td>${ta.id}</td>
                <td><a href="/steve/manager/chargepoints/details/${ta.chargeBoxPk}">${ta.chargeBoxId}</a></td>
                <td>${ta.connectorId}</td>
                <td><a href="/steve/manager/ocppTags/details/${ta.ocppTagPk}">${ta.ocppIdTag}</a></td>
                <td>${ta.startTimestamp}</td>
                <td>${ta.startValue}</td>
                <td>${ta.stopTimestamp}</td>
                <td>${ta.stopValue}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
<br>
</div>
<%@ include file="../00-footer.jsp" %>