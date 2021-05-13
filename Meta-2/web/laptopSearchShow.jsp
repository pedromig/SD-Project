
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Search page</title>
</head>
<body>
<div id="header">
    <s:form method="GET" action="laptopSearch">
        <s:textfield name="inputObject.model" label="Model" />
        <s:textfield name="inputObject.brand" label="Manufacturer" />
        <s:textfield name="inputObject.price" label="Price" />
        <s:textfield name="inputObject.screenSize" label="Screen size" />
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
                    Model: <c:out value="${item.model}" /> <br />
                    Manufacturer: <c:out value="${item.brand}" /> <br />
                    Price: <c:out value="${item.price}" /> <br />
                    Screen size: <c:out value="${item.screenSize}" /> <br />
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>
</body>
</html>
