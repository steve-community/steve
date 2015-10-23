<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="userInput">
	<tr>
		<td style="vertical-align:top">
            <input type="button" value="Select All" onClick="selectAll(document.getElementById('chargePointSelectList'))"><input type="button" value="Select None" onClick="selectNone(document.getElementById('chargePointSelectList'))">
        </td>
		<td>
			<form:select path="chargePointSelectList" size="5" multiple="true">
				<c:forEach items="${cpList}" var="cp">
					<form:option value="${cp.ocppTransport};${cp.chargeBoxId};${cp.endpointAddress}" label="${cp.chargeBoxId}"/>
				</c:forEach>
			</form:select>
		</td>
	</tr>
</table>
<br>