<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<thead><tr><th>Address</th><th></th></thead>
	<tr><td>Street:</td><td><form:input path="address.street"/></td></tr>
	<tr><td>House Number:</td><td><form:input path="address.houseNumber"/></td></tr>
	<tr><td>Zip code:</td><td><form:input path="address.zipCode"/></td></tr>
	<tr><td>City:</td><td><form:input path="address.city"/></td></tr>
	<tr><td>Country:</td><td><form:select path="address.country" items="${countryCodes}"/></td></tr>
	<tr><td>Latitude:</td><td><form:input path="address.latitude"/></td></tr>
	<tr><td>Longitude:</td><td><form:input path="address.longitude"/></td></tr>

    <!--
      Extract the address object using spring:bind to make it accessible for JSTL conditionals.

      Why: The form:input tag works with relative paths ("address.latitude"), but JSTL's c:if
      cannot resolve these relative paths - it needs direct object references. Since address
      can be nested in different parent objects (cp.address, user.address, etc.), we use
      spring:bind to resolve the path relative to the form's modelAttribute and expose the
      actual address object.

      See: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/tags/BindTag.html
    -->
    <spring:bind path="address">
        <c:set var="boundAddress" value="${status.value}" />
    </spring:bind>

    <c:if test="${(not empty boundAddress.latitude) and (not empty boundAddress.longitude)}">
        <tr>
           <td></td>
           <td><a target="_blank" href="https://maps.google.com/?q=${boundAddress.latitude},${boundAddress.longitude}">
              Show on Google Maps</a>
           </td>
        </tr>
    </c:if>
</table>
