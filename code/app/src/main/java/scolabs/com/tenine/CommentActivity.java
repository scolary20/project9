package scolabs.com.tenine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarContextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.databaseQueries.UserQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.ui.CommentAdapter;
import scolabs.com.tenine.ui.CommentList;
import scolabs.com.tenine.utils.GlobalSettings;


public class CommentActivity extends ActionBarActivity {

    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private long showId;
    private Show clickedShow;
    private boolean isScrolling = true;
    private ScrollThread thread;
    private ArrayList<CountDownTimer> waitTimeList;
    private int scrollSpeedCount;
    private ImageButton send;
    private User loginUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_ui);

        //DB initialisation
        GlobalSettings.setup_db(this, "", false);

        //Handling the user, redirect if no user
        if (!getLoginUser())
            new Intent(this, MainActivity.class);

        Global.commentActivity = this;

        //Getting the Show
        showId = getIntent().getLongExtra("showId", -999);
        clickedShow = ShowQueries.getShowById(showId);

        actionBarSetup();
        Global.linearList = (LinearLayout) findViewById(R.id.linearList);
        TextView numOfComments = (TextView) findViewById(R.id.numOfComments);


        waitTimeList = new ArrayList<>();
        scrollSpeedCount = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Global.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Global.txt = (TextView)findViewById(R.id.cmt_feedback);

        TextView date = (TextView) findViewById(R.id.show_date);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        date.setText(df.format(new Date()));

        sendCommentButton();
        dialogWhileVideoLoading();
        initializeVideoView();
        setVideoPath();
        playVideo();
        startCommentListIntent();
    }

    public boolean getLoginUser() {
        loginUser = GlobalSettings.getLoginUser();
        if (loginUser == null) {
            SharedPreferences sp = GlobalSettings.getSharedPreference(this);
            String username = sp.getString("username", "");
            loginUser = UserQueries.getDbUser(username, "", "", "");
            GlobalSettings.setLoginUser(loginUser);
            GlobalSettings.setupChatSettings(loginUser, this);
        }
        if (loginUser != null)
            return true;
        else
            return false;
    }

    public void startCommentListIntent() {
        int screen_orientation = this.getResources().getConfiguration().orientation;
        if (screen_orientation == Configuration.ORIENTATION_PORTRAIT) {
            Global.lsView = (ListView) findViewById(R.id.listView2);
            Intent myIntent = new Intent(CommentActivity.this, CommentList.class);
            myIntent.putExtra("showId", showId);
            startActivity(myIntent);
        }
    }

    public void initializeVideoView() {
        //initialize the videoView
        myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
            }
        });
    }

    public void setVideoPath() {
        try {
            int raw_id = getResources().getIdentifier(clickedShow.getShow_trailer_location(), "raw", getPackageName());
            String PATH = "android.resource://" + getPackageName() + "/" + raw_id;
            myVideoView.setMediaController(mediaControls);

            if (PATH != null)
                myVideoView.setVideoPath(PATH);
                //myVideoView.setVideoURI(Uri.parse("http://larytech.com/site/empire_trailer.mp4"));
            else
                //myVideoView.setVideoURI(Uri.parse("http://larytech.com/site/empire_trailer.mp4"));
                myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.big_bang_trailer));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

    }

    public void playVideo() {
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
    }

    public void dialogWhileVideoLoading() {
        //create a progess bar while the video is loading
        progressDialog = new ProgressDialog(CommentActivity.this);
        //set a title for the progress bar
        progressDialog.setTitle("Getting Content from server...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void actionBarSetup() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.live_comment);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void sendCommentButton() {
        int screen_orientation = this.getResources().getConfiguration().orientation;
        if (screen_orientation == Configuration.ORIENTATION_PORTRAIT) {
            final EditText input = (EditText) findViewById(R.id.cment_inputText);
            send = (ImageButton) findViewById(R.id.sendButton);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!input.getText().equals("")) {
                        String cment = input.getText().toString().trim();
                        String name = loginUser.getUsername();
                        Comment c = new Comment(cment, name, new Date(), showId);
                        GlobalSettings.hideKeyboard(CommentActivity.this);
                        //Global.cmAdapter.add(c);
                        Global.txt.setVisibility(View.GONE);
                        input.setText("");
                        input.clearFocus();
                        Global.chatSettings.sendMessage(clickedShow.getName(), cment, showId);
                        //c.save();
                    }
                }
            });
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
        return actionBarActions(item);
    }

    public boolean actionBarActions(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.auto_scroll:
                autoScroll();
                break;
            case R.id.unsubscribe:
                unsubscribe();
                break;
            case R.id.comment_bt:
                Intent i = new Intent(this, WriteComment.class);
                i.putExtra("showId", showId);
                i.putExtra("type", 0);
                startActivityForResult(i, 2);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.expand:
                if (myVideoView.getVisibility() == View.GONE)
                    myVideoView.setVisibility(View.VISIBLE);
                else
                    myVideoView.setVisibility(View.GONE);
                break;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void autoScroll() {
        final ListView list = Global.lsView;
        CommentAdapter commentAdapter = Global.cmAdapter;
        list.setAdapter(commentAdapter);
        list.setSmoothScrollbarEnabled(true);

        if (isScrolling) {
            thread = new ScrollThread();
            list.post(thread);
            scrollSpeedCount++;
            if (scrollSpeedCount == 5)
                isScrolling = false;
            Toast.makeText(this, "Scrolling Speed: " + scrollSpeedCount, Toast.LENGTH_SHORT).show();
        } else {
            --scrollSpeedCount;
            try {
                waitTimeList.get(scrollSpeedCount).onFinish();
                waitTimeList.remove(scrollSpeedCount);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } finally {
                Toast.makeText(this, "Scrolling Speed: " + scrollSpeedCount, Toast.LENGTH_SHORT).show();

                if (scrollSpeedCount == 0) {
                    isScrolling = true;
                    list.scrollTo(0, 0);
                }
                System.out.println("Index " + scrollSpeedCount);
            }
        }
    }

    public void unsubscribe() {
        new AlertDialog.Builder(this)
                .setTitle("Lock This Show")
                .setMessage("You won't be able to comment\n Are you sure?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long uId = loginUser.getUserId();
                        ShowQueries.getUserShowById(uId, showId).delete();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class ScrollThread implements Runnable {
        final long totalScrollTime = Long.MAX_VALUE;
        final int scrollPeriod = 1500;
        final int heightToScroll = 20;
        final ListView list = Global.lsView;


        @Override
        public void run() {
            CountDownTimer waitTimer = new CountDownTimer(totalScrollTime, scrollPeriod) {
                public void onTick(long millisUntilFinished) {
                    /*if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1
                            && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
                        list.smoothScrollToPosition(0);
                    }*/
                    list.scrollBy(0, heightToScroll);
                }
                public void onFinish() {
                    this.cancel();
                }
            }.start();
            waitTimeList.add(waitTimer);
        }
    }
}
