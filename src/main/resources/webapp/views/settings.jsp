<%@ include file="00-header.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<section><span>Settings</span></section>
<form:form action="/steve/manager/settings" commandName="settingsForm">
<table class="userInputFullPage">
<tr><td>Heartbeat Interval:</td><td>
    <form:input path="heartbeat"/>
    <form:errors path="heartbeat" cssClass="error"/>
</td></tr>
<tr><td><i>The time interval in <b>seconds</b> for how often a charge point <br> should request the current time from SteVe.</i></td><td>(Current value: tt)</td></tr>
<tr><td>Expiration:</td><td>
    <form:input path="expiration"/>
    <form:errors path="expiration" cssClass="error"/>
</td></tr>
<tr><td><i>The amount of time in <b>hours</b> for how long a charge point <br> should store the authorization info of an idTag in its local white list.</i></td><td>(Current value: tt)</td></tr>
<tr><td></td><td id="add_space"><input type="submit" value="Change"></td></tr>
</table>
</form:form>
<%@ include file="00-footer.jsp" %>