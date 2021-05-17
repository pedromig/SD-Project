<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Login</title>
</head>
	<body>
		<%--	Bypass Protection	--%>
		<c:choose>
			<c:when test="${sessionScope.get('rmiServerOnline') == true}">
				<h2>Login</h2>
				<s:form action="login" method="post">
					<s:textfield name="username" label="ID Card"/>
					<s:password name="password" label="Password"/>
					<s:submit />
				</s:form>
			</c:when>
			<c:otherwise>
				Trying to bypass Login huh?
			</c:otherwise>
		</c:choose>
	</body>
</html>