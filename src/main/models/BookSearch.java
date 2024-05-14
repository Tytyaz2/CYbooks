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
import java.util.ArrayList;
import java.util.List;



public class BookSearch {
    /*public List<Book> search(String keyword, int startIndex, int pageSize) {
        return search(keyword, 20); // Appel de la méthode overloaded avec une limite de 20 livres
    }*/
    public List<Book> search(String keyword, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedKeyword = keyword != null ? URLEncoder.encode(keyword, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedKeyword + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Titre non disponible";
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "Auteur(s) non disponible";
                    String isbn = volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "ISBN non disponible";
                    String kind = volumeInfo.has("kind") ? volumeInfo.get("kind").getAsString() : "Kind non disponible";

                    books.add(new Book(title, authors, isbn, kind));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
    public List<Book> searchByTitle(String title, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedTitle = title != null ? URLEncoder.encode(title, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + encodedTitle + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                    String bookTitle = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Titre non disponible";
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "Auteur(s) non disponible";
                    String isbn = volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "ISBN non disponible";
                    String kind = volumeInfo.has("kind") ? volumeInfo.get("kind").getAsString() : "Kind non disponible";

                    books.add(new Book(bookTitle, authors, isbn, kind));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
    public List<Book> searchByAuthor(String author, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedAuthor = author != null ? URLEncoder.encode(author, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=inauthor:" + encodedAuthor + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Titre non disponible";
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "Auteur(s) non disponible";
                    String isbn = volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "ISBN non disponible";
                    String kind = volumeInfo.has("kind") ? volumeInfo.get("kind").getAsString() : "Kind non disponible";

                    books.add(new Book(title, authors, isbn, kind));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
    public List<Book> searchByISBN(String isbn, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedISBN = isbn != null ? URLEncoder.encode(isbn, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + encodedISBN + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null) {
                for (JsonElement item : items) {
                    JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Titre non disponible";
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").toString() : "Auteur(s) non disponible";
                    String isbnResult = volumeInfo.has("industryIdentifiers") ? volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "ISBN non disponible";
                    String kind = volumeInfo.has("kind") ? volumeInfo.get("kind").getAsString() : "Kind non disponible";

                    books.add(new Book(title, authors, isbnResult, kind));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }


}
