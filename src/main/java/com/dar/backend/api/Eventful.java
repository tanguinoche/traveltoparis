package com.dar.backend.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Category;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.network.ServerCommunication;
import com.evdb.javaapi.operations.EventOperations;

public class Eventful {
    // Application Keys
    private static String key = "cH942g8gwHwmjpVh";


    public static List<Event> getEvents(String begin, String end, ArrayList<Category> c) {
        APIConfiguration.setEvdbUser("TravelToParis");
        APIConfiguration.setEvdbPassword("none");
        APIConfiguration.setApiKey(key);

        EventSearchRequest esr = new EventSearchRequest();

        if(!c.isEmpty()) {
            StringBuilder categories = new StringBuilder();
            for(int i = 0; i < c.size()-1 ; i++) {
                categories.append(c.get(i).getId()).append(',');
            }
            categories.append(c.get(c.size()-1));
            esr.setCategory(categories.toString());
        }

        esr.setLocation("Paris");
        esr.setDateRange(begin+"00-"+end+"00");
        esr.setPageSize(50);
        return  getEvents(esr);
    }

    public static List<Event> getEvents(String begin, String end) {
        APIConfiguration.setEvdbUser("TravelToParis");
        APIConfiguration.setEvdbPassword("none");
        APIConfiguration.setApiKey(key);


        EventSearchRequest esr = new EventSearchRequest();

        esr.setLocation("Paris");
        esr.setDateRange(begin+"00-"+end+"00");
        esr.setPageSize(50);
        return  getEvents(esr);
    }

    //Begin and end should be YYYYMMDD
    public static List<Event> getEvents(EventSearchRequest esr) {
        List<Event> events = new ArrayList<>();
        SearchResult sr = null;

        EventOperations eo = new EventOperations();
        int pageNumber = 0;
        int nbPages = -1;
        do {
            try {
                esr.setPageNumber(++pageNumber);
                sr = eo.search(esr);
                nbPages = sr.getPageCount();
                events.addAll(sr.getEvents());
                System.out.println("DEBUG : Added "+sr.getEvents().size()+" elements from page "+pageNumber+'/'+nbPages);
            } catch (EVDBRuntimeException | EVDBAPIException e) {
                System.err.println("ERROR Eventful : "+e.getMessage());
            }
        } while(pageNumber <= nbPages);
        return events;
    }


    public static void printXMLCategories() {
        APIConfiguration.setEvdbUser("TravelToParis");
        APIConfiguration.setEvdbPassword("none");
        APIConfiguration.setApiKey(key);

        InputStream is;
        try {
            HashMap<String,String> params = new HashMap<>();
            params.put("subcategories", "1");
            is = new ServerCommunication().invokeMethod("categories/list", params);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
            System.out.println(result.toString());
        } catch (EVDBRuntimeException | EVDBAPIException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        APIConfiguration.setEvdbUser("TravelToParis");
        APIConfiguration.setEvdbPassword("none");
        APIConfiguration.setApiKey(key);

        InputStream is;
        try {
            HashMap<String,String> params = new HashMap<>();
            params.put("subcategories", "1");
            is = new ServerCommunication().invokeMethod("categories/list", params);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document= builder.parse(is);
            final Element root = document.getDocumentElement();
            final NodeList nodes = root.getChildNodes();
            final int nbNodes = nodes.getLength();
            for (int i = 0; i<nbNodes; i++)
                if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element category = (Element) nodes.item(i);
                    Category c = new Category();
                    c.setId(category.getElementsByTagName("id").item(0).getTextContent());
                    c.setName(category.getElementsByTagName("name").item(0).getTextContent());
                    categories.add(c);
                }
        } catch (EVDBRuntimeException | EVDBAPIException | IOException | ParserConfigurationException | SAXException e1) {
            e1.printStackTrace();
        }

        return categories;
    }


    public static void main(String[] args) {
        //getEvents("20161101", "20161131");
        //List<Category> categories = getCategories();
        //for(Category c : categories) System.out.println(c.getId() + " : "  + c.getName());
        printXMLCategories();
    }
}