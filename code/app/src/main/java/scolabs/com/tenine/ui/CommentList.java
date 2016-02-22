package scolabs.com.tenine.ui;

import android.app.Activity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Global;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 1/30/2016.
 */
public class CommentList extends Activity
{
    private ArrayList<Comment> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;
    long showId = Global.showId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();
        commentList();
        ListView list = Global.lsView;//(ListView) findViewById(R.id.listView2);
        commentAdapter = new CommentAdapter(this, R.layout.comment_item, commentList);
        list.setAdapter(commentAdapter);
        Global.commentListContext = this;
    }
    private void commentList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Comment c1 = new Comment("this is a nice show I like it", "scolary", new Date(),showId);
                commentList.add(c1);
                //c1.save();
                Comment c2 = new Comment("I am sleepy, i will shaw the show tommorow", "tsent", new Date(), showId);
                commentList.add(c2);
                //c2.save();
                Comment c3 = new Comment("Jesus navas, whzt a name", "Nyota", new Date(),showId);
                commentList.add(c3);
                //c3.save();
                Comment c4 = new Comment("lot lot of fun", "Dembaba", new Date(),showId);
                commentList.add(c4);
                //c4.save();
                Comment c5 = new Comment("You cannot ignore..It gonna be there", "xCalibar", new Date(),showId);
                commentList.add(c5);
                //c5.save();
               commentAdapter.notifyDataSetChanged();
            }
        }).start();
        //commentList = CommentQueries.getComments(showId);
    }
}
