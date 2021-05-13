<%--
  Created by IntelliJ IDEA.
  User: frederico
  Date: 19-09-2018
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Search page</title>
</head>
<body>
<div id="header">
    <s:form method="GET" action="carSearch">
        <s:textfield name="inputObject.carModel" label="Model" />
        <s:textfield name="inputObject.manufacturer" label="Manufacturer" />
        <s:textfield name="inputObject.year" label="Year" />
        <s:textfield name="inputObject.price" label="Price" />
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
                    Model: <c:out value="${item.carModel}" /> <br />
                    Manufacturer: <c:out value="${item.manufacturer}" /> <br />
                    Year: <c:out value="${item.year}" /> <br />
                    Price: <c:out value="${item.price}" /> <br />
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>
</body>
</html>
