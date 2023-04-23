package com.trader.api.domain.responce;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trader.api.models.CertificateDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CerResponse {
    @JsonProperty
    private CertificateDetail certificate;
}
