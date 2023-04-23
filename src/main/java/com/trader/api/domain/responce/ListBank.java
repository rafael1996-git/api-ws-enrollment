package com.trader.api.domain.responce;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trader.api.models.Bank;
import com.trader.core.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBank extends BaseModel {
    @JsonProperty
    private List<Bank> banks;
}
