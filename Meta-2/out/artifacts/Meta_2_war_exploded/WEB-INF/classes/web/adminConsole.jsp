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
				<p>Welcome, ${sessionScope.get('username')}.</p>

				<h3>People</h3>
				<s:a href="signUp.jsp"><s:submit type="button">Sign up</s:submit></s:a>

				<h3>Database Info</h3>
				<s:a action=""><s:submit type="button">Database Info</s:submit></s:a>

				<h3>Elections</h3>
				<s:a action=""><s:submit type="button">Create Election</s:submit></s:a>
				<s:a action=""><s:submit type="button">Edit Election</s:submit></s:a>
				<s:a action=""><s:submit type="button">Ended Elections Log</s:submit></s:a>
				<s:a action=""><s:submit type="button">Person Audit</s:submit></s:a>

				<h3>Lists</h3>
				<s:a action=""><s:submit type="button">Create List</s:submit></s:a>
				<s:a action=""><s:submit type="button">Add List to Election</s:submit></s:a>
				<s:a action=""><s:submit type="button">Remove List from Election</s:submit></s:a>
				<s:a action=""><s:submit type="button">Add People to List </s:submit></s:a>
				<s:a action=""><s:submit type="button">Remove People from List </s:submit></s:a>

				<h3>Real Time Data</h3>
				<p>To be continued... (websockets)</p>
			</c:when>
			<c:otherwise>
				Trying to bypass huh?
			</c:otherwise>
		</c:choose>
	</body>
</html>
