var table = $(".res").stupidtable({
    "date": function (a) {
        return (parseInt(a) || 0);
    }
});

table.on("aftertablesort", function (event, data) {
    var th = $(this).find("th");
    th.find(".arrow").remove();
    var dir = $.fn.stupidtable.dir;

    // https://en.wikipedia.org/wiki/Geometric_Shapes
    var arrow = data.direction === dir.ASC ? "&#9650;" : "&#9660;";
    th.eq(data.column).append('<span class="arrow" style="float: right">' + arrow + '</span>');
});
