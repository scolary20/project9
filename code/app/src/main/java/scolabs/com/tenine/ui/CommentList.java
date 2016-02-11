package scolabs.com.tenine.ui;

import android.app.Activity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.R;
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
        commentList.add(new Comment("this is a nice show I like it", new User("scolary", "jt@lar.com","pass"), new Date()));
        commentList.add(new Comment("I am sleepy, i will shaw the show tommorow", Settings.getLoginUser(), new Date()));
        commentList.add(new Comment("Jesus navas, whzt a name", new User("Nyota", "jt@lar.com","pass"), new Date()));
        commentList.add(new Comment("lot lot of fun", new User("Dembaba", "jt@lar.com","pass"), new Date()));
        commentList.add(new Comment("You cannot ignore..It gonna be there", new User("xCalibar", "jt@lar.com","pass"), new Date()));
        commentList.add(new Comment("it's not easy...I am talking about", Settings.getLoginUser(), new Date()));
        commentList.add(new Comment("Jesus navas, whzt a name", new User("Djogo", "jt@lar.com","pass"), new Date()));
        commentList.add(new Comment("the sooner you get the",new User("Reward ent", "jt@lar.com","pass"), new Date()));
    }
}
