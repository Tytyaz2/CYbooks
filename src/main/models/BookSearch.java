package main.models;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


class BookSearch {

    public void search(String keyword) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedKeyword;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Résultats de la recherche pour '" + keyword + "':");
            printBookDetails(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printBookDetails(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items != null) {
            for (JsonElement item : items) {
                JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Titre non disponible";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "Auteur(s) non disponible";
                String isbn = volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "ISBN non disponible";
                String kind = volumeInfo.has("kind") ? volumeInfo.get("kind").getAsString() : "Kind non disponible";

                System.out.println("Titre: " + title);
                System.out.println("Auteurs: " + authors);
                System.out.println("ISBN: " + isbn);
                System.out.println("Kind: " + kind);
                System.out.println();
            }
        } else {
            System.out.println("Aucun résultat trouvé.");
        }
    }

}