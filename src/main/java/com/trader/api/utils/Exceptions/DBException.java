package com.trader.api.utils.Exceptions;

public class DBException extends Exception {
    private static final long serialVersionUID = 1L;
    private String code;

    public String getCode() {
        return code;
    }

    public DBException(String message, String code, Throwable t) {
        super(message,t);
        this.code = code;
    }

    public DBException(String message, String code){
        super(message);
        this.code = code;
    }
}
