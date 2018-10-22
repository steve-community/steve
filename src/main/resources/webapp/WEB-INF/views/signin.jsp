<%@ page contentType="text/html" pageEncoding="utf-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="00-context.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<link rel="icon" href="${ctxPath}/static/images/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="${ctxPath}/static/images/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" type="text/css" href="${ctxPath}/static/css/style.css">
	<title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
<div class="top-banner"><div class="container"><a href="${ctxPath}/manager/home"><img src="${ctxPath}/static/images/logo2.png" height="80"></a></div></div>
<div class="top-menu"></div>
<div class="main-wrapper">
    <c:if test="${param.error != null}">
        <div class="error">Your name or password is incorrect.</div>
    </c:if>
    <div class="content">
        <section><span>Sign In</span></section>
        <form method="POST" action="${ctxPath}/manager/signin">
            <table class="userInput">
                <tr><td>Name:</td><td><input type="text" name="username" id="username" required /></td></tr>
                <tr><td>Password:</td><td><input type="password" name="password" id="password" required /></td></tr>
                <tr><td></td><td id="add_space"><input type="submit" value="Sign In"></td></tr>
            </table>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
<%@ include file="00-footer.jsp" %>
