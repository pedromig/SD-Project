<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
      <title>Remove Department</title>
    </head>
    <body>
        <h2>Remove Department</h2>
        <s:form action="removeDepartment">
          <s:select name="selectedDepartmentName" label="Select Department" list="departments"/>
          <s:submit/>
        </s:form>
    </body>
</html>
