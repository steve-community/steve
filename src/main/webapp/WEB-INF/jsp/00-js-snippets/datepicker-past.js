var startDate = $("#startDate").datepicker({
    firstDay: 1,
    dateFormat: 'yy-mm-dd',
    maxDate: 0,
    onSelect: function(selectedDate) {
        stopDate.datepicker("option", "minDate", selectedDate);
    }
});

var stopDate = $("#stopDate").datepicker({
    firstDay: 1,
    dateFormat: 'yy-mm-dd',
    maxDate: 0,
    onSelect: function(selectedDate) {
        startDate.datepicker("option", "maxDate", selectedDate);
    }
});