<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>User Console</title>
	</head>
	<body>
		<h2>User Console</h2>
		<p>Welcome, ${sessionScope.get('username')}.</p>
		<s:a action="vote"><button>Votar</button></s:a>

		<a href="${sessionScope.get('accountAuthURL')}">
			<button>Link with Facebook</button>
		</a>
	</body>

</html>
