<%@ page contentType="text/html" pageEncoding="utf-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="/steve/static/images/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="/steve/static/images/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" type="text/css" href="/steve/static/css/style.css">
	<title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
<div class="top-banner"><div class="container"><a href="/steve/manager/home"><img src="/steve/static/images/logo2.png" height="80"></a></div></div>
<div class="top-menu"></div>
<div class="main-wrapper">
    <c:if test="${param.error != null}">
        <div class="error">Your name or password is incorrect.</div>
    </c:if>
    <div class="content">
        <section><span>Sign In</span></section>
        <form method="POST" action="/steve/manager/signin">
            <table class="userInput">
                <tr><td>Name:</td><td><input type="text" name="username" id="username" required /></td></tr>
                <tr><td>Password:</td><td><input type="password" name="password" id="password" required /></td></tr>
                <tr><td></td><td id="add_space"><input type="submit" value="Sign In"></td></tr>
            </table>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>
</div>
<div class="footer">
	<a href="http://www.rwth-aachen.de"><img src="/steve/static/images/logo_rwth.png"></a>
	<a href="http://dbis.rwth-aachen.de"><img src="/steve/static/images/logo_i5.png"></a>
</div>
</body>
</html>