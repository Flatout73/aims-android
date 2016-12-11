package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.aimsproject.exceptions.*;

/**
 * Created by Антон on 27.10.2016.
 * Представляет цель. Абстрактный класс.
 */
public abstract class Aim implements Comparable<Aim> {
    /**
     * Список подцелей.
     */
    private List<Aim> subAims = new ArrayList<Aim>();

    /**
     * Надцель.
     */
    private Aim superAim;

    /**
     * Текст цели.
     */
    private String text;

    /**
     * Название цели.
     */
    private String header;

    /**
     * Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     */
    private int type;

    /**
     * Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     */
    private int flag;

    /**
     * Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     */
    private int modif;

    /**
     * Список пользователей, имеющих доступ к просмотру цели.
     */
    private List<User> readingAllowedUsers = new ArrayList<User>();

    /**
     * Список пользователей, имеющих доступ к редактированию (и просмотру) цели.
     */
    private List<User> writingAllowedUsers = new ArrayList<User>();

    /**
     * Автор цели.
     */
    private User author;

    /**
     * Дата публикации цели.
     */
    private Date date;

    /**
     * Дата начала выполнения цели.
     */
    private Date startDate;

    /**
     * Дата окончания выполнения цели.
     */
    private Date endDate;

    /**
     * Конструктор, инициализирует объект цели.
     // * @param subAims Список подцелей.
     * @param text Текст цели.
     * @param header Название цели.
     * @param type Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @param flag Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @param modif Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param author Автор цели.
     * @param date Дата публикации цели.
     * @param startDate Дата начала выполнения цели.
     * @param endDate Дата окончания выполнения цели.
     * @throws IncompatibleAimsDatesException Возникает, если дата начала выполнения цели раньше даты её публикации или дата окончания выполнения цели раньше даты её начала.
     */
    public Aim( /* List<Aim> subAims, */ String text, String header, int type, int flag, int modif, User author, Date date, Date startDate, Date endDate) throws IncompatibleAimsDatesException {
        if(date.compareTo(startDate) > 0) {
            throw new IncompatibleAimsDatesException("Время начала выполнения цели должно быть не раньше времени её публикации.", this);
        }
        if(startDate.compareTo(endDate) > 0) {
            throw new IncompatibleAimsDatesException("Время окончания выполнения цели должно быть позже времени начала её выполнения.", this);
        }
        // this.subAims = subAims;
        this.text = text;
        this.header = header;
        this.type = type;
        this.flag = flag;
        this.modif = modif;
        this.author = author;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Возвращает список поцелей.
     * @return Список подцелей.
     */
    public List<Aim> getSubAims() {
        return subAims;
    }

    /**
     * Возвращает надцель.
     * @return Надцель.
     */
    public Aim getSuperAim() {
        return superAim;
    }

    /**
     * Возвращает текст цели.
     * @return Текст цели.
     */
    public String getText() {
        return text;
    }

    /**
     * Возвращает название цели.
     * @return Название цели.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Возвращает тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @return Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     */
    public int getType() {
        return type;
    }

    /**
     * Возвращает состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @return Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Возвращает модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @return Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     */
    public int getModif() {
        return modif;
    }

    /**
     * Возвращает список пользователей, имеющих доступ к просмотру цели.
     * @return Список пользователей, имеющих доступ к просмотру цели.
     */
    public List<User> getReadingAllowedUsers() {
        return readingAllowedUsers;
    }

    /**
     * Возвращает список пользователей, имеющих доступ к редактированию (и просмотру) цели.
     * @return Список пользователей, имеющих доступ к редактированию (и просмотру) цели.
     */
    public List<User> getWritingAllowedUsers() {
        return writingAllowedUsers;
    }

    /**
     * Возвращает автора цели.
     * @return Автор цели.
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Возвращает дату публикации цели.
     * @return Дата публикации цели.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Возвращает дату начала выполнения цели.
     * @return Дата начала выполнения цели.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Возвращает дату окончания выполнения цели.
     * @return Дата окончания выполнения цели.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Устанавливает надцель.
     * @param superAim Надцель.
     */
    public void setSuperAim(Aim superAim) {
        this.superAim = superAim;
        modif = superAim.getModif();
        if(modif == 1) {
            readingAllowedUsers = superAim.getReadingAllowedUsers();
        }
    }

    /**
     * Устанавливает текст цели.
     * @param text Текст цели.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Устанавливает название цели.
     * @param header Название цели.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Устанавливает состояние выполнения цели.
     * @param flag Состояние выполнения цели.
     * @throws UnallowedAimFlagChangeException Возникает, если состояние цели меняется "назад" (например, с 1 на 0) или если меняется состояние завершённой/проваленной цели.
     */
    public void setFlag(int flag) throws UnallowedAimFlagChangeException {
        if(this.flag == 1 && flag == 0) {
            throw new UnallowedAimFlagChangeException("Состояние выполнения цели не может быть изменено с начатой на неначатую.", this);
        }
        if(this.flag == 2) {
            throw new UnallowedAimFlagChangeException("Состояние выполнения завершённой цели не может быть изменено.", this);
        }
        if(this.flag == 3) {
            throw new UnallowedAimFlagChangeException("Состояние выполнения проваленной цели не может быть изменено.", this);
        }
        this.flag = flag;
    }

    /**
     * Устанавливает модификатор доступа к цели таким же, как у надцели, если надцель установлена.
     */
    public void setModifAsInSuperAim() {
        if(superAim != null) {
            modif = superAim.getModif();
        }
    }

    /**
     * Сравнивает цель с другой для сортировки по убыванию дат публикации целей.
     * @param other Другая цель.
     * @return Значение для метода сортировки по убыванию дат публикации целей.
     */
    @Override
    public int compareTo(Aim other) {
        return -date.compareTo(other.getDate());
    }

    /**
     * Добавляет пользователя в список пользователей, имеющих доступ к просмотру цели, если его ещё нет в этом списке.
     * @param user Добавляемый пользователь.
     * @return true, если добавление пользователя успешно состоялось (пользователя ещё не было в этом списке), иначе false.
     */
    public boolean addReadingAllowedUser(User user) {
        if(!readingAllowedUsers.contains(user)) {
            readingAllowedUsers.add(user);
            return true;
        }
        return false;
    }

    /**
     * Удаляет пользователя из списка пользователей, имеющих доступ к просмотру цели, если он есть в этом списке.
     * @param user Удаляемый пользователь.
     * @return true, если удаление пользователя успешно состоялось (пользователь был в этом списке), иначе false.
     */
    public boolean removeReadingAllowedUser(User user) {
        return readingAllowedUsers.remove(user);
    }

    /**
     * Удаляет пользователя из списка пользователей, имеющих доступ к просмотру цели, по индексу, если индекс находится в пределах размера списка.
     * @param index Индекс удаляемого пользователя.
     * @return true, если удаление пользователя успешно состоялось (индекс находился в пределах размера списка), иначе false.
     */
    public boolean removeReadingAllowedUser(int index) {
        try {
            readingAllowedUsers.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    /**
     * Добавляет пользователя в список пользователей, имеющих доступ к редактированию (и просмотру) цели, если его ещё нет в этом списке.
     * @param user Добавляемый пользователь.
     * @return true, если добавление пользователя успешно состоялось (пользователя ещё не было в этом списке), иначе false.
     */
    public boolean addWritingAllowedUser(User user) {
        if(!writingAllowedUsers.contains(user)) {
            writingAllowedUsers.add(user);
            return true;
        }
        return false;
    }

    /**
     * Удаляет пользователя из списка пользователей, имеющих доступ к редактированию (и просмотру) цели, если он есть в этом списке.
     * @param user Удаляемый пользователь.
     * @return true, если удаление пользователя успешно состоялось (пользователь был в этом списке), иначе false.
     */
    public boolean removeWritingAllowedUser(User user) {
        return writingAllowedUsers.remove(user);
    }

    /**
     * Удаляет пользователя из списка пользователей, имеющих доступ к редактированию (и просмотру) цели, по индексу, если индекс находится в пределах размера списка.
     * @param index Индекс удаляемого пользователя.
     * @return true, если удаление пользователя успешно состоялось (индекс находился в пределах размера списка), иначе false.
     */
    public boolean removeWritingAllowedUser(int index) {
        try {
            writingAllowedUsers.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    /**
     * Добавляет подцель в список подцелей, если её ещё нет в этом списке.
     * @param subAim Добавляемая подцель.
     * @return true, если добавление подцели успешно состоялось (подцели ещё не было в этом списке), иначе false.
     * @throws IncompatibleSubAndSuperAimsFlagsException Возникает, если надцель или подцель уже завершена/провалена.
     * @throws IncompatibleSubAndSuperStartDatesException Возникает, если дата начала выполнения подцели раньше даты начала выполнения надцели.
     * @throws IncompatibleSubAndSuperEndDatesException Возникает, если дата окончания выполнения подцели позже даты окончания выполнения надцели.
     * @throws NullPointerException Возникает, если переданная подцель представляет собой null.
     */
    public boolean addSubAim(Aim subAim)
            throws IncompatibleSubAndSuperAimsFlagsException, IncompatibleSubAndSuperStartDatesException, IncompatibleSubAndSuperEndDatesException, NullPointerException {
        if(subAims.contains(subAim)) {
            return false;
        }
        if(startDate.compareTo(subAim.getStartDate()) > 0) {
            throw new IncompatibleSubAndSuperStartDatesException("Дата начала выполнения подцели должна быть не раньше, чем дата начала выполнения надцели.", subAim, this);
        }
        if(endDate.compareTo(subAim.getEndDate()) < 0) {
            throw new IncompatibleSubAndSuperStartDatesException("Дата окончания выполнения подцели должна быть не позже, чем дата окончания выполнения надцели.", subAim, this);
        }
        if(flag == 2) {
            throw new IncompatibleSubAndSuperAimsFlagsException("Надцель не должна быть уже выполненной.", subAim, this);
        }
        if(flag == 3) {
            throw new IncompatibleSubAndSuperAimsFlagsException("Надцель не должна быть уже проваленной.", subAim, this);
        }
        if(subAim.getFlag() == 2) {
            throw new IncompatibleSubAndSuperAimsFlagsException("Подцель не должна быть уже выполненной.", subAim, this);
        }
        if(subAim.getFlag() == 3) {
            throw new IncompatibleSubAndSuperAimsFlagsException("Подцель не должна быть уже проваленной.", subAim, this);
        }
        subAims.add(subAim);
        subAim.setSuperAim(this);
        if(flag == 0 && subAim.getFlag() == 1) {
            flag = 1;
        }
        subAim.setModifAsInSuperAim();
        return true;
    }

    /**
     * Удаляет подцель из списка подцелей, если она есть в этом списке.
     * @param subAim Удаляемая подцель.
     * @return true, если удаление подцели успешно состоялось (подцель была в этом списке), иначе false.
     * @throws NullPointerException Возникает, если переданная подцель представляет собой null.
     */
    public boolean removeSubAim(Aim subAim) throws NullPointerException {
        if(subAim.getSuperAim() == this) {
            subAim.setSuperAim(null);
        }
        return subAims.remove(subAim);
    }

    /**
     * Удаляет подцель из списка подцелей, по индексу, если индекс находится в пределах размера списка.
     * @param index Индекс удаляемой подцели.
     * @return true, если удаление подцели успешно состоялось (индекс находился в пределах размера списка), иначе false.
     */
    public boolean removeSubAim(int index) {
        try {
            Aim subAim = subAims.get(index);
            if(subAim.getSuperAim() == this) {
                subAim.setSuperAim(null);
            }
            subAims.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }
}
