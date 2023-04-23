package com.trader.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonProperty
    private Long fcUser;
    @JsonProperty
    private String password;
    @JsonProperty
    private Integer typeData;
}
