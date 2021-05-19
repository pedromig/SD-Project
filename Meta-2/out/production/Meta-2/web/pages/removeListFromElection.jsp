<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Remove List From Election</title>
    </head>
    <body>
        <s:form action="removeListFromElection">
            <s:if test="selectedElectionJsp == null">
                <h2>Remove List From Election [1/2]</h2>
                <s:select name="selectedElectionJsp" label="Select Election" list="electionsOpts"/>
            </s:if>
            <s:elseif test="selectedListJsp == null">
                <h2>Remove List From Election [2/2]</h2>
                <s:select name="selectedListJsp" label="Select List" list="listOpts"/>
            </s:elseif>

            <s:submit/>
        </s:form>
    </body>
</html>
