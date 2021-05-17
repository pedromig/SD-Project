<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head>
        <title>E-Voting</title>
    </head>
    <body>
        <h1>Welcome to E-Voting!</h1>
        <s:url action="connect" var="connectURL" />
        <s:a href="%{connectURL}" >Start!</s:a>
    </body>
</html>
