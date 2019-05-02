package com.ihg.soda.api.vending.fund;

import com.ihg.soda.enums.DenominationTypes;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Coin extends Denomination {
	
	@Setter(value = AccessLevel.NONE)
	private final DenominationTypes denominationType = DenominationTypes.COIN;
	
}
