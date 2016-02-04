package scolabs.com.tennine.model;

import java.util.Date;

/**
 * Created by scolary on 1/30/2016.
 */
public class Comment
{
    private final int WORDS_LIMIT = 144;
    private String content;
    private String commentator;
    private Date date;
    private long ups_mark;
    private long down_mark;
    

    public Comment()
    {

    }

    public Comment(String content, String commentator, Date date) {
        this.content = content;
        this.commentator = commentator;
        this.date = date;
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
