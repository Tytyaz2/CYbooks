package main.API;

import main.models.Book;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URL;

/**
 * SearchBookAPI is the class that access BNF's API
 */
public class SearchBookAPI {

    /**
     * @param query this is the submitted query
     * @param start from which index do we want to start ( begins with 1 )
     * @param number the number of book we want
     * @return a list of books the API returned
     */
    public static List<Book> search(String categorie, String query, int start, int number) {
        String myURL = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=bib.doctype any \"a\" and bib."+ categorie +" all \""+ query +"\"&recordSchema=dublincore&maximumRecords="+number+"&startRecord="+start;
        URI juri = null;
        try {
            URL url = new URL(myURL);
            String nullFragment = null;
            juri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
        } catch (MalformedURLException e) {
            System.out.println("URL " + myURL + " is a malformed URL");
        } catch (URISyntaxException e) {
            System.out.println("URI " + myURL + " is a malformed URL");
        }

        // building the request
        HttpRequest request = HttpRequest.newBuilder().uri(juri).method("GET", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = null;

        // getting the response
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // writing the response in a file and then parsing the file to get usable
        OperationOnXMLFIle.createFile("src/main/resources/answer.xml");
        OperationOnXMLFIle.writeFile("src/main/resources/answer.xml", response.body());
        List<Book> books = Metamorph.createBook("src/main/resources/answer.xml");

        return books;
    }
}
