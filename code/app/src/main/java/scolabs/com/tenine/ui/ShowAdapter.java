package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.R;
import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 2/9/2016.
 */
public class ShowAdapter extends ArrayAdapter {
    Context mContext;
    int layoutResourceId;
    ArrayList<Show> data = null;

    public ShowAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        Log.e("Inside show adapter", "-------");


    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Log.e("Inside show View", "-------");
        final View listItem;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        final Show c = data.get(pos);
        TextView lbl = (TextView) listItem.findViewById(R.id.show_name);
        Chronometer chronometer = (Chronometer) listItem.findViewById(R.id.chronometer);
        TextView num_watching = (TextView) listItem.findViewById(R.id.num_watching);
        final TextView num_comments = (TextView) listItem.findViewById(R.id.num_comment);

        lbl.setText(c.getName());
        lbl.append(" " + c.getSeason());

        lbl = (TextView) listItem.findViewById(R.id.network_name);
        lbl.setText(c.getNetwork());

        if (c.getNetwork().equalsIgnoreCase("netflix")) {
            ImageView show_image = (ImageView) listItem.findViewById(R.id.show_image);
            Drawable img = getContext().getResources().getDrawable(R.drawable.up_rating);
            Drawable sImage = getContext().getResources().getDrawable(R.drawable.breaking);
            show_image.setImageDrawable(sImage);
            img.setBounds(0, 0, 62, 62);
            lbl = (TextView) listItem.findViewById(R.id.rating_arrow);
            lbl.setCompoundDrawables(img, null, null, null);
            lbl.refreshDrawableState();
            final Date d = new Date();
            long minutes = d.getMinutes();
            long seconds = d.getSeconds();
            long elapsedTime = SystemClock.elapsedRealtime() - (4 * 60000);
            chronometer.setBase(SystemClock.elapsedRealtime() - (minutes * 60000 + seconds * 1000));
            //SystemClock.elapsedRealtime() - chronometer.getBase();
            Log.e("Elapsed Time ", "" + chronometer.getBase());
            Log.e("Show End", "" + elapsedTime);
            Log.e("Show start", " " + chronometer.getBase());
            boolean bool = chronometer.getBase() < elapsedTime;
            Log.e("Compare ", "" + bool);
            Log.e("Minutes", " " + minutes);
            chronometer.start();

            new CountDownTimer(c.getShow_length()*60000, 1000) {

                public void onTick(long millisUntilFinished) {

                    int seconds = 60 - new Date().getSeconds();
                    num_comments.setText("Minutes remaining: " + millisUntilFinished / 60000);
                }

                public void onFinish() {
                    num_comments.setText("done!");
                }
            }.start();


        } else {
            Drawable img = getContext().getResources().getDrawable(R.drawable.down_rating);
            img.setBounds(0, 0, 62, 62);
            lbl = (TextView) listItem.findViewById(R.id.rating_arrow);
            lbl.setCompoundDrawables(img, null, null, null);
            lbl.refreshDrawableState();
            chronometer.stop();
            chronometer.setText("off-air");
        }


        return listItem;
    }

}
