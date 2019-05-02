<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<html>
<head>
<title>Soda Pop Vending Machine</title>

<style type="text/css">

body {
	font-family: monospace;
}

button {
	width: 125px;
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

<script src="http://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$('#cashPay').attr('checked', true);
	$('.card').hide();
	
	$("input[name='paymentType']").click(function() {
		
		if($('#cashPay').is(':checked')) {
			$('.card').find('input').prop('disabled', true);
			$('.card').hide();
			
			$('.cash').find('input').prop('disabled', false);
			$('.cash').show();
			
			$('#ejectCoins').show();
			$('#ejectBills').show();
		}
		
		if($('#cardPay').is(':checked')) {
			$('.cash').find('input').prop('disabled', true);
			$('.cash').hide();
			
			$('.card').find('input').prop('disabled', false);
			$('.card').show();
			
			$('#ejectCoins').hide();
			$('#ejectBills').hide();
		}
	});
	
	
	$('#ejectCoins').click(function() {
		$(".coins input:checked").prop('checked', false);
	});
	
	$('#ejectBills').click(function() {
		$(".bills input:checked").prop('checked', false);
	});
	
	$('#beverageForm').submit(function() {
		var selectedPayOption = $(".payOptions input:checked").val();
		switch(selectedPayOption) {
		case "card":
			$(this).attr('action', 'card');
			break;
		default:
			$(this).attr('action', 'cash');
		}
	});
	
});
	
</script>

</head>
<body>
	<div id="price">All Beverages: $1.00 <br>(Today Only)</div>
	<form:form method="post" id="beverageForm" action="cash" modelAttribute="beverageRequestModel">
        <table>
        <tr>
	        <td><form:label path="brand" cssClass="dateEntryGuide">Choose Beverage</form:label></td>
        </tr>
        <tr class="optionsOffered">
	        <td><form:radiobuttons path="brand" items="${brands}" /></td>
        </tr>
        
        <tr>
	        <td><form:label path="packaging" cssClass="dateEntryGuide">Choose Beverage Packaging</form:label></td>
        </tr>
        <tr class="optionsOffered">
	        <td><form:radiobuttons path="packaging" items="${packagings}" /></td>
		</tr>
		
		<tr>
			<td><label for="paymentType" class="dateEntryGuide">Form of payment:</label></td>
		</tr>
		<tr class="optionsOffered payOptions">
	        <td>
				<span>
	        	<input type="radio" id="cashPay" name="paymentType" value="cash">
	        	<label for="cash">Cash</label>
	        	<input type="radio" id="cardPay" name="paymentType" value="card">
	        	<label for="card">Card</label>
	        	</span>
	        </td>
		</tr>
		
		<tr class="cash">
			<td><form:label path="coinName" cssClass="dateEntryGuide">Choose Coin</form:label></td>
        </tr>
        <tr class="optionsOffered coins cash">
	        <td><form:radiobuttons path="coinName" items="${coins}" /></td>
		</tr>
		<tr class="cash">
			<td><form:label path="coinQuantity" cssClass="dateEntryGuide">Choose Coin Quantity</form:label></td>
        </tr>
        <tr class="optionsOffered coins cash">
			<td><form:radiobuttons path="coinQuantity" items="${denominationQuantity}" /></td>
		</tr>
		
		<tr class="cash">
			<td><form:label path="bankNoteName" cssClass="dateEntryGuide">Choose Bill</form:label></td>
        </tr>
		<tr class="optionsOffered bills cash">
	        <td><form:radiobuttons path="bankNoteName" items="${bankNotes}"/></td>
		</tr>
		<tr class="cash">
			<td><form:label path="bankNoteQuantity" cssClass="dateEntryGuide">Choose Bill Quantity</form:label></td>
        </tr>
        <tr class="optionsOffered bills cash">
			<td><form:radiobuttons path="bankNoteQuantity" items="${denominationQuantity}" /></td>
		</tr>
		
		<tr class="card">
        	<td><form:label path="provider" cssClass="dateEntryGuide">Enter Card Provider</form:label></td>
        </tr>
        <tr class="card">
        	<td><form:input path="provider" placeholder=" mastercard, visa, etc."/></td>
		</tr>
        
        <tr>
	        <td>
		        <button type="button" id="ejectCoins" >Eject Coins</button>
		        <button type="button" id="ejectBills" >Eject Bills</button>
	        </td>
        </tr>
        
        <tr>
	        <td>
		        <button>Purchase</button>
		        <button type="reset">Reset</button>
	        </td>
        </tr>
        </table>
    </form:form>
</body>
</html>