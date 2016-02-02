package scolabs.com.tennine.ui;

import android.app.ActionBar;
import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
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
        commentAdapter = new CommentAdapter(this, R.layout.comment_item,commentList);
        list.setAdapter(commentAdapter);

        final Animation bounce = AnimationUtils.loadAnimation(CommentList.this,R.anim.bounce_marks);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                TextView up = (TextView)arg1.findViewById(R.id.up_mark);
                TextView down = (TextView)arg1.findViewById(R.id.down_mark);
                final ImageView profile_pic = (ImageView)arg1.findViewById(R.id.commet_pp);
                final LinearLayout lly = (LinearLayout)arg1.findViewById(R.id.rating_linear);
                final TextView content = (TextView)arg1.findViewById(R.id.content);
                final int poss = pos;

                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(bounce);
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(bounce);
                    }
                });

                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(profile_pic.getVisibility()==View.GONE)
                        {
                            profile_pic.setVisibility(profile_pic.VISIBLE);
                            System.out.println("Position "+ poss);
                        }
                        else
                        {
                            profile_pic.setVisibility(profile_pic.GONE);
                            System.out.println("Position "+ poss);
                        }
                    }
                });

                final int initial_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getHeight(),getResources().getDisplayMetrics());
                final int initial_width  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getWidth(),getResources().getDisplayMetrics());

                profile_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(content.getVisibility()==View.GONE){
                            content.setVisibility(content.VISIBLE);
                            lly.setVisibility(lly.VISIBLE);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height/2;
                            params.width =  initial_width/2;
                            profile_pic.setLayoutParams(params);
                        }
                        else
                        {
                            Animation bounce = AnimationUtils.loadAnimation(CommentList.this,R.anim.image_anim);
                            content.setVisibility(content.GONE);
                            lly.setVisibility(lly.GONE);
                            profile_pic.setAnimation(bounce);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height * 2;
                            params.width =initial_width * 2;
                            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(initial_width*2, initial_height*2);
                            //layoutParams.gravity=Gravity.CENTER;
                            profile_pic.setLayoutParams(params);
                        }
                    }
                });
            }

        });

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
