package scolabs.com.tenine.ui;

import android.app.Activity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.R;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Global;

/**
 * Created by scolary on 1/30/2016.
 */
public class CommentList extends Activity
{
    private ArrayList<Comment> commentList;
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
        commentList = new ArrayList<>();
        commentList.add(new Comment("this is a nice show I like it", "scolary", new Date()));
        commentList.add(new Comment("I am sleepy, i will shaw the show tommorow", "Tsent", new Date()));
        commentList.add(new Comment("Jesus navas, whzt a name", "Nyota", new Date()));
        commentList.add(new Comment("lot lot of fun", "Mnet", new Date()));
        commentList.add(new Comment("You cannot ignore..It gonna be there", "scolary", new Date()));
        commentList.add(new Comment("it's not easy...I am talking about", "Tsent", new Date()));
        commentList.add(new Comment("Jesus navas, whzt a name", "Nyota", new Date()));
        commentList.add(new Comment("the sooner you get the","Reward Ent", new Date()));
    }
}
