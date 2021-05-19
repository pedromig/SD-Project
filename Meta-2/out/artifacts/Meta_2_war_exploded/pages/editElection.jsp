<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Edit Election Menu</title>
    </head>
    <body>
        <h2></h2>
            <s:if test="selectedElectionJsp == null">
                <s:form action="editElectionMenu">
                    <h2>Edit Election Menu [1/2]</h2>
                    <s:select name="selectedElectionJsp" label="Select Election to Edit" list="electionsOpts"/>
                    <s:submit/>
                </s:form>
            </s:if>
            <s:if test="selectedElectionJsp != null">
                <h2>Edit Election Menu [2/2]</h2>
                <h3>Note: The submit button will only edit the respective attribute</h3>
                <s:form action="editElectionAttribute">
                    <s:textfield name="name" label="Election Name"/>
                    <s:submit/>
                </s:form>
                <s:form action="editElectionAttribute">
                    <s:textfield name="description" label="Election Description"/>
                    <s:submit/>
                </s:form>
                <s:form action="editElectionAttribute">
                    <s:textfield name="startDate" label="Start Date" type="datetime-local"/>
                    <s:submit/>
                </s:form>
                <s:form action="editElectionAttribute">
                    <s:textfield name="endDate" label="End Date" type="datetime-local"/>
                    <s:submit/>
                </s:form>
                <div>
                    <s:a action=""><button>Add Department</button></s:a>
                    <s:a action=""><button>Remove Department</button></s:a>
                    <s:a action=""><button>Add Restraint</button></s:a>
                    <s:a action=""><button>Remove Restraint</button></s:a>
                </div>
            </s:if>


    </body>
</html>
