package com.trader.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateUser {

    @JsonProperty
    private Long fcUser;
    @JsonProperty
    private String password;
    @JsonProperty
    private String cerBase64;
    @JsonProperty
    private String keyBase64;
}
