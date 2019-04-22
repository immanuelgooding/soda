package com.ihg.soda.model.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.ihg.soda.enums.Denominations;
import com.ihg.soda.enums.LiquidContainerTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.model.BeverageRequest;
import com.ihg.soda.model.BeverageResponse;
import com.ihg.soda.model.ChargeCard;
import com.ihg.soda.model.web.BeverageRequestModel;

@Controller
@RequestMapping("drink")
public class SodaMachineController {
	
	@GetMapping("/select")
	public String getSodaMachine(ModelMap model) {
		
		model.addAttribute("brands", ProductBrands.values());
		model.addAttribute("conatinerTypes", LiquidContainerTypes.values());
		model.addAttribute("denominations", Denominations.values());
		
		model.addAttribute("beverageRequestModel", new BeverageRequestModel());
		
		return "sodaMachineScreen";
	}
	
	@PostMapping("/select")
	public ResponseEntity<BeverageResponse> postSodaMachine(@ModelAttribute BeverageRequestModel model) throws URISyntaxException {
		List<Denominations> cash = new ArrayList<>();
		IntStream.range(0, model.getDenominationQuantity()).forEach(i -> cash.add(model.getDenomination()));
		
		ChargeCard chargeCard = ChargeCard.builder()
		.cardNumber(model.getCardNumber())
		.name(model.getCardName())
		.expiryMonth(model.getExpiryMonth())
		.expiryYear(model.getExpiryYear())
		.provider(model.getProvider())
		.build();
		
		
		BeverageRequest beverageRequest = BeverageRequest.builder()
		.brand(model.getBrand())
		.containerType(model.getContainerType())
		.cash(cash)
		.build();
		
		if(!chargeCard.getProvider().trim().isEmpty()) {
			beverageRequest.setChargeCard(chargeCard);
		}
		
		RequestEntity<BeverageRequest> request = RequestEntity
			     .post(new URI("http://localhost:8080/drink/purchase"))
			     .accept(MediaType.APPLICATION_JSON)
			     .body(beverageRequest);
		
		 RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BeverageResponse> response = restTemplate .exchange(request, BeverageResponse.class);
		 return response;
		
	}

}
