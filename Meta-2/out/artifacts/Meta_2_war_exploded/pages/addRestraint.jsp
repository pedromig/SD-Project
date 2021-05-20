<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Add Restraint</title>
    </head>
    <body>
        <h2>Add Restraint</h2>
        <s:form action="addRestraint">
            <s:select name="selectedDepartmentName" label="Select Department Restraint" list="departments"/>
            <s:submit/>
        </s:form>
    </body>
</html>
