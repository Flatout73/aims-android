package ru.aimsproject.connectionwithbackend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.*;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.lang.String;
import java.util.TimeZone;
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
     * Разница в миллисекундах между временем текущего часового пояса и временем UTC.
     */
    private static final long timeZoneOffsetMilliseconds = TimeZone.getDefault().getRawOffset();

    /**
     * Добавляет атрибут (имя атрибута и его значение) к HTTP-запросу.
     * @param urlString URL-адрес для запроса.
     * @param attributeName Имя атрибута.
     * @param attributeValue Значение атрибута.
     * @param isFirstAttribute true, если добавляемый атрибут является первым из атрибутов запроса, false в противном случае.
     * @return Обновлённый URL-адрес для запроса (с добавленным атрибутом).
     */
    private static String addAttribute(String urlString, String attributeName, String attributeValue, boolean isFirstAttribute) {
        String value = attributeValue.substring(0);
        value = value.replaceAll("&", "%26");
        value = value.replaceAll(" ", "%20");
        return urlString + (isFirstAttribute ? "?" : "&") + attributeName + "=" + value;
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
     * Проверяет символы логина.
     * @param login Строка логина.
     * @return true, если login содержит только допустимые символы, false в ином случае.
     */
    private static boolean checkLogin(String login) {
        for(int i = 0; i < login.length(); i++) {
            if(!((login.charAt(i) >= 'A' && login.charAt(i) <= 'Z') || (login.charAt(i) >= 'a' && login.charAt(i) <= 'z') || (login.charAt(i) >= '0' && login.charAt(i) <= '9') || login.charAt(i) == '_')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает строку даты в формате, необходимом для передачи на сервер.
     * @param date Дата для обработки.
     * @return Строка даты в формате, необходимом для передачи на сервер.
     */
    private static String getCSharpDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = date.getHours();
        int minute = date.getMinutes();
        int sec = date.getSeconds();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "T" + (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute + ":" + (sec < 10 ? "0" : "") + sec;
    }

    /**
     * Парсит дату формата сервера.
     * @param csharpDate Дата для парсинга.
     * @return Объект даты.
     */
    private static Date parseCSharpDate(String csharpDate) {
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})((\\.\\d*)?)");
        Date date = null;
        Matcher dateMatcher = datePattern.matcher(csharpDate);
        if(dateMatcher.matches()) {
            date = getCurrentTimeZoneDateFromUTC(new Date(Integer.parseInt(dateMatcher.group(1)) - 1900, Integer.parseInt(dateMatcher.group(2)) - 1, Integer.parseInt(dateMatcher.group(3)), Integer.parseInt(dateMatcher.group(4)), Integer.parseInt(dateMatcher.group(5)), Integer.parseInt(dateMatcher.group(6))));
        }
        return date;
    }

    /**
     * Парсит объект JSON для получения объекта цели 1-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @param needLikes Показывает, нужно ли парсить лайки и дислайки.
     * @param comments Список комментариев к цели.
     * @param proofs Список подтверждений выполнения цели.
     * @return Объект цели 1-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType1(JSONObject jsonObjectAim, User author, boolean needLikes, List<Comment> comments, List<Proof> proofs) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Date aimDate = parseCSharpDate(jsonObjectAim.getString("Date"));
        Date startDate = parseCSharpDate(jsonObjectAim.getString("StartDate"));
        Date endDate = parseCSharpDate(jsonObjectAim.getString("EndDate"));
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("Us");
            author = parseUser(userObject, false, false, 0);
        }
        int likes = 0;
        int dislikes = 0;
        int liked = 0;
        if(needLikes) {
            likes = jsonObjectAim.getInt("Likes");
            dislikes = jsonObjectAim.getInt("DisLikes");
            liked = jsonObjectAim.getInt("Liked");
        }
        return new AimType1(new ArrayList<Aim>(), text, header, 1, flag, modif, author, aimDate, startDate, endDate, likes, dislikes, liked, comments, proofs);
    }

    /**
     * Парсит объект JSON для получения объекта цели 2-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @param needLikes Показывает, нужно ли парсить лайки и дислайки.
     * @param comments Список комментариев к цели.
     * @param proofs Список подтверждений выполнения цели.
     * @return Объект цели 2-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType2(JSONObject jsonObjectAim, User author, boolean needLikes, List<Comment> comments, List<Proof> proofs) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Date aimDate = parseCSharpDate(jsonObjectAim.getString("Date"));
        Date startDate = parseCSharpDate(jsonObjectAim.getString("StartDate"));
        Date endDate = parseCSharpDate(jsonObjectAim.getString("EndDate"));
        int dateSection = jsonObjectAim.getInt("DateSection");
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("Us");
            author = parseUser(userObject, false, false, 0);
        }
        int likes = 0;
        int dislikes = 0;
        int liked = 0;
        if(needLikes) {
            likes = jsonObjectAim.getInt("Likes");
            dislikes = jsonObjectAim.getInt("DisLikes");
            liked = jsonObjectAim.getInt("Liked");
        }
        return new AimType2(new ArrayList<Aim>(), text, header, 2, flag, modif, author, aimDate, startDate, endDate, likes, dislikes, liked, comments, proofs, dateSection);
    }

    /**
     * Парсит объект JSON для получения объекта цели 3-го типа.
     * @param jsonObjectAim Объект JSON для парсинга.
     * @param author Автор цели (или null, если автор при вызове метода неизвестен).
     * @param needLikes Показывает, нужно ли парсить лайки и дислайки.
     * @param comments Список комментариев к цели.
     * @param proofs Список подтверждений выполнения цели.
     * @return Объект цели 3-го типа.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    private static Aim parseAimType3(JSONObject jsonObjectAim, User author, boolean needLikes, List<Comment> comments, List<Proof> proofs) throws JSONException, IncompatibleAimsDatesException {
        String text = jsonObjectAim.getString("Text");
        String header = jsonObjectAim.getString("Header");
        int flag = jsonObjectAim.getInt("Flag");
        int modif = jsonObjectAim.getInt("Modif");
        Date aimDate = parseCSharpDate(jsonObjectAim.getString("Date"));
        Date startDate = parseCSharpDate(jsonObjectAim.getString("StartDate"));
        Date endDate = parseCSharpDate(jsonObjectAim.getString("EndDate"));
        int allTasks = jsonObjectAim.getInt("AllTasks");
        int currentTasks = jsonObjectAim.getInt("CurrentTasks");
        if(author == null) {
            JSONObject userObject = jsonObjectAim.getJSONObject("Us");
            author = parseUser(userObject, false, false, 0);
        }
        int likes = 0;
        int dislikes = 0;
        int liked = 0;
        if(needLikes) {
            likes = jsonObjectAim.getInt("Likes");
            dislikes = jsonObjectAim.getInt("DisLikes");
            liked = jsonObjectAim.getInt("Liked");
        }
        return new AimType3(new ArrayList<Aim>(), text, header, 3, flag, modif, author, aimDate, startDate, endDate, likes, dislikes, liked, comments, proofs, allTasks, currentTasks);
    }

    /**
     * Парсит объект JSON для получения объекта комментария к цели.
     * @param jsonObjectComment Объект JSON для парсинга.
     * @return Объект комментария к цели.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     */
    private static Comment parseComment(JSONObject jsonObjectComment) throws JSONException {
        String text = jsonObjectComment.getString("Text");
        String authorLogin = jsonObjectComment.getString("Login");
        String authorName = jsonObjectComment.getString("Name");
        String authorPhotoString = jsonObjectComment.getString("Photo");
        Bitmap authorPhoto = getBitmapFromBase64(authorPhotoString);
        Date date = parseCSharpDate(jsonObjectComment.getString("Date"));
        return new Comment(text, authorLogin, authorName, authorPhoto, date);
    }

    /**
     * Парсит объект JSON для получения объекта подтверждения выполнения цели.
     * @param jsonObjectProofPhoto Объект JSON для парсинга.
     * @return Объект подтверждения выполнения цели.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     */
    private static ProofPhoto parseProofPhoto(JSONObject jsonObjectProofPhoto) throws JSONException {
        String text = jsonObjectProofPhoto.getString("Text");
        Date date = parseCSharpDate(jsonObjectProofPhoto.getString("Date"));
        String photoString = jsonObjectProofPhoto.getString("Photo");
        Bitmap photo = getBitmapFromBase64(photoString);
        return new ProofPhoto(text, date, photo);
    }

    /**
     * Парсит объект JSON для получения объекта пользователя.
     * @param jsonObjectUser Объект JSON для парсинга.
     * @param needEmail Показывает, нужно ли парсить E-mail пользователя.
     * @param needRating Показывает, нужно ли парсить рейтинг пользователя.
     * @param inFriends -2 - пользователь в читаемых, -1 - пользователь в подписчиках, 0 - пользователь не в друзьях, 1 - пользователь в друзьях.
     * @return Объект пользователя.
     * @throws JSONException Возникает, если формат JSON-объекта некорректный.
     */
    private static User parseUser(JSONObject jsonObjectUser, boolean needEmail, boolean needRating, int inFriends) throws JSONException {
        String name = jsonObjectUser.getString("Name");
        String login = jsonObjectUser.getString("Login");
        String email = null;
        if(needEmail) {
            email = jsonObjectUser.getString("Email");
        }
        int sex = jsonObjectUser.getInt("Sex");
        Bitmap image = getBitmapFromBase64(jsonObjectUser.getString("Image"));
        Bitmap imageMin = getBitmapFromBase64(jsonObjectUser.getString("ImageMin"));
        int rating = 0;
        if(needRating) {
            rating = jsonObjectUser.getInt("Rating");
        }
        return new User(name, login, email, sex, image, imageMin, rating, inFriends);
    }

    /**
     * Объединяет массив тегов в одну строку, где теги разделены запятыми.
     * @param tags Массив тегов цели.
     * @return Строка, где теги разделены запятыми.
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
     * Получает строку Base64 из изображения, сохранённого в виде Bitmap.
     * @param imageBitmap Изображение, сохранённое в виде Bitmap.
     * @return Строка Base64.
     */
    private static String getBase64String(Bitmap imageBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String base64String = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64String;
    }

    /**
     * Получает изображение, сохранённое в виде Bitmap, из строки Base64.
     * @param base64String Строка Base64.
     * @return Изображение, сохранённое в виде Bitmap.
     */
    private static Bitmap getBitmapFromBase64(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return imageBitmap;
    }

    /**
     * Получает время UTC из времени в текущем часовом поясе.
     * @param date Время в текущем часовом поясе.
     * @return Время UTC из времени в текущем часовом поясе.
     */
    private static Date getUTCDate(Date date) {
        return new Date(date.getTime() - timeZoneOffsetMilliseconds);
    }

    /**
     * Получает время в текущем часовом поясе из времени UTC.
     * @param date Время UTC.
     * @return Время в текущем часовом поясе из времени UTC.
     */
    private static Date getCurrentTimeZoneDateFromUTC(Date date) {
        return new Date(date.getTime() + timeZoneOffsetMilliseconds);
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
        String response = Request.doRequest(urlString, null, null);
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
        if(!checkLogin(userLogin)) {
            throw new Exception("Логин может содержать только латинские символы, цифры и символы подчёркивания.");
        }
        String hashPsd = getMD5Hash(userPassword);
        String urlString = userURL;
        urlString += "register/";
        urlString = addAttribute(urlString, "login", userLogin, true);
        urlString = addAttribute(urlString, "hashpsd", hashPsd, false);
        urlString = addAttribute(urlString, "email", email, false);
        urlString = addAttribute(urlString, "name", name, false);
        urlString = addAttribute(urlString, "sex", sex, false);
        String response = Request.doRequest(urlString, null, null);
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
        String response = Request.doRequest(urlString, null, null);
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
    public static void changeImage(Bitmap image) throws Exception {
        String urlString = userURL;
        urlString += "changeimage/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token.");
        }
        String response = Request.doRequest(urlString, getBase64String(image), currentToken);
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
            DataStorage.getMe().setImage(image);
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
    public static void changePsd(String lastUserPassword, String userPassword) throws Exception {
        String lastHashPsd = getMD5Hash(lastUserPassword);
        String hashPsd = getMD5Hash(userPassword);
        String urlString = userURL;
        urlString += "changepsd/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token.");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "lasthashpsd", lastHashPsd, false);
        urlString = addAttribute(urlString, "hashpsd", hashPsd, false);
        String response = Request.doRequest(urlString, null, null);
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
        String response = Request.doRequest(urlString, null, null);
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
            DataStorage.getMe().setEmail(email);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Метод для изменения имени пользователя.
     * @param name Новое имя пользователя.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void changeName(String name) throws Exception {
        String urlString = userURL;
        urlString += "changename/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "name", name, false);
        String response = Request.doRequest(urlString, null, null);
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
            DataStorage.getMe().setName(name);
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
        String urlString = userURL;
        urlString += "search/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", userLogin, false);
        String response = Request.doRequest(urlString, null, null);
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
            JSONArray users = jsonObject.getJSONArray("Users");
            List<User> resultList = new ArrayList<>();
            for(int i = 0; i < users.length(); i++) {
                resultList.add(parseUser(users.getJSONObject(i), false, false, 0));
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
     * @return Возвращает полученный от сервера JSON в виде строки (для отправки на часы)
     */
    public static String getProfile() throws Exception {
        String urlString = newsURL;
        urlString += "getprofile/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        String response = Request.doRequest(urlString, null, null);
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
            User me = parseUser(userObject, true, true, 0);
            JSONArray aimsType1 = jsonObject.getJSONArray("aim1");
            for(int i = 0; i < aimsType1.length(); i++) {
                me.addAim(parseAimType1(aimsType1.getJSONObject(i), me, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                me.addAim(parseAimType2(aimsType2.getJSONObject(i), me, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                me.addAim(parseAimType3(aimsType3.getJSONObject(i), me, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            Collections.sort(me.getAims());
            DataStorage.setMe(me);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
        return response;
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
        urlString = addAttribute(urlString, "userlogin", userLogin, false);
        String response = Request.doRequest(urlString, null, null);
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
            int inFriends = jsonObject.getInt("inFriends");
            JSONObject userObject = jsonObject.getJSONObject("User");
            User user = parseUser(userObject, false, true, inFriends);
            JSONArray aimsType1 = jsonObject.getJSONArray("aim1");
            for(int i = 0; i < aimsType1.length(); i++) {
                user.addAim(parseAimType1(aimsType1.getJSONObject(i), user, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                user.addAim(parseAimType2(aimsType2.getJSONObject(i), user, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                user.addAim(parseAimType3(aimsType3.getJSONObject(i), user, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
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
     * @return Возвращает полученный от сервера JSON в виде строки (для отправки на часы)
     */
    public static String getNews(Date date) throws Exception {
        String urlString = newsURL;
        urlString += "getnews/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(date)), false);
        String response = Request.doRequest(urlString, null, null);
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
                DataStorage.getNewsFeed().add(parseAimType1(aimsType1.getJSONObject(i), null, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType2 = jsonObject.getJSONArray("aim2");
            for(int i = 0; i < aimsType2.length(); i++) {
                DataStorage.getNewsFeed().add(parseAimType2(aimsType2.getJSONObject(i), null, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            JSONArray aimsType3 = jsonObject.getJSONArray("aim3");
            for(int i = 0; i < aimsType3.length(); i++) {
                DataStorage.getNewsFeed().add(parseAimType3(aimsType3.getJSONObject(i), null, false, new ArrayList<Comment>(), new ArrayList<Proof>()));
            }
            Collections.sort(DataStorage.getNewsFeed());
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
        return response;
    }

    /**
     * Добавляет цель 1-го типа.
     * @param header Название цели.
     * @param text Текст цели.
     * @param mode Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param endDate Дата окончания выполнения цели.
     * @param startDate Дата начала выполнения цели.
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType1(String header, String text, int mode, Date endDate, Date startDate, String tags) throws Exception {
        String urlString = aimsURL;
        urlString += "type1/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "header", header, false);
        urlString = addAttribute(urlString, "text", text, false);
        urlString = addAttribute(urlString, "mode", "" + mode, false);
        urlString = addAttribute(urlString, "endDate", getCSharpDateString(getUTCDate(endDate)), false);
        urlString = addAttribute(urlString, "startDate", getCSharpDateString(getUTCDate(startDate)), false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType1(new ArrayList<Aim>(), text, header, 1, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate, 0, 0, 0, new ArrayList<Comment>(), new ArrayList<Proof>());
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString, null, null);
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
            Date date = parseCSharpDate(jsonObject.getString("date"));
            newAim.setDate(date);
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
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType2(String header, String text, int mode, Date endDate, Date startDate, int dateSection, String tags) throws Exception {
        String urlString = aimsURL;
        urlString += "type1/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "header", header, false);
        urlString = addAttribute(urlString, "text", text, false);
        urlString = addAttribute(urlString, "mode", "" + mode, false);
        urlString = addAttribute(urlString, "endDate", getCSharpDateString(getUTCDate(endDate)), false);
        urlString = addAttribute(urlString, "startDate", getCSharpDateString(getUTCDate(startDate)), false);
        urlString = addAttribute(urlString, "dateSection", "" + dateSection, false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType2(new ArrayList<Aim>(), text, header, 2, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate, 0, 0, 0, new ArrayList<Comment>(), new ArrayList<Proof>(), dateSection);
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString, null, null);
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
            Date date = parseCSharpDate(jsonObject.getString("date"));
            newAim.setDate(date);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Добавляет цель 3-го типа.
     * @param header Название цели.
     * @param text Текст цели.
     * @param mode Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param endDate Дата окончания выполнения цели.
     * @param startDate Дата начала выполнения цели.
     * @param AllTasks Общее количество однотипных задач, которые необходимо решить для выполнения цели.
     * @param tags Строка тегов цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addAimType3(String header, String text, int mode, Date endDate, Date startDate, int AllTasks, String tags) throws Exception {
        String urlString = aimsURL;
        urlString += "type1/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "header", header, false);
        urlString = addAttribute(urlString, "text", text, false);
        urlString = addAttribute(urlString, "mode", "" + mode, false);
        urlString = addAttribute(urlString, "endDate", getCSharpDateString(getUTCDate(endDate)), false);
        urlString = addAttribute(urlString, "startDate", getCSharpDateString(getUTCDate(startDate)), false);
        urlString = addAttribute(urlString, "AllTasks", "" + AllTasks, false);
        urlString = addAttribute(urlString, "tags", tags, false);
        Aim newAim = new AimType3(new ArrayList<Aim>(), text, header, 3, 0, mode, DataStorage.getMe(), new Date(), startDate, endDate, 0, 0, 0, new ArrayList<Comment>(), new ArrayList<Proof>(), AllTasks, 0);
        DataStorage.getMe().addAim(newAim);
        String response = Request.doRequest(urlString, null, null);
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
            Date date = parseCSharpDate(jsonObject.getString("date"));
            newAim.setDate(date);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Получает подробную информацию о цели.
     * @param aim Цель для получения подробной информации.
     * @return Возвращает подробную информацию о цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static Aim getAimInfo(Aim aim) throws Exception {
        String urlString = aimsURL;
        urlString += "info/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(aim.getDate())), false);
        String response = Request.doRequest(urlString, null, null);
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
            JSONArray jsonComments = jsonObject.getJSONArray("comments");
            List<Comment> comments = new ArrayList<Comment>();
            for(int i = 0; i < jsonComments.length(); i++) {
                comments.add(parseComment(jsonComments.getJSONObject(i)));
            }
            JSONArray jsonProofs = jsonObject.getJSONArray("proofs");
            List<Proof> proofs = new ArrayList<Proof>();
            for(int i = 0; i < jsonProofs.length(); i++) {
                proofs.add(parseProofPhoto(jsonProofs.getJSONObject(i)));
            }
            JSONObject jsonObjectAim = jsonObject.getJSONObject("Aim");
            Aim aimInfo = null;
            switch (aim.getType()) {
                case 1: aimInfo = parseAimType1(jsonObjectAim, aim.getAuthor(), true, comments, proofs); break;
                case 2: aimInfo = parseAimType2(jsonObjectAim, aim.getAuthor(), true, comments, proofs); break;
                case 3: aimInfo = parseAimType3(jsonObjectAim, aim.getAuthor(), true, comments, proofs); break;
            }
            return aimInfo;
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Пропускает цель (делает её проваленной).
     * @param aim Цель для пропуска.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void skipAim(Aim aim) throws Exception {
        String urlString = aimsURL;
        urlString += "skip/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(aim.getDate())), false);
        String response = Request.doRequest(urlString, null, null);
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

    /**
     * Добавляет комментарий к цели.
     * @param aim Цель для добавления комментария.
     * @param comment Добавляемый комментарий.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void commentAim(Aim aim, String comment) throws Exception {
        String urlString = aimsURL;
        urlString += "comment/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "dateAim", getCSharpDateString(getUTCDate(aim.getDate())), false);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "comment", comment, false);
        String response = Request.doRequest(urlString, null, null);
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
            aim.addComment(new Comment(comment, DataStorage.getMe().getLogin(), DataStorage.getMe().getName(), DataStorage.getMe().getImageMin(), new Date()));
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Добавляет лайк к цели.
     * @param aim Цель для лайка.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось или данный пользователь уже ставил лайк к данной цели.
     */
    public static void likeAim(Aim aim) throws Exception {
        if(aim.getLiked() == -1) {
            dislikeAim(aim);
        }
        String urlString = likesURL;
        urlString += "likeaim/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(aim.getDate())), false);
        String response = Request.doRequest(urlString, null, null);
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
            aim.addLike();
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Добавляет дислайк к цели.
     * @param aim Цель для дислайка.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось или данный пользователь уже ставил дислайк к данной цели.
     */
    public static void dislikeAim(Aim aim) throws Exception {
        if(aim.getLiked() == 1) {
            likeAim(aim);
        }
        String urlString = likesURL;
        urlString += "dislikeaim/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", aim.getAuthor().getLogin(), false);
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(aim.getDate())), false);
        String response = Request.doRequest(urlString, null, null);
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
            aim.addDislike();
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Добавляет подтверждение выполнения цели в виде фотографии.
     * @param aim Подтверждаемая цель.
     * @param proofText Текст подтверждения выполнения цели.
     * @param image Фотография подтверждения выполнения цели.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addProofPhoto(Aim aim, String proofText, Bitmap image) throws Exception {
        String urlString = proofsURL;
        urlString += "addproofphoto/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "date", getCSharpDateString(getUTCDate(aim.getDate())), false);
        urlString = addAttribute(urlString, "proofText", proofText, false);
        Date proofDate = new Date();
        urlString = addAttribute(urlString, "dateProof", getCSharpDateString(getUTCDate(proofDate)), false);
        String response = Request.doRequest(urlString, getBase64String(image), currentToken);
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
            Proof proofPhoto = new ProofPhoto(proofText, proofDate, image);
            aim.addProof(proofPhoto);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Отправляет запрос на добавление в друзья.
     * @param user Пользователь, которому отправляется запрос на добавления в друзья.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void addFriend(User user) throws Exception {
        String urlString = friendshipsURL;
        urlString += "add/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", user.getLogin(), false);
        String response = Request.doRequest(urlString, null, null);
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
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Отклоняет запрос на добавление в друзья.
     * @param user Пользователь, чей запрос на добавление в друзья отклоняется.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void declineRequest(User user) throws Exception {
        String urlString = friendshipsURL;
        urlString += "decline/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", user.getLogin(), false);
        String response = Request.doRequest(urlString, null, null);
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
                if(token.equals("Request Error")) {
                    throw new Exception("Такого запроса на добавления в друзья не существует.");
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
     * Подтверждает запрос на добавление в друзья.
     * @param user Пользователь, чей запрос на добавление в друзья подтверждается.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void acceptFriendship(User user) throws Exception {
        String urlString = friendshipsURL;
        urlString += "accept/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", user.getLogin(), false);
        String response = Request.doRequest(urlString, null, null);
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
                if(token.equals("Request Error")) {
                    throw new Exception("Такого запроса на добавления в друзья не существует.");
                }
                throw new Exception("Неизвестная ошибка.");
            }
            DataStorage.setToken(token);
            DataStorage.getMe().addFriend(user);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Удаляет пользователя из списка друзей.
     * @param user Пользователь, удаляемый из спика друзей.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void deleteFriend(User user) throws Exception {
        String urlString = friendshipsURL;
        urlString += "delete/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", user.getLogin(), false);
        String response = Request.doRequest(urlString, null, null);
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
            DataStorage.getMe().removeFriend(user, true);
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }

    /**
     * Получает список друзей пользователя.
     * @param user Пользователь, список друзей которого получаем.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     * @return Возвращает полученный от сервера JSON в виде строки (для отправки на часы)
     */
    public static String getFriends(User user) throws Exception {
        String urlString = friendshipsURL;
        urlString += "get/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        urlString = addAttribute(urlString, "userlogin", user.getLogin(), false);
        String response = Request.doRequest(urlString, null, null);
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
            JSONArray users = jsonObject.getJSONArray("Users");
            DataStorage.getMe().getFriends().clear();
            for(int i = 0; i < users.length(); i++) {
                DataStorage.getMe().getFriends().add(parseUser(users.getJSONObject(i), false, false, 1));
            }
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }

        return response;
    }

    /**
     * Получает список запросов на добавление в друзья.
     * @throws Exception Бросает исключение с текстом ошибки, если успешно выполнить метод не удалось.
     */
    public static void getFriendshipRequests() throws Exception {
        String urlString = friendshipsURL;
        urlString += "getreq/";
        String currentToken = DataStorage.getToken();
        if(currentToken == null) {
            throw new Exception("Ошибка подключения к серверу: пустой token");
        }
        urlString = addAttribute(urlString, "token", currentToken, true);
        String response = Request.doRequest(urlString, null, null);
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
            JSONArray users = jsonObject.getJSONArray("Users");
            DataStorage.getFriendshipRequests().clear();
            for(int i = 0; i < users.length(); i++) {
                DataStorage.getFriendshipRequests().add(parseUser(users.getJSONObject(i), false, false, -1));
            }
        }
        catch (JSONException ex) {
            throw new Exception("Ошибка формата ответа сервера.");
        }
    }
}
