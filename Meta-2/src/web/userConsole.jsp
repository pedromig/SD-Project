<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>User Console</title>
</head>
<body>
<%--	Bypass Protection	--%>
<c:choose>
	<c:when test="${(sessionScope.get('rmiServerOnline') == true) &&
					(sessionScope.get('username') != null) &&
					(sessionScope.get('isAdmin') == false)}">

		<h2>User Console</h2>

		<s:a action="">
			<s:submit type="button">Votar</s:submit>
		</s:a>

	</c:when>
	<c:otherwise>
		Trying to bypass huh?
	</c:otherwise>
</c:choose>
</body>
</html>
