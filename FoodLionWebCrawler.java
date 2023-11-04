import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches FoodLion for information
 */
public class FoodLionWebCrawler {
    public static List<StoreInfo> infoList = new ArrayList<>();
    public static void main(String[] args) {
        String baseUrl = "https://stores.foodlion.com/index.html";
        crawler(baseUrl, new ArrayList<String>());
        StoreInfo.writeFile(infoList);
    }

    /**
     * Iterates through websites to find data
     * @param url to search
     * @param visited urls already searched
     */
    public static void crawler(String url, ArrayList<String> visited) {
        Document doc = request(url);
        isEmpty(url);
        try {
            doc.hasSameValue("a.Directory-listLink");
            Elements listItems = doc.select("a.Directory-listLink");
            for (Element link : listItems) {
                String nextLink = link.absUrl("href");
                if (visited.contains(nextLink) == false) {
                    isEmpty(nextLink);
                    visited.add(nextLink);
                    crawler(nextLink, visited);
                }
            }
        } catch (Exception e) {
            System.out.println("");
        }
        try {
            Elements teaser = doc.select("a.Teaser-titleLink");
            for (Element link : teaser) {
                String nextLink = link.absUrl("href");
                if (visited.contains(nextLink) == false) {
                    visited.add(nextLink);
                    crawler(nextLink, visited);
                }
            }
        } catch (Exception e) {
            System.out.println("");
        }
        try {
            crawlDestination(url);
        } catch (Exception e) {
            System.out.println("Loading");
        }
    }

    /**
     * Tests url and returns doc
     * @param url to be tested
     * @return document to be used
     */
    public static Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            
            if (con.response().statusCode() == 200) {
                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Find information on webpages
     * @param url to find name, address, and phone on
     */
    private static void crawlDestination(String url) {
        try {
            Document document = Jsoup.connect(url).get();

            String title = document.select("span.LocationName--hero").text();
            String address = document.selectFirst("span.Address-field.Address-line1").text();
            String city = document.select("span.Address-field.Address-city").text();
            String state = document.select("abbr.Address-field.Address-region").text();
            String zip = document.select("span.Address-field.Address-postalCode").text();
            String phoneNumber = document.select("a.Link.Phone-link").text();
            String latitude = document.select("meta[itemprop=latitude]").attr("content");
            String longitude = document.select("meta[itemprop=longitude]").attr("content");
            String urlSave = url;

            StoreInfo store = new StoreInfo(title, address, city, state, zip, phoneNumber, latitude, longitude, urlSave);
            infoList.add(store);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if passed url is empty
     * @param url to see if it is empty
     */
    public static void isEmpty(String url) {
        if (url == null || url.length() == 0) {
            System.exit(0);
        }
    }

    /**
     * Represents each store as an Object
     */
    private static class StoreInfo {
        private final String title;
        private final String address;
        private final String city;
        private final String state;
        private final String zip;
        private final String phoneNumber;
        private final String latitude;
        private final String longitude;
        private final String urlSaved;
        private final String country = "USA";

        /**
         * Creates a new object of a location
         * @param title of location
         * @param address of location
         * @param phoneNumber of location
         */
        public StoreInfo(String title, String address, String city, String state, String zip, String phoneNumber, String latitude, String longitude, String urlSaved) {
            this.title = title;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.phoneNumber = phoneNumber;
            this.latitude = latitude;
            this.longitude = longitude;
            this.urlSaved = urlSaved;
        }

        /**
         * Getter for title
         * @return title of location
         */
        public String getTitle() {
            return title;
        }

        /**
         * Getter for address
         * @return address of location
         */
        public String getAddress() {
            return address;
        }

        /**
         * Getter for city
         * @return city of location
         */
        public String getCity() {
            return city;
        }

        /**
         * Getter for state
         * @return state of location
         */
        public String getState() {
            return state;
        }

        /**
         * Getter for zip code
         * @return zip code of location
         */
        public String getZip() {
            return zip;
        }

        /**
         * Getter for phone number
         * @return phone number for location
         */
        public String getPhoneNumber() {
            return phoneNumber;
        }

        /**
         * Getter for Latitude
         * @return latitude for location
         */
        public String getLatitude() {
            return latitude;
        }

        /**
         * Getter for longtitude
         * @return longitude of location
         */
        public String getLongitude() {
            return longitude;
        }

        /**
         * Getter for country
         * @return country of location
         */
        public String getCountry() {
            return country;
        }

        /**
         * Getter for url of location
         * @return url for each location
         */
        public String getUrlSaved() {
            return urlSaved;
        }

        /**
         * String of location to be entered into file
         * @return String representation of each location
         */
        public String toString() {
            return getTitle() + "," + getAddress() + "," + getCity() + "," + getState() + "," + getZip() + "," + getPhoneNumber() + "," + getLatitude() + "," + getLongitude()
                + "," + getCountry() + "," + getUrlSaved();
        }

        /**
         * Stores the store objects in a file
         * @param locations stored in a List
         */
        public static void writeFile(List<StoreInfo> locations) {
            try (BufferedWriter writer = new BufferedWriter
                (new FileWriter("FoodLionLocations.csv"))) {
                    for (StoreInfo element : locations) {
                        writer.write(element.toString());
                        writer.newLine();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}