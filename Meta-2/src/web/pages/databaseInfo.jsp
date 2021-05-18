<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <title>Database Info</title>
  </head>
  <body>
      <div>
          <h2>Database Info</h2>
          <s:a action="databaseInfo"><button>Refresh</button></s:a>
      </div>
      <div>
          <c:choose>
              <c:when test="${sessionScope.get('elections') == null || sessionScope.get('lists') == null || sessionScope.get('people') == null}">
                A problem occurred during the search!
              </c:when>
              <c:otherwise>
                  <h3>Elections</h3>
                  <c:forEach var="item" items="${sessionScope.get('elections')}" >
                      <c:out value="${item}"/> <br/>
                  </c:forEach>
                  <h3>Lists</h3>
                  <c:forEach var="item" items="${sessionScope.get('lists')}" >
                      <c:out value="${item}"/> <br/>
                  </c:forEach>
                  <h3>People</h3>
                  <c:forEach var="item" items="${sessionScope.get('people')}" >
                      <c:out value="${item}"/> <br/>
                  </c:forEach>
              </c:otherwise>
          </c:choose>
      </div>
  </body>
</html>
