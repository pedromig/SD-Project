<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Remove Restraint</title>
    </head>
        <body>
        <h2>Remove Restraint</h2>
        <s:form action="removeRestraint">
            <s:select name="selectedDepartmentName" label="Select Department" list="departments"/>
            <s:submit/>
        </s:form>
    </body>
</html>
