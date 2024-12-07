import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.CosNaming.NamingContextExtPackage.URLStringHelper;

import javax.net.ssl.HttpsURLConnection;
import javax.print.DocFlavor;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(System.in);
            String city;
            do{
                System.out.println("===========================================");
                System.out.println("Enter City (Say No to Quit)");
                city = scanner.nextLine();

                if(city.equalsIgnoreCase("No")) break;

                // Getting Location data
                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(latitude, longitude);
            }while(!city.equalsIgnoreCase("No"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void displayWeatherData(double latitude, double longitude) {
        try{
            // 1. Fetch the API response based on the API Link
            String url = "https://api.open-meteo.com/v1/forecast?latitude="+ latitude +
                    "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m&hourly=temperature_2m";

            HttpURLConnection apiConnection = fetchApiResponse(url);

            //Check for response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }

            // 2. Read the response and convert store string type
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            // 4. Store the data into their corresponding data type
            String time = (String) currentWeatherJson.get("time");
            System.out.println("Current Time: " + time);

            double temperature = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature (C): " + temperature);

            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relative Humidity: " + relativeHumidity);

            double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
            System.out.println("Wind Speed: " + windSpeed);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static JSONObject getLocationData(String city) {
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                city + "&count=1&language=en&format=json";

        try{
            //Fetching a response from the API
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            //Checking the status of the API response
            // 200 means that it was a success

            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //2. Read the response from the API and turn it into a JSON object
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            // 4. Retrieve teh Location data
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.get(0);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {
        try{
            // Create a string builder to store the resulting JSON Data
            StringBuilder resultJson = new StringBuilder();

            // Create a scanner to read from the input Stream of the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            //Loop through each line in the response and append it to the stringBuilder
            while (scanner.hasNext()){
                //Read and append the current line to the stringbuilder
                resultJson.append(scanner.nextLine());
            }

            // Close the Scanner to release resources associated with it
            scanner.close();

            //Return the JSON data as a string
            return resultJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try{
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Set request method to get
            conn.setRequestMethod("GET");

            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}