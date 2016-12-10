package ru.aimsproject.connectionwithbackend;

import org.json.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.lang.String;

import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * Created by Антон on 17.11.2016.
 * Представляет статический методы для запросов к серверу.
 */
public class RequestMethods {
    /**
     * Элемент HTTP-запроса для методов из группы aims.
     */
    private static final String aimsURL = "aims/";

    /**
     * Элемент HTTP-запроса для методов из группы friendships.
     */
    private static final String friendshipsURL = "friendships/";

    /**
     * Элемент HTTP-запроса для методов из группы likes.
     */
    private static final String likesURL = "likes/";

    /**
     * Элемент HTTP-запроса для методов из группы news.
     */
    private static final String newsURL = "news/";

    /**
     * Элемент HTTP-запроса для методов из группы proofs.
     */
    private static final String proofsURL = "proofs/";

    /**
     * Элемент HTTP-запроса для методов из группы user.
     */
    private static final String userURL = "user/";

    /**
     * Добавляет атрибут (имя атрибута и его значение) к HTTP-запросу.
     * @param urlString URL-адрес для запроса.
     * @param attributeName Имя атрибута.
     * @param attributeValue Значение атрибута.
     * @param isFirstAttribute true, если добавляемый атрибут является первым из атрибутов запроса, false в противном случае.
     * @return Обновлённый URL-адрес для запроса (с добавленным атрибутом).
     */
    private static String addAttribute(String urlString, String attributeName, String attributeValue, boolean isFirstAttribute) {
        return urlString + (isFirstAttribute ? "?" : "&") + attributeName + "=" + attributeValue;
    }

    /**
     * Возвращает MD5-хэш от входной строки.
     * @param s Входная строка.
     * @return MD5-хэш от входной строки.
     */
    private static String getMD5Hash(String s) {
        byte[] bytes = null;
        try {
            bytes = s.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex) { }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex) { }
        byte[] hashBytes = messageDigest.digest(bytes);
        BigInteger bigInteger = new BigInteger(1, hashBytes);
        String result = bigInteger.toString(16);
        return result;
    }

    /**
     * Метод для входа пользователя в аккаунт.
     * @param userLogin Логин пользователя.
     * @param userPassword Пароль пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void login(String userLogin, String userPassword) throws Exception {
        String hashPsd = getMD5Hash(userPassword);
        String urlString = userURL;
        urlString += "login/";
        urlString = addAttribute(urlString, "login", userLogin, true);
        urlString = addAttribute(urlString, "hashpsd", hashPsd, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("User Error")) {
                    throw new Exception("Такого пользователя не существует.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            getProfile();
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для регистрации нового пользователя.
     * @param userLogin Логин пользователя.
     * @param userPassword Пароль пользователя.
     * @param email Адрес E-mail пользователя.
     * @param name Имя пользователя.
     * @param sex Пол пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void register(String userLogin, String userPassword, String email, String name, String sex) throws Exception {
        String hashPsd = getMD5Hash(userPassword);
        String urlString = userURL;
        urlString += "register/";
        urlString = addAttribute(urlString, "login", userLogin, true);
        urlString = addAttribute(urlString, "hashpsd", hashPsd, false);
        urlString = addAttribute(urlString, "email", email, false);
        urlString = addAttribute(urlString, "name", name, false);
        urlString = addAttribute(urlString, "sex", sex, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Login Error")) {
                    throw new Exception("Пользователь с таким логином существует.");
                }
                if(token.equals("Email error")) {
                    throw new Exception("Некорректный или несуществующий адрес E-mail.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            getProfile();
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для восстановления пароля пользователя.
     * @param email Адрес E-mail пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void resetPsd(String email) throws Exception {
        String urlString = userURL;
        urlString += "resetpsd/";
        urlString = addAttribute(urlString, "email", email, true);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("User Error")) {
                    throw new Exception("Такого пользователя не существует.");
                }
                if(token.equals("Email error")) {
                    throw new Exception("Некорректный или несуществующий адрес E-mail.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для изменения аватарки пользователя.
     * @param image Новая аватарка пользователя (изображение, сохранённое в виде строки).
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void changeImage(String image) throws Exception {
        String urlString = userURL;
        urlString += "changeimage/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token.");
        }
        User me = DataStorage.getMe();
        if(me == null) {
            throw new Exception("Ошибка: на устройстве не обнаружен вошедший в систему пользователь.");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "image", image, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            me.setImage(image);
            DataStorage.setToken(token);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для изменения пароля пользователя.
     * @param userPassword Новый пароль пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void changePsd(String userPassword) throws Exception {
        String hashPsd = getMD5Hash(userPassword);
        String urlString = userURL;
        urlString += "changepsd/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token.");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "hashpsd", hashPsd, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                if(token.equals("Email error")) {
                    throw new Exception("Некорректный или несуществующий адрес E-mail.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для изменения адреса E-mail пользователя.
     * @param email Новый адрес E-mail пользователя.
     * @param lastEmail Старый адрес E-mail пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void changeMail(String email, String lastEmail) throws Exception {
        String urlString = newsURL;
        urlString += "changemail/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "email", email, false);
        urlString = addAttribute(urlString, "lastemail", lastEmail, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                if(token.equals("Email error")) {
                    throw new Exception("Некорректный или несуществующий адрес E-mail.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для поиска пользователя по логину.
     * @param userLogin Логин искомого пользователя.
     * @return Возвращает найденного пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось или если таких пользователей не найдено.
     */
    public static List<User> search(String userLogin) throws Exception {
        String urlString = newsURL;
        urlString += "search/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", userLogin, false);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                if(token.equals("User Error")) {
                    throw new Exception("Не существует пользователя с таким или похожим логином."); // Подумать, не лучше ли возвращать просто пустой список.
                }
                throw new Exception("Неизвестная ошибка.");
            }
            JSONArray users = jsonObject.getJSONArray("Users");
            List<User> resultList = new ArrayList<>();
            for(int i = 0; i < users.length(); i++) {
                String name = users.getJSONObject(i).getString("Name");
                String login = users.getJSONObject(i).getString("Login");
                int sex = users.getJSONObject(i).getInt("Sex");
                String image = users.getJSONObject(i).getString("Image");
                resultList.add(new User(name, login, null, sex, image));
            }
            return resultList;
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для получения своего профиля.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void getProfile() throws Exception {
        String urlString = newsURL;
        urlString += "getprofile/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            JSONObject userObject = jsonObject.getJSONObject("User");
            String name = userObject.getString("Name");
            String login = userObject.getString("Login");
            String email = userObject.getString("Email"); // Напомнить Андрею, чтобы исправил это у себя.
            int sex = userObject.getInt("Sex");
            String image = userObject.getString("Image");
            DataStorage.setMe(new User(name, login, email, sex, image));
            DataStorage.setToken(token);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для получения профиля пользователя по логину.
     * @param userLogin Логин искомого пользователя.
     * @return Профиль найденного пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось или если таких пользователей не найдено.
     */
    public static User getUserProfile(String userLogin) throws Exception {
        String urlString = newsURL;
        urlString += "getuserprofile/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                if(token.equals("User Error")) {
                    throw new Exception("Такого пользователя не существует."); // Подумать, не лучше ли возвращать просто пустой список.
                }
                throw new Exception("Неизвестная ошибка.");
            }
            JSONObject userObject = jsonObject.getJSONObject("User");
            String name = userObject.getString("Name");
            String login = userObject.getString("Login");
            int sex = userObject.getInt("Sex");
            String image = userObject.getString("Image");
            return new User(name, login, null, sex, image);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    public static void addAimType1(String header, String text, int mode, Date endDate, Date startDate, String[] tags) throws Exception {
        String urlString = aimsURL;
        urlString += "type1/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "text", text, false);
        urlString = addAttribute(urlString, "mode", "" + mode, false);
        urlString = addAttribute(urlString, "endDate", endDate.toString(), false);
        urlString = addAttribute(urlString, "startDate", startDate.toString(), false);
        //urlString = addAttribute(urlString, "tags", String.)
        //String someString = join()

    }
}
