package scolabs.com.tenine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.DateFormat;
import java.util.Date;

import scolabs.com.tenine.model.Global;
import scolabs.com.tenine.ui.CommentAdapter;
import scolabs.com.tenine.ui.CommentList;


public class CommentActivity extends ActionBarActivity {

    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private long showId = Global.showId;
    private boolean isScrolling = false;
    private ScrollThread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_ui);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.live_comment);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Global.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Global.txt = (TextView)findViewById(R.id.cmt_feedback);

        TextView date = (TextView) findViewById(R.id.show_date);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        date.setText(df.format(new Date()));
        Global.commentActivity = this;

        if (mediaControls == null) {
            mediaControls = new MediaController(CommentActivity.this);
        }

        //initialize the videoView
        myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
            }
        });

        //create a progess bar while the video is loading
        progressDialog = new ProgressDialog(CommentActivity.this);
        //set a title for the progress bar
        progressDialog.setTitle("Getting Content from server...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        try {
            myVideoView.setMediaController(mediaControls);
            myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.empire_trailer));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                myVideoView.seekTo(position);
                mp.setVolume(0f, 0f);
                if (position == 0)
                    myVideoView.start();
                else {
                    myVideoView.pause();
                }
            }
        });

        int screen_orientation = this.getResources().getConfiguration().orientation;
        if (screen_orientation == Configuration.ORIENTATION_PORTRAIT) {
            Global.lsView = (ListView) findViewById(R.id.listView2);
            Intent myIntent = new Intent(CommentActivity.this, CommentList.class);
            startActivity(myIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
        myVideoView.start();
        myVideoView.refreshDrawableState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.expand) {
            if (myVideoView.getVisibility() == View.GONE)
                myVideoView.setVisibility(myVideoView.VISIBLE);
            else
                myVideoView.setVisibility(myVideoView.GONE);
        }

        if(id == android.R.id.home)
        {
            onBackPressed();
        }

        if(id == R.id.comment_bt){
            Intent i = new Intent(this,WriteComment.class);
            i.putExtra("showId", showId);
            i.putExtra("type", 0);
            startActivityForResult(i, 2);
        }

        if (id == R.id.unsubscribe) {
            new AlertDialog.Builder(this)
                    .setTitle("Lock This Show")
                    .setMessage("You won't be able to comment\n Are you sure?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (id == R.id.auto_scroll) {
            final ListView list = Global.lsView;
            CommentAdapter commentAdapter = Global.cmAdapter;
            list.setAdapter(commentAdapter);
            list.setSmoothScrollbarEnabled(true);

            if (!isScrolling) {
                isScrolling = true;
                thread = new ScrollThread();
                list.post(thread);
            } else {
                thread.stop();
                list.setTop(0);
                isScrolling = false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class ScrollThread implements Runnable {
        final long totalScrollTime = 60000 * 40;
        final int scrollPeriod = 1500;
        final int heightToScroll = 20;
        final ListView list = Global.lsView;
        Thread thd;


        @Override
        public void run() {
            new CountDownTimer(totalScrollTime, scrollPeriod) {
                public void onTick(long millisUntilFinished) {
                    if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1
                            && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
                        list.smoothScrollToPosition(0);
                    }
                    list.scrollBy(0, heightToScroll);
                    thd = Thread.currentThread();
                }

                public void onFinish() {
                    //you can add code for restarting timer here
                }
            }.start();
        }

        public void stop() {
            try {
                thd.interrupt();
            } catch (Exception ex) {
            }

        }
    }
}
