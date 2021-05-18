<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<title>Create Election</title>
	</head>
	<body>
		<h2>Create Election Menu</h2>
		<s:form action="createElection" method="post">
			<s:select name="electionType" label="Election Type" list="{'Student','Teacher','Employee'}"/>
			<s:textfield name="name" label="Election Name"/>
			<s:textfield name="description" label="Election Description"/>

			<s:textfield name="startDate" label="Start Date" type="datetime-local"/>
			<s:textfield name="endDate" label="End Date" type="datetime-local"/>

			<s:submit/>
		</s:form>
	</body>
</html>