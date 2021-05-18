<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<title>Error page</title>
	</head>
	<body>
		<div>
			<h2>This is an error page </h2>
<%--			fazer um if para redirecionar para index caso o rmi falhe, login caso não esteja logged e admin/user caso esteja logged--%>
			<s:a action="index"><button>Index Page</button></s:a>

		</div>
		<p></p>
		<IMG SRC="https://media.giphy.com/media/4Vtk42BGiL1T2/giphy.gif">
	</body>
</html>
