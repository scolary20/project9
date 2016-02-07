package scolabs.com.tenine.model;

import java.util.Date;

/**
 * Created by scolary on 1/29/2016.
 */
public class Show {
    private Date airing_date;
    private Date airing_time;
    private long num_watching;
    private long comments;
    private long show_length;
    private String name;
    private String season;
    private String network;
    private int id;
    public Show(String name, String season, String network) {
        this.name = name;
        this.season = season;
        this.network = network;
        this.id = 1+(int) (Math.random() * 100);
    }

    public Date getAiring_date() {
        return airing_date;
    }

    public void setAiring_date(Date airing_date) {
        this.airing_date = airing_date;
    }

    public Date getAiring_time() {
        return airing_time;
    }

    public void setAiring_time(Date airing_time) {
        this.airing_time = airing_time;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public long getShow_length() {
        return show_length;
    };

    public void setShow_length(long show_length) {
        this.show_length = show_length;
    }

    public void Show() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public long getNum_watching() {
        return num_watching;
    }

    public void setNum_watching(long num_watching) {
        this.num_watching = num_watching;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

private enum RatingTrend{up,down}
}

