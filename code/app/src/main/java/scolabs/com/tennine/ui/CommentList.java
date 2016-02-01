package scolabs.com.tennine.ui;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        commentList();
        ListView list = Global.lsView;//(ListView) findViewById(R.id.listView2);
        commentAdapter = new CommentAdapter();
        list.setAdapter(commentAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

            }
        });
    }

    private void commentList() {
        commentList = new ArrayList<>();
        commentList.add(new Comment("this is a nice show I like it", "scolary", new Date()));
        commentList.add(new Comment("I am sleepy, i will shaw the show tommorow", "Tsent", new Date()));
        commentList.add(new Comment("this is a nice show I like it", "scolary", new Date()));
        commentList.add(new Comment("I am sleepy, i will shaw the show tommorow", "Tsent", new Date()));
        commentList.add(new Comment("this is a nice show I like it", "scolary", new Date()));
        commentList.add(new Comment("I am sleepy, i will shaw the show tommorow", "Tsent", new Date()));
    }

    /**
     * The Class CutsomAdapter is the adapter class fo"cts ListView. The
     * currently implementation of this adapter simply display static dummy
     * contents. You need to write the code for displaying actual contents.
     */
    private class CommentAdapter extends BaseAdapter {

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return commentList.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Comment getItem(int arg0) {
            return commentList.get(arg0);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            if (v == null)
                v = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.comment_item, null);

            Comment c = getItem(pos);
            TextView lbl = (TextView) v.findViewById(R.id.content);
            lbl.setText(c.getContent());

            lbl = (TextView) v.findViewById(R.id.pins);
            if(c.getPings()> 1)
                lbl.setText(c.getPings()+" ups");
            else
                lbl.setText(c.getPings()+" up");

            lbl = (TextView) v.findViewById(R.id.comment_date);
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            lbl.setText(dateFormat.format(c.getDate()));
            return v;
        }
    }
}
