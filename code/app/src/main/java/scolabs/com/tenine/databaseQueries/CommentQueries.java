package scolabs.com.tenine.databaseQueries;

import com.activeandroid.query.Select;

import java.util.ArrayList;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 2/22/2016.
 */
public class CommentQueries {

    public static ArrayList<Comment> getComments(long showId)
    {
        return (ArrayList)new Select()
                .from(Comment.class)
                .where("showId = ?", showId)
                .execute();
    }

}
