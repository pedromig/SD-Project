
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Search page</title>
</head>
<body>
<div id="header">
    <s:form method="GET" action="shoesSearch">
        <s:textfield name="inputObject.size" label="Size" />
        <s:textfield name="inputObject.color" label="Color" />
        <s:textfield name="inputObject.brand" label="Brand" />
        <s:submit label="Search!" />
    </s:form>
</div>
<div id="main">
    <c:choose>
        <c:when test="${results == null}">
            A problem occurred during the search!
        </c:when>
        <c:when test="${results.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${results.size()} products!
            <br />
            <c:forEach items="${results}" var="item">
                <div>
                    Size: <c:out value="${item.size}" /> <br />
                    Color: <c:out value="${item.color}" /> <br />
                    Brand: <c:out value="${item.brand}" /> <br />
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>
</body>
</html>
