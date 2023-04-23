package com.trader.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String key;
    @JsonProperty
    private String cer;
    @JsonProperty
    private String password;
}
