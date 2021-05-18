<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<title>Create List</title>
	</head>
	<body>
		<h2>Create List Menu</h2>
		<s:form action="createList" method="post">
			<s:select name="listType" label="Election Type" list="{'Student','Teacher','Employee'}"/>
			<s:textfield name="name" label="Election Name"/>
			<s:submit/>
		</s:form>
	</body>
</html>