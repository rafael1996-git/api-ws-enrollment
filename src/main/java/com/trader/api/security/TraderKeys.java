package com.trader.api.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class TraderKeys implements Serializable {

    private static final long serialVersionUID = 1L;
    private String traderMainKey;
    private String pwdKey;
}
