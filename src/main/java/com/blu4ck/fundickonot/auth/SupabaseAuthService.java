package com.blu4ck.fundickonot.auth;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;

public class SupabaseAuthService {

    private static final String SUPABASE_URL = "https://fungolwlkfwkyfdyevrq.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ1bmdvbHdsa2Z3a3lmZHlldnJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM1MDAxNDAsImV4cCI6MjA1OTA3NjE0MH0.VwQ8xuUcGspJX_KijtNcWQ_HCTE1LgrO6JEBLuKTELI";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private JsonObject sessionData;

    public boolean signUp(String email, String password) throws IOException {
        String url = SUPABASE_URL + "/auth/v1/signup";

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public String getAccessToken() {
        if (sessionData != null && sessionData.has("access_token")) {
            return sessionData.get("access_token").getAsString();
        }
        return null;
    }


    public boolean signIn(String email, String password) throws IOException {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                this.sessionData = gson.fromJson(responseBody, JsonObject.class);
                return true;
            }
            return false;
        }
    }

    public boolean isEmailVerified() {
        if (sessionData != null && sessionData.has("user")) {
            JsonObject user = sessionData.getAsJsonObject("user");
            if (user.has("confirmed_at") && !user.get("confirmed_at").isJsonNull()) {
                return true;
            }
        }
        return false;
    }

    public String getUserId() {
        if (sessionData != null && sessionData.has("user")) {
            JsonObject user = sessionData.getAsJsonObject("user");
            return user.has("id") ? user.get("id").getAsString() : null;
        }
        return null;
    }
}
