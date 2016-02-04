package scolabs.com.tennine.ui;

import android.app.ActionBar;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import scolabs.com.tennine.DrawerItemCustomAdapter;
import scolabs.com.tennine.R;
import scolabs.com.tennine.model.Comment;
import scolabs.com.tennine.model.Global;
import scolabs.com.tennine.model.Show;

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
