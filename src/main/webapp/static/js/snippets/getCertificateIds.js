$(document).ready(function() {
    // context path (e.g. "/" or "/steve/") rendered by the layout template
    var ctxPath = $('meta[name="ctx-path"]').attr("content");

    $("#chargePointSelectList").change(function() {
        var cp = $(this).find("option:selected").text();
        $.getJSON(ctxPath + "manager/ajax/" + cp + "/certificateIds", function(data) {
            var options = "";
            $.each(data, function() {
                options += "<option value='" + this + "'>" + this + "</option>";
            });
            var select = $("#installedCertificateId");
            select.prop("disabled", false);
            select.html(options);
        });
    });
});
