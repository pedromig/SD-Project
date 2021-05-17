<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
	<body>
	<h2>Login</h2>
		<s:form action="login" method="post">
			<s:textfield name="username" label="ID Card"/>
			<s:password name="password" label="Password"/>
			<s:submit />
		</s:form>
	</body>

</html>