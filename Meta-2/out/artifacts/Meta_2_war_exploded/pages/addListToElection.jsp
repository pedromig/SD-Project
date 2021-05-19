<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>Add List To Election</title>
	</head>
	<body>
		<s:form action="addListToElection">
			<s:if test="selectedElection == null">
				<h2>Add List To Election [1/2]</h2>
				<s:select name="selectedElection" label="Select Election" list="electionsOpts"/>
			</s:if>
			<s:elseif test="selectedList == null">
				<h2>Add List To Election [2/2]</h2>
				<s:select name="selectedList" label="Select List" list="listOpts"/>
			</s:elseif>

			<s:submit/>
		</s:form>
	</body>
</html>
