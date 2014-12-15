<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<section><span>Charge Points with OCPP v1.2</span></section>
<table class="userInput">
	<tr>
		<td style="vertical-align:top">Select one:</td>
		<td>
			<select name="cp_items" id="cp_items" size="5" multiple required>
			<c:forEach items="${cpList}" var="cp">
			<option value="${cp.key};${cp.value}">${cp.key} &#8212; ${cp.value}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
</table>
<br>