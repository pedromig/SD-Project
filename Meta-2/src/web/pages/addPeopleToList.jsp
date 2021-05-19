<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>Add People To List</title>
	</head>
	<body>
		<s:form action="addPeopleToList">
			<s:if test="selectedList == null">
				<h2>Add People To List [1/2]</h2>
				<s:select name="selectedList" label="Select List" list="listOpts"/>
			</s:if>
			<s:elseif test="selectedPerson == null">
				<h2>Add People To List [2/2]</h2>
				<s:select name="selectedPerson" label="Select Person" list="peopleOpts"/>
			</s:elseif>

			<s:submit/>
		</s:form>
	</body>
</html>
