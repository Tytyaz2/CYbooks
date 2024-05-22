/*package main.models;

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

                    books.add(new Book(title, authors, isbn));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static List<Book> searchByTitle(String title, int startIndex, int pageSize) {
        return searchByField("intitle:" + title, startIndex, pageSize);
    }

    public List<Book> searchByAuthor(String author, int startIndex, int pageSize) {
        return searchByField("inauthor:" + author, startIndex, pageSize);
    }

    public List<Book> searchByISBN(String isbn, int startIndex, int pageSize) {
        return searchByField("isbn:" + isbn, startIndex, pageSize);
    }

    public List<Book> searchByPublisher(String publisher, int startIndex, int pageSize) {
        return searchByField("inpublisher:" + publisher, startIndex, pageSize);
    }

    public List<Book> searchByPublishedDate(String publishedDate, int startIndex, int pageSize) {
        return searchByField("publishedDate:" + publishedDate, startIndex, pageSize);
    }

    public List<Book> searchByDescription(String description, int startIndex, int pageSize) {
        return searchByField("description:" + description, startIndex, pageSize);
    }

    public List<Book> searchByPageCount(int pageCount, int startIndex, int pageSize) {
        return searchByField("pageCount:" + pageCount, startIndex, pageSize);
    }

    public List<Book> searchByCategories(String category, int startIndex, int pageSize) {
        return searchByField("subject:" + category, startIndex, pageSize);
    }

    public List<Book> searchByAverageRating(double averageRating, int startIndex, int pageSize) {
        return searchByField("averageRating:" + averageRating, startIndex, pageSize);
    }

    public List<Book> searchByRatingsCount(int ratingsCount, int startIndex, int pageSize) {
        return searchByField("ratingsCount:" + ratingsCount, startIndex, pageSize);
    }

    public List<Book> searchByLanguage(String language, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedLanguage = language != null ? URLEncoder.encode(language, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=language:" + encodedLanguage + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

                    books.add(new Book(title, authors, isbn));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }


    public static List<Book> searchByField(String field, int startIndex, int pageSize) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedField = field != null ? URLEncoder.encode(field, "UTF-8") : "";
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedField + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
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

                    books.add(new Book(title, authors, isbn));
                }
            } else {
                System.out.println("Aucun résultat trouvé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
}*/
