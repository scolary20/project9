package scolabs.com.tenine.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by scolary on 1/29/2016.
 */
@Table(name="Show")
public class Show extends Model{
    @Column(index = true)
    private Date airing_date;
    @Column
    private Date airing_time;
    @Column
    private long num_watching;
    @Column
    private long num_comment;
    @Column
    private long show_length;
    @Column
    private String name;
    @Column
    private String season;
    @Column
    private String episode;
    @Column
    private String network;
    @Column(index = true, notNull = true, unique = true)
    private long showId;
    @Column
    private int rating_arrow; //1: up, 2: down
    @Column
    private String show_trailer_location;
    @Column
    private String show_stheme_location; //song theme
    @Column
    private String show_img_location;
    @Column
    private String comment_content;
    @Column
    private long rating;

    public Show(String name, String season, String network) {
        this.name = name;
        this.season = season;
        this.network = network;
        this.showId = new Random().nextInt();   //1 + (int) (Math.random() * 100);
        rating_arrow = 2;
    }

    public Show() {

    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getShow_trailer_location() {
        return show_trailer_location;
    }

    public void setShow_trailer_location(String show_trailer_location) {
        this.show_trailer_location = show_trailer_location;
    }

    public String getShow_img_location() {
        return show_img_location;
    }

    public void setShow_img_location(String show_img_location) {
        this.show_img_location = show_img_location;
    }

    public String getShow_stheme_location() {
        return show_stheme_location;
    }

    public void setShow_stheme_location(String show_stheme_location) {
        this.show_stheme_location = show_stheme_location;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getRating_arrow() {
        return rating_arrow;
    }

    public void setRating_arrow(int rating_arrow) {
        this.rating_arrow = rating_arrow;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
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

    public long getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(long num_comment) {
        this.num_comment = num_comment;
    }

    ;

    public long getShow_length() {
        return show_length;
    }

    public void setShow_length(long show_length) {
        this.show_length = show_length;
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

    public List<Comment> comments() {
        return getMany(Comment.class, "Comment");
    }

    public List<User> usersWatching() {
        return getMany(User.class, "User");
    }
}

