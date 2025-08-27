// https://stackoverflow.com/a/14533243
$('#periodsTable').on('click', '.removeRow', function() {
    $(this).closest("tr").remove();
});

$('#addRow').click(function () {
  // Find the last table row
  const last = $('#periodsTable tbody tr').last();
  // If there is no row (because table is empty), start with id 0. Otherwise, increment last row's id.
  const id = last.length === 0 ? 0 : parseInt(last[0].id, 10) + 1;
  const startPeriodInSeconds = `<td><input id="schedulePeriods${id}.startPeriodInSeconds" name="schedulePeriods[${id}].startPeriodInSeconds" type="text" value=""/></td>`;
  const powerLimit = `<td><input id="schedulePeriods${id}.powerLimit" name="schedulePeriods[${id}].powerLimit" type="text" value=""/></td>`;
  const numberPhases = `<td><input id="schedulePeriods${id}.numberPhases" name="schedulePeriods[${id}].numberPhases" type="text" value="" placeholder="if empty, 3 will be assumed"/></td>`;
  const deleteButton = `<td><input type="button" class="removeRow" value="Delete"></td>`;
  const row = `<tr id="${id}">${startPeriodInSeconds}${powerLimit}${numberPhases}${deleteButton}</tr>`;

  $('#periodsTable tbody').append(row);
});
