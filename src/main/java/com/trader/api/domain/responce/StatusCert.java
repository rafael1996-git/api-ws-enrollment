package com.trader.api.domain.responce;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusCert {
    @JsonProperty
    private boolean status;
    @JsonProperty
    private int idStatus;
}
