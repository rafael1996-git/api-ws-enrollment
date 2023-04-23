package com.trader.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bank{
    @JsonProperty
    private Integer idBank;
    @JsonProperty
    private String name;
}
