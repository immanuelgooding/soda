package com.ihg.soda.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.api.repository.DispensableProductRepository;
import com.ihg.soda.api.vending.device.VendingMachine;
import com.ihg.soda.api.vending.fund.BankNote;
import com.ihg.soda.api.vending.fund.Coin;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.enums.ProductTypes;

@Configuration
public class VendingMachineConfiguration {
	
	@Autowired
	private VendingMachineConfigurationProperties vendingMachineConfigProps;
	@Autowired
	private ProductConfigurationProperties productConfig;
	@Autowired
	private DispensableProductRepository productRepository;
	
	@Bean
	public VendingMachine vendingMachine() {
		VendingMachine machine = new VendingMachine(vendingMachineConfigProps.getCurrency());
		machine.setMaxUnitsPerProduct(vendingMachineConfigProps.getMaxCount());
		machine.setNumberOfProductQueues(vendingMachineConfigProps.getQueues());
		return machine;
	}
	
	@Bean
	public Set<Coin> acceptedCoins() {
		Coin quarter = Coin.builder().amount(BigDecimal.valueOf(0.25d)).commonName("Quarter").build();
		Coin dime = Coin.builder().amount(BigDecimal.valueOf(0.10d)).commonName("Dime").build();
		return new HashSet<Coin>(Arrays.asList(quarter, dime));
	}
	
	@Bean
	public Set<BankNote> acceptedBankNotes() {
		BankNote singleDollar = BankNote.builder().amount(BigDecimal.valueOf(1.00d)).commonName("1 Dollar Bill").build();
		BankNote fiveDollar = BankNote.builder().amount(BigDecimal.valueOf(5.00d)).commonName("5 Dollar Bill").build();
		return new HashSet<BankNote>(Arrays.asList(singleDollar, fiveDollar));
	}
	
	@PostConstruct
	public void initialize() {
		vendingMachine().setMachineState(MachineStates.AWAIT_PAYMENT);
		Iterable<DispensableProduct> productInventory = initializeProductInventory();
		stockVendingMachine(productInventory);
	}
	
	/**
	 * <ul>
	 * <li>Build products for each brand (e.g. Coke, Fanta, Sprite)</li>
	 * <li>Persist all products - allows for deletion from repository when dispensed</li>
	 * </ul>
	 * @return 
	 * @see {@link ProductBrands}
	 */
	private Iterable<DispensableProduct> initializeProductInventory() {
		List<DispensableProduct> inventory = productConfig.getProducts().stream()
		.flatMap(productDetail -> {
			List<DispensableProduct> products = new ArrayList<>();
			
			IntStream.range(0, vendingMachine().getMaxUnitsPerProduct())
			.forEach(i -> products.add(buildProduct(productDetail)));
			
			return products.stream();
		})
		.collect(Collectors.toList());
		return productRepository.saveAll(inventory);
	}

	private DispensableProduct buildProduct(ProductDetail detail) {
		FinancialExchange price = FinancialExchange.builder()
				.amount(BigDecimal.ONE)
				.currency(vendingMachine().getDefaultCurrency())
				.build();
		
		return 	DispensableProduct.builder()
				.price(price)
				.productDetail(detail)
				.productStatus(ProductStatuses.UNSTOCKED)
				.productType(ProductTypes.DRINK)
				.build();
	}

	/**
	 * Create a {@link Map} where k is {@link ProductDetail} and
	 * v is an implementation of a {@link Queue} of products. 
	 * Also ensures that each product queue is limited to the 
	 * maximum number of products specified for said queue.
	 * 
	 * @param inventory
	 */
	private void stockVendingMachine(Iterable<DispensableProduct> inventory) {
		Optional.ofNullable(vendingMachine().getProductStock()).ifPresent(stock -> stock.clear());
		
		EnumSet<ProductStatuses> stockableProductStatuses = EnumSet.of(ProductStatuses.UNSTOCKED, ProductStatuses.STOCKED);
		List<DispensableProduct> allProducts = StreamSupport.stream(inventory.spliterator(), false)
				.filter(product -> stockableProductStatuses.contains(product.getProductStatus()))
				.collect(Collectors.toList());
		
		List<DispensableProduct> stockedProducts = new ArrayList<>();
		Map<ProductDetail, LinkedList<DispensableProduct>> productMap = allProducts.stream()
				.collect(Collectors.groupingBy(DispensableProduct::getProductDetail, 
					Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
											.limit(vendingMachine().getMaxUnitsPerProduct().intValue())
											.peek(product -> {
												product.setProductStatus(ProductStatuses.STOCKED);
												stockedProducts.add(product);
											})
											.collect(Collectors.toCollection(LinkedList::new)))));
			
		vendingMachine().setProductStock(productMap);
		productRepository.saveAll(stockedProducts);
	}
	
}
