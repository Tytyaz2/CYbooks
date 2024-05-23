package main.models;

import main.models.Book;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Metamorph {

    public static List<Book> createBook(String file) {
        List<Book> books = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(file));

            NodeList recordList = doc.getElementsByTagName("oai_dc:dc");
            for (int i = 0; i < recordList.getLength(); i++) {
                Element record = (Element) recordList.item(i);
                String title = getElementValue(record, "dc:title");
                String author = getElementValue(record, "dc:creator");
                String isbn = getElementValue(record, "dc:identifier",1);
                if (isbn.length() > 5) {
                    isbn = isbn.substring(5);
                }
                else if (isbn.length() < 5) {
                    isbn = getElementValue(record, "dc:identifier");
                    isbn = isbn.substring(35);
                }
                Book book = new Book(title, author, isbn);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    private static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    private static String getElementValue(Element element, String tagName, int index) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > index) {
            return nodeList.item(index).getTextContent();
        }
        return "";
    }
}
