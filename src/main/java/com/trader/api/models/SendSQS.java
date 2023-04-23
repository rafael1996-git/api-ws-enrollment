package com.trader.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSQS {
    @JsonProperty
    private Long idTrader;
    @JsonProperty
    private String base64Cer;
    @JsonProperty
    private String base64Key;
}
