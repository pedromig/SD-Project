<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<title>Sign Up</title>
	</head>
	<body>
		<h2>Sign Up menu</h2>
		<s:form action="signUp" method="post">
			<s:select name="personType" label="Select Job" list="{'Student','Teacher','Employee'}"/>
			<s:textfield name="name" label="Name"/>
			<s:password name="password" label="Password"/>
			<s:textfield name="address" label="Address"/>
			<s:textfield name="faculty" label="Faculty"/>
			<s:textfield name="department" label="Department"/>
			<s:textfield name="phoneNumber" label="Phone Number"/>
			<s:textfield name="identityCardNumber" label="IdentityCardNumber"/>
			<s:textfield name="identityCardExpiryDate" label="ID Card Expiry Date" type="date"/>

			<s:submit/>
		</s:form>
	</body>
</html>
