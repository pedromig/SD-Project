<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <title> </title>
    </head>
    <body>
        <h1>Welcome to E-Voting!</h1>
        <s:url action="connectRmi" var="rmiConnectUrl" />
        <s:a href="%{rmiConnectUrl}">Start!</s:a>
    </body>
</html>
