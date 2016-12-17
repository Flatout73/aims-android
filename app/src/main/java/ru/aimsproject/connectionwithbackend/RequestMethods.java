package ru.aimsproject.connectionwithbackend;

import org.json.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aimsproject.data.DataStorage;
import ru.aimsproject.exceptions.IncompatibleAimsDatesException;
import ru.aimsproject.models.*;

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
     * Парсит объект JSON для получения объекта цели 1-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @return Объект цели 1-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType1(JSONObject jsonObjectAim, User author) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})");
        Date aimDate = null;
        Matcher aimDateMatcher = datePattern.matcher(jsonObjectAim.getString("Date"));
        if(aimDateMatcher.matches()) {
            aimDate = new Date(Integer.parseInt(aimDateMatcher.group(1)), Integer.parseInt(aimDateMatcher.group(2)), Integer.parseInt(aimDateMatcher.group(3)), Integer.parseInt(aimDateMatcher.group(4)), Integer.parseInt(aimDateMatcher.group(5)), Integer.parseInt(aimDateMatcher.group(6)));
        }
        Date startDate = null;
        Matcher startDateMatcher = datePattern.matcher(jsonObjectAim.getString("StartDate"));
        if(startDateMatcher.matches()) {
            startDate = new Date(Integer.parseInt(startDateMatcher.group(1)), Integer.parseInt(startDateMatcher.group(2)), Integer.parseInt(startDateMatcher.group(3)), Integer.parseInt(startDateMatcher.group(4)), Integer.parseInt(startDateMatcher.group(5)), Integer.parseInt(startDateMatcher.group(6)));
        }
        Date endDate = null;
        Matcher endDateMatcher = datePattern.matcher(jsonObjectAim.getString("EndDate"));
        if(endDateMatcher.matches()) {
            endDate = new Date(Integer.parseInt(endDateMatcher.group(1)), Integer.parseInt(endDateMatcher.group(2)), Integer.parseInt(endDateMatcher.group(3)), Integer.parseInt(endDateMatcher.group(4)), Integer.parseInt(endDateMatcher.group(5)), Integer.parseInt(endDateMatcher.group(6)));
        }
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("User");
            author = parseUser(userObject);
        }
        return new AimType1(text, header, 1, flag, modif, author, aimDate, startDate, endDate);
    }

    /**
     * Парсит объект JSON для получения объекта цели 2-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @return Объект цели 2-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType2(JSONObject jsonObjectAim, User author) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})");
        Date aimDate = null;
        Matcher aimDateMatcher = datePattern.matcher(jsonObjectAim.getString("Date"));
        if(aimDateMatcher.matches()) {
            aimDate = new Date(Integer.parseInt(aimDateMatcher.group(1)), Integer.parseInt(aimDateMatcher.group(2)), Integer.parseInt(aimDateMatcher.group(3)), Integer.parseInt(aimDateMatcher.group(4)), Integer.parseInt(aimDateMatcher.group(5)), Integer.parseInt(aimDateMatcher.group(6)));
        }
        Date startDate = null;
        Matcher startDateMatcher = datePattern.matcher(jsonObjectAim.getString("StartDate"));
        if(startDateMatcher.matches()) {
            startDate = new Date(Integer.parseInt(startDateMatcher.group(1)), Integer.parseInt(startDateMatcher.group(2)), Integer.parseInt(startDateMatcher.group(3)), Integer.parseInt(startDateMatcher.group(4)), Integer.parseInt(startDateMatcher.group(5)), Integer.parseInt(startDateMatcher.group(6)));
        }
        Date endDate = null;
        Matcher endDateMatcher = datePattern.matcher(jsonObjectAim.getString("EndDate"));
        if(endDateMatcher.matches()) {
            endDate = new Date(Integer.parseInt(endDateMatcher.group(1)), Integer.parseInt(endDateMatcher.group(2)), Integer.parseInt(endDateMatcher.group(3)), Integer.parseInt(endDateMatcher.group(4)), Integer.parseInt(endDateMatcher.group(5)), Integer.parseInt(endDateMatcher.group(6)));
        }
        Matcher dateSectionMatcher = datePattern.matcher(jsonObjectAim.getString("DateSection"));
        Date dateSection = null;
        if(dateSectionMatcher.matches()) {
            dateSection = new Date(Integer.parseInt(dateSectionMatcher.group(1)), Integer.parseInt(dateSectionMatcher.group(2)), Integer.parseInt(dateSectionMatcher.group(3)), Integer.parseInt(dateSectionMatcher.group(4)), Integer.parseInt(dateSectionMatcher.group(5)), Integer.parseInt(dateSectionMatcher.group(6)));
        }
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("User");
            author = parseUser(userObject);
        }
        return new AimType2(text, header, 2, flag, modif, author, aimDate, startDate, endDate, dateSection);
    }

    /**
     * Парсит объект JSON для получения объекта цели 3-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @return Объект цели 3-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType3(JSONObject jsonObjectAim, User author) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})");
        Date aimDate = null;
        Matcher aimDateMatcher = datePattern.matcher(jsonObjectAim.getString("Date"));
        if(aimDateMatcher.matches()) {
            aimDate = new Date(Integer.parseInt(aimDateMatcher.group(1)), Integer.parseInt(aimDateMatcher.group(2)), Integer.parseInt(aimDateMatcher.group(3)), Integer.parseInt(aimDateMatcher.group(4)), Integer.parseInt(aimDateMatcher.group(5)), Integer.parseInt(aimDateMatcher.group(6)));
        }
        Date startDate = null;
        Matcher startDateMatcher = datePattern.matcher(jsonObjectAim.getString("StartDate"));
        if(startDateMatcher.matches()) {
            startDate = new Date(Integer.parseInt(startDateMatcher.group(1)), Integer.parseInt(startDateMatcher.group(2)), Integer.parseInt(startDateMatcher.group(3)), Integer.parseInt(startDateMatcher.group(4)), Integer.parseInt(startDateMatcher.group(5)), Integer.parseInt(startDateMatcher.group(6)));
        }
        Date endDate = null;
        Matcher endDateMatcher = datePattern.matcher(jsonObjectAim.getString("EndDate"));
        if(endDateMatcher.matches()) {
            endDate = new Date(Integer.parseInt(endDateMatcher.group(1)), Integer.parseInt(endDateMatcher.group(2)), Integer.parseInt(endDateMatcher.group(3)), Integer.parseInt(endDateMatcher.group(4)), Integer.parseInt(endDateMatcher.group(5)), Integer.parseInt(endDateMatcher.group(6)));
        }
        int allTasks = jsonObjectAim.getInt("AllTasks");
        int currentTasks = jsonObjectAim.getInt("CurrentTasks");
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("User");
            author = parseUser(userObject);
        }
        return new AimType3(text, header, 3, flag, modif, author, aimDate, startDate, endDate, allTasks, currentTasks);
    }

    /**
     * Парсит объект JSON для получения объекта пользователя.
     * @param jsonObjectUser Объект JSON для парсинга.
     * @return Объект пользователя.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     */
    private static User parseUser(JSONObject jsonObjectUser) throws JSONException {
        String name = jsonObjectUser.getString("Name");
        String login = jsonObjectUser.getString("Login");
        int sex = jsonObjectUser.getInt("Sex");
        String image = jsonObjectUser.getString("Image");
        return new User(name, login, sex, image);
    }

    /**
     * Объединяет массив тегов в одну строку, где теги разделены запятыми.
     * @param tags Массив тегов цели.
     * @return Возвращает строку, где теги разделены запятыми.
     */
    private static String joinTags(String[] tags) {
        String result = "";
        for(int i = 0; i < tags.length; i++) {
            result += tags;
            if(i < tags.length - 1) {
                result += ",";
            }
        }
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
            DataStorage.setToken(token);
            me.setImage(image);
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
            DataStorage.setToken(token);
            JSONArray users = jsonObject.getJSONArray("Users");
            List<User> resultList = new ArrayList<>();
            for(int i = 0; i < users.length(); i++) {
                resultList.add(parseUser(users.getJSONObject(i)));
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
            DataStorage.setToken(token);
            JSONObject userObject = jsonObject.getJSONObject("User");
            User me = parseUser(userObject);
            JSONArray aimsType1 = jsonObject.getJSONArray("aim1");
            for(int i = 0; i < aimsType1.length(); i++) {
                me.addAim(parseAimType1(aimsType1.getJSONObject(i), me));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                me.addAim(parseAimType2(aimsType2.getJSONObject(i), me));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                me.addAim(parseAimType3(aimsType3.getJSONObject(i), me));
            }
            Collections.sort(me.getAims());
            DataStorage.setMe(me);
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
                    throw new Exception("Такого пользователя не существует.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            JSONObject userObject = jsonObject.getJSONObject("User");
            User user = parseUser(userObject);
            JSONArray aimsType1 = jsonObject.getJSONArray("aim1");
            for(int i = 0; i < aimsType1.length(); i++) {
                user.addAim(parseAimType1(aimsType1.getJSONObject(i), user));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                user.addAim(parseAimType2(aimsType2.getJSONObject(i), user));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                user.addAim(parseAimType3(aimsType3.getJSONObject(i), user));
            }
            Collections.sort(user.getAims());
            return user;
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Получает цели друзей, начиная с определённого времени.
     * @param date Время, начиная с которого получаются цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось или если у пользователя нет друзей.
     */
    public static void getNews(Date date) throws Exception {
        String urlString = newsURL;
        urlString += "getuserprofile/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "date", date.toString(), false);
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
                if(token.equals("Friends Error")) {
                    throw new Exception("У Вас нет друзей.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            DataStorage.getNewsFeed().clear();
            JSONArray aimsType1 = jsonObject.getJSONArray("aim1");
            for(int i = 0; i < aimsType1.length(); i++) {
                DataStorage.getNewsFeed().add(parseAimType1(aimsType1.getJSONObject(i), null));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                DataStorage.getNewsFeed().add(parseAimType2(aimsType2.getJSONObject(i), null));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                DataStorage.getNewsFeed().add(parseAimType3(aimsType3.getJSONObject(i), null));
            }
            Collections.sort(DataStorage.getNewsFeed());
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Добавляет цель 1-го типа.
     * @param header Название цели.
     * @param text Текст цели.
     * @param mode Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param endDate Дата окончания выполнения цели.
     * @param startDate Дата начала выполнения цели.
     //* @param tags Массив тегов цели.
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType1(String header, String text, int mode, Date endDate, Date startDate, /* String[] tags */ String tags) throws Exception {
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
        //urlString = addAttribute(urlString, "tags", joinTags(tags), false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType1(text, header, 1, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate);
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                DataStorage.getMe().removeAim(newAim);
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
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
     * Добавляет цель 2-го типа.
     * @param header Название цели.
     * @param text Текст цели.
     * @param mode Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param endDate Дата окончания выполнения цели.
     * @param startDate Дата начала выполнения цели.
     * @param dateSection Промежуток времени, не позднее, чем через который, необходимо подтверждать цель.
    //* @param tags Массив тегов цели.
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType2(String header, String text, int mode, Date endDate, Date startDate, Date dateSection, /* String[] tags */ String tags) throws Exception {
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
        urlString = addAttribute(urlString, "dateSection", dateSection.toString(), false);
        //urlString = addAttribute(urlString, "tags", joinTags(tags), false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType2(text, header, 2, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate, dateSection);
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                DataStorage.getMe().removeAim(newAim);
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
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
     * Добавляет цель 1-го типа.
     * @param header Название цели.
     * @param text Текст цели.
     * @param mode Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param endDate Дата окончания выполнения цели.
     * @param startDate Дата начала выполнения цели.
     * @param AllTasks Общее количество однотипных задач, которые необходимо решить для выполнения цели.
    //* @param tags Массив тегов цели.
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType3(String header, String text, int mode, Date endDate, Date startDate, int AllTasks, /* String[] tags */ String tags) throws Exception {
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
        urlString = addAttribute(urlString, "AllTasks", "" + AllTasks, false);
        //urlString = addAttribute(urlString, "tags", joinTags(tags), false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType3(text, header, 3, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate, AllTasks, 0);
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            String token = jsonObject.getString("Token");
            if(!jsonObject.getBoolean("OperationOutput")) {
                DataStorage.getMe().removeAim(newAim);
                if(token.equals("DataBase Error")) {
                    throw new Exception("Ошибка при подключении к базе данных.");
                }
                if(token.equals("Token Error")) {
                    throw new Exception("Неправильный токен.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    public static void skipAim(Aim aim) throws Exception {
        String urlString = aimsURL;
        urlString += "skip/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "date", aim.getDate().toString(), false);
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
            DataStorage.setToken(token);
            aim.setFlag(3);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    public static void commentAim(Aim aim, String comment) throws Exception {
        String urlString = aimsURL;
        urlString += "comment/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "date", aim.getDate().toString(), false);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "comment", comment, false);
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
                    throw new Exception("Такого пользователя не существует.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            aim.addComment(comment);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    public static void likeAim(Aim aim) throws Exception {
        String urlString = likesURL;
        urlString += "likes/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "date", aim.getDate().toString(), false);
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
                    throw new Exception("Такого пользователя не существует.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            // Coming soon...
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }
}
