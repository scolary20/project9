package scolabs.com.tenine.model;

import android.support.annotation.ColorRes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by scolary on 1/30/2016.
 */
@Table(name="Comment")
public class Comment extends Model
{
    @Column
    private final int WORDS_LIMIT = 144;
    @Column
    private String content;
    @Column
    private Date date;
    @Column
    private long ups_mark;
    @Column
    private long down_mark;
    @Column
    private String commentator;

    @Column
    private long showId;

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }

    @Column
    private Show show;

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Comment()
    {

    }

    public Comment(String content, String commentator, Date date, long showId) {
        this.content = content;
        this.commentator = commentator;
        this.date = date;
        this.showId = showId;
        ups_mark = 452342L;
    }

    public long getUps_mark() {
        return ups_mark;
    }

    public void setUps_mark(long ups_mark) {
        this.ups_mark = ups_mark;
    }

    public long getDown_mark() {
        return down_mark;
    }

    public void setDown_mark(long down_mark) {
        this.down_mark = down_mark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCommentator() {
        return commentator;
    }

    public void setCommentator(String commentator) {
        this.commentator = commentator;
    }

    public int getWORDS_LIMIT() {
        return WORDS_LIMIT;
    }


}
