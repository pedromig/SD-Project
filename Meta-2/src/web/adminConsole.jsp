<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Admin Console</title>
</head>
<body>
	<%--	Bypass Protection	--%>
	<c:choose>
		<c:when test="${(sessionScope.get('rmiServerOnline') == true) && (sessionScope.get('isAdmin') == true)}">
			<h2>Admin Console</h2>
			<p>Welcome, ${sessionScope.get('username')}. Say HEY to someone.</p>
			<s:a action="">
				<s:submit type="button">Sign up</s:submit>
			</s:a>

			<s:a action="">
				<s:submit type="button">Create Election</s:submit>
			</s:a>

		</c:when>
		<c:otherwise>
			Trying to bypass huh?
		</c:otherwise>
	</c:choose>
</body>
</html>
