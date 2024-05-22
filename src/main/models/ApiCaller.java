package main.models;

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
 * ApiCaller is the class that access BNF's API
 *
 * @author TDLT
 */
public class ApiCaller {

    /**
     * @param query this is the submitted query
     * @param start from which book do we want to start
     * @param number the number of book we want
     * @return a list of books the API returned
     */
    public static List<Book> call(String categorie,String query, int start, int number) {
// is appended to the query a link to the API, parameters to get only books,
// options to get the API's response in the expected format
        System.out.println(start +" "+ number );
        String myURL = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=bib.doctype any \"a\" and bib."+ categorie +" any \""+ query +"\"&recordSchema=dublincore&maximumRecords="+number+"&startRecord="+start;
        URI juri = null;

// this is to encode special characters in a usable format eg. " " gives " "
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
        HttpRequest request = HttpRequest.newBuilder().uri(juri).method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

// getting the response
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
// writing the response in a file and then parsing the file to get usable
// instances of books
        OperaFile.createFile("answer.xml");
        OperaFile.writeFile("answer.xml", response.body());
        List<Book> books = Metamorph.createBook("answer.xml");

        return books;
    }
}
