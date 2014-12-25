<%@ include file="00-header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<spring:hasBindErrors name="settingsForm">
    <div class="error">
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="content">
<section><span>Settings</span></section>
<form:form action="/steve/manager/settings" modelAttribute="settingsForm">
<table class="userInputFullPage">
<tr><td>Heartbeat Interval:</td><td>
    <form:input path="heartbeat"/>
</td></tr>
<tr><td><i>The time interval in <b>minutes</b> for how often a charge point <br> should request the current time from SteVe.</i></td><td>(Current value: ${currentHeartbeat})</td></tr>
<tr><td>Expiration:</td><td>
    <form:input path="expiration"/>
</td></tr>
<tr><td><i>The amount of time in <b>hours</b> for how long a charge point <br> should store the authorization info of an idTag in its local white list.</i></td><td>(Current value: ${currentExpiration})</td></tr>
<tr><td></td><td id="add_space"><input type="submit" value="Change"></td></tr>
</table>
</form:form>
</div>
<%@ include file="00-footer.jsp" %>