package scolabs.com.tenine.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Global;

/**
 * Created by scolary on 1/30/2016.
 */
public class CommentList extends Activity
{
    CommentAdapter commentAdapter;
    long showId = Global.showId;
    ListView list;
    private ArrayList<Comment> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        new LoadComments().execute("");
    }

    private class LoadComments extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            commentList = CommentQueries.getComments(showId);
            Log.d("Number of Elements", ""+commentList.size());
            Log.d("Show Id", ""+showId);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            final long totalScrollTime = 60000 * 40;
            final int scrollPeriod = 1500;
            final int heightToScroll = 20;

            list = Global.lsView;
            commentAdapter = new CommentAdapter(CommentList.this, R.layout.comment_item, commentList);
            list.setAdapter(commentAdapter);
            commentAdapter.setNotifyOnChange(true);
            Global.cmAdapter = commentAdapter;
            Global.commentListContext = CommentList.this;
            Global.progressBar.setVisibility(View.GONE);

            if(commentList.size()>0)
            {
                Global.txt.setVisibility(View.GONE);
            }
            else
                Global.txt.setText("No comments...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Global.progressBar.setVisibility(View.VISIBLE);
            Global.txt.setText("Loading comments...");
            Global.txt.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
