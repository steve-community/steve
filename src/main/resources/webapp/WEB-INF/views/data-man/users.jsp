<%@ include file="../00-header.jsp" %>
<div class="content"><div>
    <section><span>User Overview</span></section>
    <form:form action="/steve/manager/users/query" method="get" modelAttribute="params">
        <table class="userInput">
            <tr>
                <td>User ID:</td>
                <td><form:input path="userPk"/></td>
            </tr>
            <tr>
                <td>Ocpp ID Tag:</td>
                <td><form:input path="ocppIdTag"/></td>
            </tr>
            <tr>
                <td>Name:</td>
                <td><form:input path="name"/></td>
            </tr>
            <tr>
                <td>E-Mail:</td>
                <td><form:input path="email"/></td>
            </tr>
            <tr>
                <td></td>
                <td id="add_space">
                    <input type="submit" value="Get">
                </td>
            </tr>
        </table>
    </form:form>
    <br>
    <table class="res action">
        <thead>
            <tr>
                <th>User ID</th>
                <th>Ocpp ID Tag</th>
                <th>Name</th>
                <th>Phone</th>
                <th>E-Mail</th>
                <th>
                    <form:form action="/steve/manager/users/add" method="get">
                        <input type="submit" value="Add New">
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${userList}" var="cr">
            <tr><td><a href="/steve/manager/users/details/${cr.userPk}">${cr.userPk}</a></td>
                <td>
                    <c:if test="${not empty cr.ocppIdTag}">
                        <a href="/steve/manager/ocppTags/details/${cr.ocppIdTag}">${cr.ocppIdTag}</a>
                    </c:if>
                </td>
                <td>${cr.name}</td>
                <td>${cr.phone}</td>
                <td>${cr.email}</td>
                <td>
                    <form:form action="/steve/manager/users/delete/${cr.userPk}">
                        <input type="submit" value="Delete">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>