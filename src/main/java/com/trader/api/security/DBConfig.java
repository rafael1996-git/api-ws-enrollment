package com.trader.api.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class DBConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String user;
    private String pass;
}
