<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <title>Choose what you are looking for</title>
  </head>
  <body>
  <s:url action="carSearch" var="carSearchUrl" />
  <s:url action="laptopSearch" var="laptopSearchUrl" />
  <s:url action="shoesSearch" var="shoesSearchUrl" />

  Choose what you are looking for <br/>
  Cars <s:a href="%{carSearchUrl}" >click here!</s:a> <br/>
  Laptops <s:a href="%{laptopSearchUrl}" >click here!</s:a>  <br/>
  Shoes  <s:a href="%{shoesSearchUrl}" >click here!</s:a> <br/>

  </body>
</html>
