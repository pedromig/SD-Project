<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <title> </title>
    </head>
    <body>
        <h1>Welcome to E-Voting!</h1>
        <s:url action="login" var="loginUrl" />
        <s:a href="%{loginUrl}" >Login</s:a>
    </body>
</html>
