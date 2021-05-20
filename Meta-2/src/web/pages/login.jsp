<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<s:form action="login" method="post">
    <s:textfield name="username" label="ID Card"/>
    <s:password name="password" label="Password"/>
    <s:submit/>
</s:form>

<a href="${sessionScope.get('authURL')}">
    <button>Login With Facebook</button>
</a>
</body>
</html>