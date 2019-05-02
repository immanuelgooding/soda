package com.ihg.soda.api.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihg.soda.api.vending.fund.BankNote;
import com.ihg.soda.api.vending.fund.Coin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(value = Include.NON_EMPTY)
public class CashRequest extends ProductRequest {
	
	private List<Coin> coins;
	private List<BankNote> bankNotes;

}
