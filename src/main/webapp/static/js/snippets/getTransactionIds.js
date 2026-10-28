$(document).ready(function() {
    // context path (e.g. "/" or "/steve/") rendered by the layout template
    var ctxPath = $('meta[name="ctx-path"]').attr("content");

    $("#chargePointSelectList").change(function() {
        var cp = $(this).find("option:selected").text();
        $.getJSON(ctxPath + "manager/ajax/" + encodeURIComponent(cp) + "/transactionIds", function(data) {
            var select = $("#transactionId");
            select.empty();
            // build options via DOM, so that values are never interpreted as markup
            $.each(data, function(index, value) {
                select.append($("<option>").val(value).text(value));
            });
            select.prop("disabled", false);
        });
    });
});
