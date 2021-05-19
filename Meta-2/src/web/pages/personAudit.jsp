<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>Person Audit</title>
	</head>
	<body>
		<h2>Person Audit</h2>
		<s:form action="personAuditMenu">
			<s:select name="selectedPerson" label="Pick a Person" list="peopleAuditNames"/>
			<s:submit/>
		</s:form>
		<s:if test="selectedPerson != null">
			<h3>Result</h3>
			<c:forEach var="item" items="${sessionScope.get('peopleAudit')}">
				<c:out value="${item}"/> <br/>
			</c:forEach>
		</s:if>
	</body>
</html>
