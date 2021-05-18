<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>Ended Elections Log</title>
	</head>
	<body>
	<div>
		<h2>Ended Elections Log</h2>
		<s:a action="endedElectionLogMenu"><button>Refresh</button></s:a>
	</div>
	<div>
		<c:choose>
			<c:when test="${sessionScope.get('endedElectionsLog') == null}">
				A problem occurred during the search!
			</c:when>
			<c:otherwise>
				<c:forEach var="item" items="${sessionScope.get('endedElectionsLog')}" >
					<c:out value="${item}"/> <br/>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>
	</body>
</html>
