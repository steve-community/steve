<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
<%@ include file="../../static/js/snippets/getCPDetails.js" %>
</script>
<spring:hasBindErrors name="chargeBoxAddForm">
    <div class="error">
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
		<li><a href="#" name="delete">Delete</a></li>
	</ul>
</div>
<div class="right-content">
	<div id="add">
		<div class="info"><b>Info:</b> ChargeBox ID is sufficient enough to register a charge point. After every reset of a charge point the remaining fields are updated.</div>
        <form:form action="/steve/manager/chargepoints/add" commandName="chargeBoxAddForm">
			<table class="userInput">
				<tr><td>ChargeBox ID (string):</td><td><form:input path="chargeBoxId"/></td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
        </form:form>
	</div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</div>
        <form:form action="/steve/manager/chargepoints/delete" commandName="chargeBoxDeleteForm">
			<table class="userInput">
				<tr><td>ChargeBox ID:</td><td>
                    <form:select path="chargeBoxId">
                        <option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
                        <%--<form:option value="" cssStyle="display:none;" label="Choose..."/>--%>
                        <form:options items="${cpList}" />
                    </form:select>
				</td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Delete"></td></tr>
			</table>
        </form:form>
	</div>
</div></div>
<%@ include file="../00-footer.jsp" %>