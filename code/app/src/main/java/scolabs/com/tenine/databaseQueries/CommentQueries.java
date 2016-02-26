package scolabs.com.tenine.databaseQueries;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.ArrayList;

import scolabs.com.tenine.model.Comment;

/**
 * Created by scolary on 2/22/2016.
 */
public class CommentQueries {

    public static ArrayList<Comment> getComments(long showId)
    {
        try{
            Thread.currentThread().sleep(1000);
        }catch (Exception ex){}

        return (ArrayList)new Select()
                .from(Comment.class)
                .where("showId = ?", showId)
                .execute();
    }

    public static Comment getCommentById(long cmtId) {
        return new Select()
                .from(Comment.class)
                .where("cmtId = ?", cmtId)
                .executeSingle();
    }

    public static void updateComment(final long cmtId, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteUtils.execSql("UPDATE Comment SET content = '" + message + "' WHERE cmtId = " + cmtId);
            }
        }).start();
    }

    public static void updateComment(final long cmtId, final int val, final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (type.equals("up")) {
                    if (val == 1)
                        SQLiteUtils.execSql("UPDATE Comment SET marked_up = " + val + ", ups_mark = ups_mark+1 WHERE cmtId = " + cmtId);
                    else
                        SQLiteUtils.execSql("UPDATE Comment SET marked_up = " + val + ", ups_mark = ups_mark-1 WHERE cmtId = " + cmtId);
                } else if (type.equals("down")) {
                    if (val == 1)
                        SQLiteUtils.execSql("UPDATE Comment SET marked_down = " + val + ", down_mark = down_mark+1 WHERE cmtId = " + cmtId);
                    else
                        SQLiteUtils.execSql("UPDATE Comment SET marked_down = " + val + ", down_mark = down_mark-1 WHERE cmtId = " + cmtId);
                }
            }
        }).start();
    }
}
