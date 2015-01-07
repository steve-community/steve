<%@ page contentType="text/html" pageEncoding="utf-8" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="icon" href="/steve/static/images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="/steve/static/images/favicon.ico" type="image/x-icon" />
    <link rel="stylesheet" type="text/css" href="/steve/static/css/style.css">
    <script type="text/javascript" src="/steve/static/js/jquery-2.0.3.min.js" ></script>
    <script type="text/javascript" src="/steve/static/js/script.js" ></script>
    <title>SteVe - Steckdosenverwaltung</title>
</head>
<body>
<div class="main">
    <div class="top-banner">
        <div class="container">
            <a href="/steve/manager/home"><img src="/steve/static/images/logo2.png" height="80"></a>
        </div>
    </div>
    <div class="top-menu">
        <div class="container">
                <ul class="navigation">
                    <li><a href="/steve/manager/home">HOME</a></li>
                    <li><a>DATA MANAGEMENT &raquo;</a>
                        <ul>
                            <li><a href="/steve/manager/chargepoints">CHARGE POINTS</a></li>
                            <li><a href="/steve/manager/users">USERS</a></li>
                            <li><a href="/steve/manager/reservations">RESERVATIONS</a></li>
                            <li><a href="/steve/manager/transactions">TRANSACTIONS</a></li>
                        </ul>
                    </li>
                    <li><a>OPERATIONS &raquo;</a>
                        <ul>
                            <li><a href="/steve/manager/operations/v1.2">OCPP v1.2</a></li>
                            <li><a href="/steve/manager/operations/v1.5">OCPP v1.5</a></li>
                            <li><a href="/steve/manager/operations/tasks">Tasks</a></li>
                        </ul>
                    </li>
                    <li><a href="/steve/manager/settings">SETTINGS</a></li>
                    <li><a href="/steve/manager/log">LOG</a></li>
                    <li><a href="/steve/manager/about">ABOUT</a></li>
                    <li><a href="/steve/manager/signout">SIGN OUT</a></li>
                </ul>
            </div>
        </div>
    <div class="main-wrapper">