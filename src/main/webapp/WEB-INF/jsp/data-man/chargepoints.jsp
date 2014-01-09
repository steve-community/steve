<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {                       
    $('#gdb').click(function() {
		var ddiv = $('#details-div');
		ddiv.html("<br>Loading...");		
    	$.getJSON("${servletPath}/getDetails?chargeBoxId=" + $("#cbi").val(), function(data) {
    		ddiv.html("<table id='details' class='cpd'><thead><tr><th>Charge Point Details</th><th></th></tr></thead></table>");
    		var table = $('#details');
            $.each(data, function(key, val) {
	            $('<tr>').appendTo(table)
		            .append($('<td>').text(key))
		            .append($('<td>').text(val));
            });
    	});
    });
});
</script>
<section><span>Registered Charge Points</span></section>
<center>
	chargeBoxId:
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
		<div class="info"><b>Info:</b> chargeBoxId is sufficient enough to register a charge point. After every reset of a charge point the remaining fields are updated.</div>
		<form method="POST" action="${servletPath}/add">
			<table>
				<tr><td>chargeBoxId (string):</td><td><input type="text" name="chargeBoxId"></td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
		</form>
	</div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a charge point causes losing all related information including transactions, reservations, connector status and connector meter values.</div>
		<form method="POST" action="${servletPath}/delete">
			<table>
				<tr><td>chargeBoxId:</td><td>
					<select name="chargeBoxId">
					<%-- Start --%>
					<c:forEach items="${cpList}" var="cp">
					<option value="${cp}">${cp}</option>
					</c:forEach>
					<%-- End --%>	
					</select>
				</td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Delete"></td></tr>
			</table>
		</form>
	</div>
</div>
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>