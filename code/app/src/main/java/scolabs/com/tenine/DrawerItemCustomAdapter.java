package scolabs.com.tenine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import scolabs.com.tenine.model.Global;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 1/27/2016.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<Show> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Show> data = null;
    private ArrayList<Thread> threads;
    private final long ONE_MINUTE_IN_MILLIS = 60000;
    private DrawerItemCustomAdapter adapter;
    boolean deleted = false;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        threads = Global.drawerShowThreads;
        adapter = this;
        EventBus.getDefault().register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        final Show show = data.get(position);
        Drawable d;


        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
        TextView length = (TextView) listItem.findViewById(R.id.time_progress);

        length.setText("" + show.getShow_length() + " min");
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "simplifica.ttf");
        Typeface font = Typeface.create(type, Typeface.BOLD);
        textViewName.setTypeface(font);
        textViewName.setText(show.getName());


        InputStream ims = null;
        try {
            ims = mContext.getAssets().open(show.getShow_img_location());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ims != null) {
            d = Drawable.createFromStream(ims, null);
            imageViewIcon.setImageDrawable(d);
        } else {
            d = getContext().getResources().getDrawable(R.drawable.show_image_default);
            imageViewIcon.setImageDrawable(d);
        }

        final ProgressBar progressBar = (ProgressBar) listItem.findViewById(R.id.progressBar);
        final int show_length = (int)/*(show.getShow_length()*/ (1 * ONE_MINUTE_IN_MILLIS);
        progressBar.setMax(show_length);

        if (threads == null || threads.size() == 0) {
            myShowProgressHander(show_length, show, position, progressBar);
            System.gc();
        } else {
            boolean exist = false;
            for (Thread t : threads) {
                if (t.getName().equals("" + show.getShowId())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                myShowProgressHander(show_length, show, position, progressBar);
                System.gc();
            }
        }

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mContext, CommentActivity.class);
                Global.showId = show.getShowId();
                mContext.startActivity(myIntent);
            }
        });

        return listItem;
    }

    public void myShowProgressHander(final int show_length, final Show show, final int position, final ProgressBar progressBar) {
        Thread t = new Thread(new Runnable() {
            long actual_start = (long) (Settings.showTimeHandler(show)[4]);
            int jumpTime = 0;//(int) actual_start;

            @Override
            public void run() {
                while (jumpTime < show_length) {
                    try {
                        Thread.currentThread().sleep(1000);
                        jumpTime += 1000;
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(jumpTime);
                            }
                        });

                        if (jumpTime == show_length) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.remove(show);
                                    EventBus.getDefault().post(show);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        });


        t.setName("" + show.getShowId());
        threads.add(t);
        t.start();
    }

    @Subscribe
    public void onEvent(Show[] arrShow) {
        data.add(arrShow[0]);
        adapter.notifyDataSetChanged();
    }
}
