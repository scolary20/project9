package scolabs.com.tennine.model;

import java.util.Date;

/**
 * Created by scolary on 1/30/2016.
 */
public class Comment
{
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment()
    {

    }

    public Comment(String content, String commentator, Date date) {
        this.content = content;
        this.commentator = commentator;
        this.date = date;

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

    private String content;
    private String commentator;
    private Date date;
    private final int WORDS_LIMIT = 144;

    public long getPings() {
        return pings;
    }

    public void setPings(long pings) {
        this.pings = pings;
    }

    private long pings;


}
