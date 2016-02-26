package scolabs.com.tenine.model;

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
    @Column
    private Show show;

    @Column
    private int marked_up;

    @Column
    private int marked_down;
    @Column(index = true, notNull = true, unique = true)
    private long cmtId;

    public Comment()
    {

    }

    public Comment(String content, String commentator, Date date, long showId) {
        this.content = content;
        this.commentator = commentator;
        this.date = date;
        this.showId = showId;
        ups_mark = 452342L;
        this.cmtId = 1 + (int) (Math.random() * 100);
    }

    public int isMarked_down() {
        return marked_down;
    }

    public void setMarked_down(int marked_down) {
        this.marked_down = marked_down;
    }

    public int isMarked_up() {
        return marked_up;
    }

    public void setMarked_up(int marked_up) {
        this.marked_up = marked_up;
    }

    public long getCmtId() {
        return cmtId;
    }

    public void setCmtId(long cmtId) {
        this.cmtId = cmtId;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
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
