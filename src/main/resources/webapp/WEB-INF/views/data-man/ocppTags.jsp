<%@ include file="../00-header.jsp" %>
<div class="content"><div>
    <form:form action="/steve/manager/ocppTags/query" method="get" modelAttribute="params">
        <section><span>
        Ocpp Tag Overview
        <a class="tooltip" href="#"><img src="/steve/static/images/info.png" style="vertical-align:middle">
            <span>Deleting an Ocpp tag causes losing all related information including transactions and reservations.</span>
        </a>
        </span></section>
        <table class="userInput">
            <tr>
                <td>ID Tag:</td>
                <td><form:select path="idTag">
                    <option value="" selected>All</option>
                    <form:options items="${idTagList}"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Parent ID Tag:</td>
                <td><form:select path="parentIdTag">
                    <option value="" selected>All</option>
                    <form:options items="${parentIdTagList}"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Expired?:</td>
                <td><form:select path="expired">
                    <form:options items="${expired}" itemLabel="value"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>In Transaction?:</td>
                <td><form:select path="inTransaction">
                    <form:options items="${inTransaction}" itemLabel="value"/>
                </form:select>
                </td>
            </tr>
            <tr>
                <td>Blocked?:</td>
                <td><form:select path="blocked">
                    <form:options items="${blocked}" itemLabel="value"/>
                </form:select>
                </td>
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
            <tr><th>ID Tag</th>
                <th>Parent ID Tag</th>
                <th>Expiry Date/Time</th>
                <th>In Transaction?</th>
                <th>Blocked?</th>
                <th>
                    <form:form action="/steve/manager/ocppTags/add" method="get">
                        <input type="submit" value="Add New"/>
                    </form:form>
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${ocppTagList}" var="item">
            <tr><td><a href="/steve/manager/ocppTags/details/${item.idTag}">${item.idTag}</a></td>
                <td>
                    <c:if test="${not empty item.parentIdTag}">
                        <a href="/steve/manager/ocppTags/details/${item.parentIdTag}">${item.parentIdTag}</a>
                    </c:if>
                </td>
                <td>${item.expiryDate}</td>
                <td>${item.inTransaction}</td>
                <td>${item.blocked}</td>
                <td>
                    <form:form action="/steve/manager/ocppTags/delete/${item.idTag}">
                        <input type="submit" value="Delete">
                    </form:form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>