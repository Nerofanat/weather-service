package com.example.serving_web_content.bot;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpClient {
    private static OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://serving-web-content.onrender.com";

    private HttpClient() {} // Сделать приватным, чтобы предотвратить создание экземпляров

    public static HttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    public String sendGet(String path) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}