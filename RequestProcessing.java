import com.google.gson.*;
import java.io.*;
import java.net.*;

public class RequestProcessing {

    private int countPhotos;

    private int getСountPhotos() {
        return countPhotos;
    }

    private void setСountPhotos(int c) {
        countPhotos = c;
    }

    // получаем  статус HTTP запроса
    public int getStatusCode(URLConnection connection) throws IOException {
        return ((HttpURLConnection) connection).getResponseCode();
    }

    // считаем кол-во "изображений" в HTTP ответе
    private int countPhotos(JsonObject jsonObject) {
        int count = 0;
        if (jsonObject.has("result")) {
            JsonObject resultObject = jsonObject.getAsJsonObject("result");
            if (resultObject.has("photos")) {
                JsonArray photosArray = resultObject.getAsJsonArray("photos");
                count = photosArray.size();
            }
        }
        return count;
    }

    void getNearestPhotos(URLConnection connection, WindowInfo window) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            String jsonResponse = response.toString();

            JsonObject convertedObject = new Gson().fromJson(jsonResponse, JsonObject.class);

            setСountPhotos(countPhotos(convertedObject));

            Gson gson = new Gson();

            JsonObject resultObject = convertedObject.getAsJsonObject("result");

            if (resultObject != null) {

            JsonArray photosArray = resultObject.getAsJsonArray("photos");

            if (photosArray != null && !photosArray.isEmpty()) {

                Place[] places;
                InfoAboutPlace[] iaplace = new InfoAboutPlace[30];

                places = gson.fromJson(photosArray, Place[].class);
                giveForPage(places, iaplace);

                window.getPlaces(places);
                window.getInfoPlaces(iaplace);

                window.setPlacesIsEmpty(false);

            } else {

                window.setPlacesIsEmpty(true);

            }
        } else window.setPlacesIsEmpty(true);

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    void giveForPage(Place[] places, InfoAboutPlace[] iaplace) throws IOException { // достаём остальную информацию "места" с помощью cid

        for (int i = 0; i < getСountPhotos(); i++) {

            String cid = places[i].getCid();
            String url = "https://pastvu.com/api2?method=photo.giveForPage&params={\"cid\": " + cid + "}";

            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");

            if (getStatusCode(connection) == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }

                String jsonResponse = response.toString();

                JsonObject convertedObject = new Gson().fromJson(jsonResponse, JsonObject.class);
                Gson gson = new Gson();
                JsonObject resultObject = convertedObject.getAsJsonObject("result");
                JsonObject photoObject = resultObject.getAsJsonObject("photo");
                iaplace[i] = gson.fromJson(photoObject, InfoAboutPlace.class);
            } else {
                System.out.println("Не удалось установить соединение :( ");
            }

        }
    }

}