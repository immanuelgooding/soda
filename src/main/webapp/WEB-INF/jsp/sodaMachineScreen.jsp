<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<head>
<title>Soda Pop Vending Machine</title>

<style type="text/css">

body {
	font-family: monospace;
}

#price {
	font-size: -webkit-xxx-large;
    color: turquoise;
}

label.dateEntryGuide {
	margin-top: 15px;
    display: inline-block;
    width: 100%;
    font-weight: bolder;
}

tr.optionsOffered span {
    display: table-cell;
}

tr.optionsOffered span input {
    vertical-align: bottom;
}

</style>

<script type="text/javascript">
	
	function ejectCurrency() {
		var denom = document.getElementsByName("denomination");
		var denomQuantity = document.getElementsByName("denominationQuantity");
		
		var denomSelected;
		var denomQuantitySelected;
		
		for (var i = 0; i < denom.length; i++)
	    {
	        var currentDenom = denom[i];
			if (currentDenom.checked)
	        {
				denomSelected = currentDenom;
	        }
	    }
		
		for (var i = 0; i < denomQuantity.length; i++)
	    {
	        var currentDenomQuantity = denomQuantity[i];
			if (currentDenomQuantity.checked)
	        {
				denomQuantitySelected = currentDenomQuantity;
	        }
	    }
		
		if(denomSelected && denomQuantitySelected) {
			denomSelected.checked = false;
			denomQuantitySelected.checked = false;
		}
		
	}
</script>

</head>
<body>
	<div id="price">All Beverages: $1.00 <br>(Today Only)</div>
	<form:form method="post" action="/drink/buy" modelAttribute="beverageRequestModel">
        <table>
        <tr>
        <td>
        <form:label path = "brand" cssClass="dateEntryGuide">Choose Beverage</form:label>
        </td>
        </tr>
        <tr class="optionsOffered">
        <td>
		<form:radiobuttons path="brand" />
		</td>
        </tr>
        
        <tr>
        <td>
        <form:label path = "containerType" cssClass="dateEntryGuide">Choose Beverage Packaging</form:label>
        </td>
        </tr>
        <tr class="optionsOffered">
        <td>
		<form:radiobuttons path="containerType" />
		</td>
		</tr>
		
		<tr>
		<td>
        <form:label path = "denomination" cssClass="dateEntryGuide">Choose Cash Denomination</form:label>
        </td>
        </tr>
        <tr class="optionsOffered">
        <td>
		<form:radiobuttons path="denomination" />
		</td>
		</tr>
		
		<tr>
		<td>
        <form:label path = "denominationQuantity" cssClass="dateEntryGuide">Choose Denomination Quantity</form:label>
        </td>
        </tr>
        <tr class="optionsOffered">
		<td>
		<form:radiobuttons path="denominationQuantity" items="${denominationQuantity}" />
		</td>
		</tr>
		
		<tr>
        <td><form:label path = "provider" cssClass="dateEntryGuide">Enter Card Provider</form:label></td>
        </tr>
        <tr>
        <td><form:input path="provider" placeholder=" mastercard, amex, etc."/></td>
		</tr>
        
        <tr>
        <td>
        <button type="button" onclick="ejectCurrency()">Eject</button>
        <button>Purchase</button>
        <button type="reset">Reset</button>
        </td>
        </tr>
        </table>
    </form:form>
</body>
</html>