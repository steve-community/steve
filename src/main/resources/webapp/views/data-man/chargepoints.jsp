<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../../static/js/snippets/getCPDetails.js" %>
        <%@ include file="../../static/js/snippets/getCPNote.js" %>
    });
</script>
<spring:hasBindErrors name="chargeBoxAddForm">
    <div class="error">
        Error while trying to add a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<spring:hasBindErrors name="chargeBoxUpdateForm">
    <div class="error">
        Error while trying to update a charge point:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content">
<section><span>Registered Charge Points</span></section>
<center>
	ChargeBox ID:
	<select id="cbi">
	<%-- Start --%>
	<c:forEach items="${cpList}" var="cp">
	<option value="${cp}">${cp}</option>
	</c:forEach>
	<%-- End --%>
	</select>
	<input type="submit" id="gdb" value="Get Details">
<br>
<div id="details-div"></div>
<br>
</center>
<section><span>Charge Point Management</span></section>
<div class="left-menu">
	<ul id="dm-menu">
		<li><a href="#" name="add" class="highlight">Add</a></li>
        <li><a href="#" name="update">Update</a></li>
		<li><a href="#" name="delete">Delete</a></li>
	</ul>
</div>
<div class="right-content">
	<div id="add">
		<div class="info"><b>Info:</b> ChargeBox ID is sufficient enough to register a charge point. After every reset of a charge point the remaining fields are updated.</div>
        <form:form action="/steve/manager/chargepoints/add" modelAttribute="chargeBoxAddForm">
			<table class="userInput">
				<tr><td>ChargeBox ID (string):</td><td><form:input path="chargeBoxId"/></td></tr>
                <tr><td>Note:</td><td><form:input path="note"/></td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
        </form:form>
	</div>
    <div id="update">
        <form:form action="/steve/manager/chargepoints/update" modelAttribute="chargeBoxUpdateForm">
            <table class="userInput">
                <tr>
                    <td>ChargeBox ID (string):</td>
                    <td>
                        <form:select path="chargeBoxId" id="update-chargeBoxId">
                            <option selected="selected" style="display:none;" disabled>Choose...</option>
                            <c:forEach items="${cpList}" var="cp">
                                <option value="${cp}">${cp}</option>
                            </c:forEach>
                        </form:select>
                    </td>
                </tr>
                <tr>
                    <td>Note:</td>
                    <td>
                        <form:input path="note" id="update-note" disabled="true"/>
                    </td>
                </tr>
                <tr><td></td><td id="add_space"><input type="submit" id="update-submit" disabled="true" value="Add"></td></tr>
            </table>
        </form:form>
    </div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</div>
        <form:form action="/steve/manager/chargepoints/delete">
            <table class="userInput">
                <tr><td>ChargeBox ID:</td><td>
                    <select name="chargeBoxId" required>
                        <option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
                        <%-- Start --%>
                        <c:forEach items="${cpList}" var="cp">
                            <option value="${cp}">${cp}</option>
                        </c:forEach>
                        <%-- End --%>
                    </select>
                </td></tr>
                <tr><td></td><td id="add_space"><input type="submit" value="Delete"></td></tr>
            </table>
        </form:form>
	</div>
</div></div>
<%@ include file="../00-footer.jsp" %>