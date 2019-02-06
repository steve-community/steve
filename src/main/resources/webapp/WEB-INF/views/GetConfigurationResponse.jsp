<%@ include file="00-header.jsp" %>
<div class="content">
	<div>
		<section><span>GetConfigurationResponse from ${chargeBoxId}</span></section>
		<c:if test="${not empty response.unknownKeys}">
			<div class="warning">
				<b>Unknown keys:</b> ${response.unknownKeys}
			</div>
			<br>
		</c:if>
		<table class="res">
			<thead>
			<tr>
				<th>Key</th>
				<th>Value</th>
				<th>Read only?</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${response.configurationKeys}" var="element">
				<tr>
					<td>${element.key}</td>
					<td>${element.value}</td>
					<td>${element.readonly}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<%@ include file="00-footer.jsp" %>
