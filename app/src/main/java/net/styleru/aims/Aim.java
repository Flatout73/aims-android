package net.styleru.aims;

import java.util.Date;

/**
 * Created by LeonidL on 23.12.16.
 */

public class Aim {

    private String text;

    /**
     * Название цели.
     */
    private String header;

    private String desription;


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
     * Дата начала выполнения цели.
     */
    private Date startDate;

    /**
     * Дата окончания выполнения цели.
     */
    private Date endDate;

    private String tags;

    private int miniTargets;

    public Aim(String header, String desription, int type, Date end, Date start, String tags) {
        this.header = header;
        this.desription = desription;
        this.type = type;
        this.endDate = end;
        this.startDate = start;
        this.tags = tags;
    }


    public String getText() {
        return text;
    }

    public String getHeader() {
        return header;
    }

    public String getDesription() {
        return desription;
    }

    public int getType() {
        return type;
    }

    public int getFlag() {
        return flag;
    }

    public int getModif() {
        return modif;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTags() {
        return tags;
    }

    public int getMiniTargets() {
        return miniTargets;
    }

    public void setMiniTargets(int miniTargets) {
        this.miniTargets = miniTargets;
    }
}


