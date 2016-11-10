package ru.aimsproject.connectionwithbackend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Антон on 27.10.2016.
 * Представляет запрос к серверу.
 */
public class Request {
    /**
     * Содержит URL-адрес нашего сервера.
     */
    final private String domain = "http://backrestapi025036.azurewebsites.net/api/";

    /**
     * Выполняет GET-запрос.
     * @param urlString URL-адрес для запроса.
     * @return Возвращает строку, полученную от сервера. Если запрос некорректен, то null.
     */
    String doRequest(String urlString) {
        String response = null;
        if(urlString.contains(domain)) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                response = "";
                String nextLine;
                while((nextLine = bufferedReader.readLine()) != null) {
                    response += nextLine;
                }
                bufferedReader.close();
            }
            catch (Exception ex) {
                response = null;
            }
        }
        return response;
    }
}
