<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<head>
<title>Soda Pop Vending Machine</title>
</head>
<body>
	<form:form method="post" action="/drink/select" modelAttribute="beverageRequestModel">
        <table>
        <tr>
        <td>
        <form:label path = "brand">Select Beverage</form:label>
        </td>
        <td>
		<form:select path="brand">
		    <form:options items="${brands}" />
		</form:select>
		</td>
        </tr>
        
        <tr>
        <td>
        <form:label path = "containerType">Select Beverage Packaging</form:label>
        </td>
        <td>
		<form:select path="containerType">
		    <form:options items="${containerTypes}" />
		</form:select>
		</td>
		</tr>
		
		<tr>
		<td>
        <form:label path = "denomination">Select Cash Denomination</form:label>
        </td>
        <td>
		<form:select path="denomination">
		    <form:options items="${denominations}" />
		</form:select>
		</td>
		</tr>
		
		<tr>
		<td>
        <form:label path = "denominationQuantity">Enter Denomination Quantity</form:label>
        </td>
		<td><form:radiobutton path = "denominationQuantity" value = "1" label = "1" /></td>
        <td><form:radiobutton path = "denominationQuantity" value = "2" label = "2" /></td>
        <td><form:radiobutton path = "denominationQuantity" value = "3" label = "3" /></td>
        <td><form:radiobutton path = "denominationQuantity" value = "4" label = "4" /></td>
        <td><form:radiobutton path = "denominationQuantity" value = "5" label = "5" /></td>
		</tr>
		
		<tr>
        <td><form:label path = "cardName">Card Holder</form:label></td>
        <td><form:input path = "cardName" /></td>
        <td><form:label path = "cardNumber">Card Number</form:label></td>
        <td><form:input path = "cardNumber" /></td>
        <td><form:label path = "expiryMonth">Expiration Month</form:label></td>
        <td><form:input path = "expiryMonth" /></td>
        <td><form:label path = "expiryYear">Expiration Year</form:label></td>
        <td><form:input path = "expiryYear" /></td>
        <td><form:label path = "provider">Card Provider (e.g. MasterCard, Visa)</form:label></td>
        <td><form:input path = "provider" /></td>
		</tr>
        
        <tr>
        <td>
        <input type="submit" />
        </td>
        </tr>
        </table>
    </form:form>
</body>
</html>