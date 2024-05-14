import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Console;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Nu s-au găsit date despre locație.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FMoscow";

        try {
            HttpURLConnection connection = fetchApiResponse(urlString);
            if (connection == null || connection.getResponseCode() != 200) {
                System.out.println("Eroare la conectare API ");
                return null;
            }

            StringBuilder responseJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                responseJson.append(scanner.nextLine());
                System.out.println(responseJson);
            }
            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(responseJson));
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            if (hourly == null) {
                System.out.println("Nu s-au găsit date meteo.");
                return null;
            }
            //print hourly
            System.out.println(hourly);

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurentHour(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");

            if (temperatureData == null || weatherCode == null || relativeHumidity == null || windSpeedData == null) {
                System.out.println("Datele meteo sunt incomplete.");
                return null;
            }

            double temperature = (double) temperatureData.get(index);
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));
            long humidity = (long) relativeHumidity.get(index);
            double windspeed = (double) windSpeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    public static JSONArray getLocationData(String locationName)
    {
        locationName = locationName.replaceAll(" ",  "+");
        // build the URL
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        // get the data
        try
        {
            HttpURLConnection connection = fetchApiResponse(urlString);
            if (connection.getResponseCode() != 200)
            {
                System.out.println("Eroare la conectare: " + connection.getResponseCode());
                return null;
            } else {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine())
                {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                //parse the data
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(response));
                JSONArray locationData =(JSONArray) resultsJsonObj.get("results");
                return locationData;

            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private static int findIndexOfCurentHour(JSONArray timeList)
    {
        String currentTime = getCurrentTime();
        for(int i = 0; i< timeList.size(); i++)
        {
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime))
            {
                return i;
            }
        }
        return 0;

    }

    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println(formattedDateTime);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode)
    {
        String weatherCondition = "";
        if(weatherCode == 0L)
        {
            //Senin
            weatherCondition = "Senin";
        }
        else if(weatherCode > 0L && weatherCode <= 3L)
        {
            weatherCondition = "Cer innorat";
        }
        else if((weatherCode >= 51L && weatherCode <= 67L) ||
                (weatherCode >= 80L && weatherCode <= 99L))
        {
            weatherCondition = "Ploaie";
        }else if (weatherCode >= 71L && weatherCode <= 77L)
        {
            weatherCondition = "Zapada";
        }
        return weatherCondition;
    }














}
