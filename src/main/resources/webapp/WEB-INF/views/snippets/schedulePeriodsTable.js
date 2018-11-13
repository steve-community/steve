// https://stackoverflow.com/a/14533243
$('#periodsTable').on('click', '.removeRow', function() {
    $(this).closest("tr").remove();
});

$('#addRow').click(function () {
    // use prefix for new rows to be at the end when ordered
    var id = "zz" + Date.now();
    var startPeriodInSeconds = "<td><input id=\"schedulePeriodMap" + id + ".startPeriodInSeconds\" name=\"schedulePeriodMap[" + id + "].startPeriodInSeconds\" type=\"text\" value=\"\"/></td>";
    var powerLimitInAmperes = "<td><input id=\"schedulePeriodMap" + id + ".powerLimitInAmperes\" name=\"schedulePeriodMap[" + id + "].powerLimitInAmperes\" type=\"text\" value=\"\"/></td>";
    var numberPhases = "<td><input id=\"schedulePeriodMap" + id + ".numberPhases\" name=\"schedulePeriodMap[" + id + "].numberPhases\" type=\"text\" value=\"\" placeholder=\"if empty, 3 will be assumed\"/></td>";
    var deleteButton = "<td><input type=\"button\" class=\"removeRow\" value=\"Delete\"></td>";
    var row = "<tr id=" + id + ">" + startPeriodInSeconds + powerLimitInAmperes + numberPhases + deleteButton + "</tr>";

    $('#periodsTable tbody').append(row);
});