$("#cp_items").change(function() {
	var cp = $(this).val().toString().split(';')[0];
	$.getJSON("/steve/manager/ajax/getConnectorIds?chargeBoxId=" + cp, function(data) {
		var options = "<option value='0'>Not for a specific connector</option>";
		$.each(data, function() {
			options += "<option value='" + this + "'>" + this + "</option>";
		});
		var select = $("#connectorId");
		select.prop("disabled", false);
		select.html(options);
	});
});
