package com.trader.api.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalInformation{
    @JsonProperty
    private Integer fcUser;
    @JsonProperty
    private String curp;
    @JsonProperty
    private String rfc;
    @JsonProperty
    private String taxResidence;
    @JsonProperty
    private String taxRegime;
    @JsonProperty
    private String bankKey;
    @JsonProperty
    private Integer idBank;
    @JsonProperty
    private String nameBank;

    @JsonProperty
    private String ineUrl;
    @JsonProperty
    private String curpUrl;
    @JsonProperty
    private String bankStatementUrl;
    @JsonProperty
    private String fiscalSituationUrl;

}
