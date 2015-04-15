$("#chargePointSelectList").change(function() {
	var cp = $(this).find("option:selected").text();
	$.getJSON("/steve/manager/ajax/getConnectorIds?chargeBoxId=" + cp, function(data) {
		var options = "";
		$.each(data, function() {
			options += "<option value='" + this + "'>" + this + "</option>";
		});
		var select = $("#connectorId");
		select.prop("disabled", false);
		select.html(options);
	});
});
