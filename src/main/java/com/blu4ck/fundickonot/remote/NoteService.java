package com.blu4ck.fundickonot.remote;

import com.blu4ck.fundickonot.config.ConfigService;
import com.blu4ck.fundickonot.model.Note;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NoteService {
    private static final String PROJECT_URL = ConfigService.get("supabase.url");
    private static final String API_KEY = ConfigService.get("supabase.key");
     private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static boolean createNote(Note note, String accessToken) {
        try {
            String json = mapper.writeValueAsString(note);
            System.out.println("📦 Gönderilen JSON:\n" + json);
            System.out.println("🛡 AccessToken: " + accessToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/rest/v1/notes"))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🔁 Supabase Response:\nStatus Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Note> getNotes(String userId, String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/rest/v1/notes?user_id=eq." + userId + "&select=*"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody.trim().startsWith("{")) {
                Note note = mapper.readValue(responseBody, Note.class);
                return List.of(note);
            } else {
                return mapper.readValue(responseBody, new TypeReference<>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static boolean updateNote(Note note, String accessToken) {
        try {
            System.out.println("🛠 Güncellenmek istenen not ID: " + note.getId()); // 👈 log eklendi

            String json = mapper.writeValueAsString(note);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/rest/v1/notes?id=eq." + note.getId()))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + accessToken)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🔁 Supabase Response:\nStatus Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteNote(String noteId, String accessToken) {
        try {
            System.out.println("🛠 Silinmek istenen not ID: " + noteId); // 👈 log eklendi

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/rest/v1/notes?id=eq." + noteId))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + accessToken)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🔁 Supabase Response:\nStatus Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Note> searchNotes(String userId, String query, String folderType, String accessToken) {
        try {
            String encodedQuery = query.replace(" ", "%20");

            String url = PROJECT_URL + "/rest/v1/notes?" +
                    "user_id=eq." + userId +
                    "&folder_type=eq." + folderType +
                    "&or=(title.ilike.*" + encodedQuery + "*,content.ilike.*" + encodedQuery + "*)" +
                    "&select=*";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody.trim().startsWith("{")) {
                Note note = mapper.readValue(responseBody, Note.class);
                return List.of(note);
            } else {
                return mapper.readValue(responseBody, new TypeReference<>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
