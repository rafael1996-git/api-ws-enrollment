package com.trader.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateUrl {
    @JsonProperty
    private Long fcUser;
    @JsonProperty
    private String password;
    @JsonProperty
    private String cerUrl;
    @JsonProperty
    private String keyUrl;
}
