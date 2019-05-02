package com.ihg.soda.web.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ihg.soda.api.model.request.CardRequest;
import com.ihg.soda.api.model.request.CashRequest;
import com.ihg.soda.api.model.response.ProductResponse;
import com.ihg.soda.api.vending.fund.BankNote;
import com.ihg.soda.api.vending.fund.ChargeCard;
import com.ihg.soda.api.vending.fund.Coin;
import com.ihg.soda.api.vending.fund.Denomination;
import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.web.model.BeverageRequestModel;

@Controller
@RequestMapping("/vendingMachine")
public class VendingMachineController {
	
	@Autowired
	private Set<Coin> acceptedCoins;
	@Autowired
	private Set<BankNote> acceptedBankNotes;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@ModelAttribute("coins")
	public Set<String> getCoins() {
		return acceptedCoins.stream().map(Denomination::getCommonName).collect(Collectors.toSet());
	}
	
	@ModelAttribute("bankNotes")
	public Set<String> getBankNotes() {
		return acceptedBankNotes.stream().map(Denomination::getCommonName).collect(Collectors.toSet());
	}
	
	@ModelAttribute("denominationQuantity")
	public List<Integer> getDebnominationQuantity() {
		return IntStream.rangeClosed(1, 5).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	@GetMapping("/beverage/buy")
	public String getSodaMachine(ModelMap model) {
		
		model.addAttribute("brands", ProductBrands.values());
		model.addAttribute("packagings", PackagingTypes.values());
		model.addAttribute("beverageRequestModel", new BeverageRequestModel());
		
		return "sodaMachineScreen";
	}
	
	@PostMapping("/beverage/cash")
	public ResponseEntity<ProductResponse> postBeverageCash(@ModelAttribute BeverageRequestModel model) 
		throws URISyntaxException {
		
		List<Coin> coins = new ArrayList<>();
		IntStream.range(0, model.getCoinQuantity())
		.forEach(i -> {
			Coin coin = acceptedCoins.stream().filter(c -> model.getCoinName().equals(c.getCommonName())).findAny().get();
			coins.add(coin);
		});
		
		List<BankNote> bankNotes = new ArrayList<>();
		IntStream.range(0, model.getBankNoteQuantity())
		.forEach(i -> {
			BankNote bankNote = acceptedBankNotes.stream().filter(bn -> model.getBankNoteName().equals(bn.getCommonName())).findAny().get();
			bankNotes.add(bankNote);
		});
		
		CashRequest cashRequest = CashRequest.builder()
		.brand(model.getBrand())
		.packaging(model.getPackaging())
		.coins(coins)
		.bankNotes(bankNotes)
		.build();
		
		RequestEntity<CashRequest> request = RequestEntity
			     .post(new URI("http://localhost:8080/beverage/purchase/cash"))
			     .accept(MediaType.APPLICATION_JSON)
			     .body(cashRequest);
		
		return restTemplate.exchange(request, ProductResponse.class);
		
	}
	
	@PostMapping("/beverage/card")
	public ResponseEntity<ProductResponse> postBeverageCard(@ModelAttribute BeverageRequestModel model) 
			throws URISyntaxException {
		
		ChargeCard chargeCard = ChargeCard.builder().provider(model.getProvider()).build();
		CardRequest cardRequest = CardRequest.builder()
				.brand(model.getBrand())
				.packaging(model.getPackaging())
				.chargeCard(chargeCard)
				.build();
		
		RequestEntity<CardRequest> request = RequestEntity
				.post(new URI("http://localhost:8080/beverage/purchase/card"))
				.accept(MediaType.APPLICATION_JSON)
				.body(cardRequest);
		
		return restTemplate.exchange(request, ProductResponse.class);
		
	}

}
