package com.trader.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.trader.core.TraderCoreLambdaHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TraderLambdaHandler extends TraderCoreLambdaHandler {
    static {
        try {
            TraderCoreLambdaHandler.initHandler(TraderApplication.class);
        } catch (Exception e) {
            System.out.println(new StringBuilder("Error al inicializar el Handler").append(e.toString()));
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        this.startHandler(inputStream, outputStream, context);
    }

}
