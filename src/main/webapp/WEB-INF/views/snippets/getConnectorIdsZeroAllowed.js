$("#chargePointSelectList").change(function() {
    var cp = $(this).find("option:selected").text();
    $.getJSON("${ctxPath}/manager/ajax/" + cp + "/connectorIds", function(data) {
        var options = "<option value='0'>Not for a specific connector</option>";
        $.each(data, function() {
            options += "<option value='" + this + "'>" + this + "</option>";
        });
        var select = $("#connectorId");
        select.prop("disabled", false);
        select.html(options);
    });
});
