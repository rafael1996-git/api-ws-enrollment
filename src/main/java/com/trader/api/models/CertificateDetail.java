package com.trader.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDetail {
    @JsonProperty
    private Long id;
    @JsonProperty
    private boolean key;
    @JsonProperty
    private boolean cer;
    @JsonProperty
    private String password;
}
