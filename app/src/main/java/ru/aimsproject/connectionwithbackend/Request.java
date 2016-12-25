package ru.aimsproject.connectionwithbackend;

import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Антон on 27.10.2016.
 * Представляет запрос к серверу.
 */
class Request {
    /**
     * Содержит URL-адрес нашего сервера.
     */
    private static final String domain = "http://backrestapi025036.azurewebsites.net/api/";

    /**
     * Выполняет GET-запрос.
     * @param urlString URL-адрес для запроса.
     * @return Возвращает строку, полученную от сервера. Если запрос некорректен, то null.
     */
    static String doRequest(String urlString, String imageBase64String, String token) throws Exception {
        urlString = domain + urlString;
        String response = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            if(imageBase64String != null) {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("token", token);
                httpURLConnection.setRequestProperty("image", imageBase64String);
            }
            else {
                httpURLConnection.setRequestMethod("GET");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            response = "";
            String nextLine;
            while((nextLine = bufferedReader.readLine()) != null) {
                response += nextLine;
            }
            bufferedReader.close();
        }
        catch (Exception ex) {
            throw new Exception("Не удалось создать соединение с сервером. Возможно, нет доступа к сети.");
        }
        if(response == null) {
            throw new Exception("Не удалось создать соединение с сервером. Возможно, нет доступа к сети.");
        }
        return response;
    }
}
