<%@ include file="00-header.jsp" %>
<section><span>Settings</span></section>
<form method="POST" action="/steve/manager/settings">
<table class="userInputFullPage">
<tr><td>Heartbeat Interval:</td><td><input type="number" name="heartbeat" placeholder="optional"></td></tr>
<tr><td><i>The time interval in <b>seconds</b> for how often a charge point <br> should request the current time from SteVe.</i></td><td>(Current value: ${heartbeat})</td></tr>
<tr><td>Expiration:</td><td><input type="number" name="expiration" placeholder="optional"></td></tr>
<tr><td><i>The amount of time in <b>hours</b> for how long a charge point <br> should store the authorization info of an idTag in its local white list.</i></td><td>(Current value: ${expire})</td></tr>
<tr><td></td><td id="add_space"><input type="submit" value="Change"></td></tr>
</table>
</form>
<%@ include file="00-footer.jsp" %>