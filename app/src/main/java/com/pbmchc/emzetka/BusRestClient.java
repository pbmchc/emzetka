package com.pbmchc.emzetka;

import com.pbmchc.emzetka.interfaces.BusLinesService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Piotrek on 2017-01-18.
 */
public class BusRestClient {

    private static final String URL = "service_url";
    private static final BusLinesService CLIENT;

    static {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CLIENT = retrofit.create(BusLinesService.class);
    }

    private BusRestClient(){}

    public static BusLinesService getClient(){
        return CLIENT;
    }
}
