package com.ihg.soda.api.vending.device;

import java.math.BigDecimal;
import java.util.Optional;

import com.ihg.soda.api.vending.fund.ChargeCard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class ChargeCardReader {

	public boolean cardPaymentAccepted(ChargeCard chargeCard, BigDecimal productPrice) {
		return Optional.ofNullable(chargeCard).map(card -> {
			boolean cardChargedSuccessfully = true; //TODO; card processing service here
			return cardChargedSuccessfully;
		}).orElseGet(() -> {
			log.error("Card details unavailable");
			return false;
		});
	}
	
	
}
