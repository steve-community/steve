<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="userInput">
	<tr>
		<td style="vertical-align:top">Select one:</td>
		<td>
			<select name="chargePointSelectList" id="chargePointSelectList" size="5" required>
			<c:forEach items="${cpList}" var="cp">
			<option value="${cp.chargeBoxId};${cp.endpointAddress}">${cp.chargeBoxId} &#8212; ${cp.endpointAddress}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
</table>
<br>