package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    int default_color;

    public ShowAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        Show c = data.get(pos);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.show_name = (TextView) convertView.findViewById(R.id.show_name);
            holder.chronometer = (Chronometer) convertView.findViewById(R.id.chronometer);
            holder.num_watching = (TextView) convertView.findViewById(R.id.num_watching);
            holder.num_comments = (TextView) convertView.findViewById(R.id.num_comment);
            holder.network_name = (TextView) convertView.findViewById(R.id.network_name);
            holder.show_image = (ImageView) convertView.findViewById(R.id.show_image);
            holder.img = getContext().getResources().getDrawable(R.drawable.up_rating);
            holder.sImage = getContext().getResources().getDrawable(R.drawable.breaking);
            holder.show_default_img = getContext().getResources().getDrawable(R.drawable.empire);
            holder.rating_arrow = (TextView) convertView.findViewById(R.id.rating_arrow);
            holder.d_img = getContext().getResources().getDrawable(R.drawable.down_rating);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        NumberFormat fmt = NumberFormat.getInstance();

        holder.show_name.setText(c.getName());
        holder.show_name.append(" " + c.getSeason());
        holder.network_name.setText(c.getNetwork());
        holder.num_comments.setText(fmt.format(c.getNum_comment()));
        holder.num_watching.setText(fmt.format(c.getNum_watching()));
        holder.img.setBounds(0, 0, 62, 62);

        if (c.getRating_arrow() == 1)
            holder.rating_arrow.setCompoundDrawables(holder.img, null, null, null);
        else
            holder.rating_arrow.setCompoundDrawables(holder.d_img, null, null, null);
        holder.rating_arrow.refreshDrawableState();
        time_show_handler(holder, c); //Handling starting show time

        if (c.getNetwork().equalsIgnoreCase("netflix")) {
            holder.show_image.setImageDrawable(holder.sImage);
        } else {
            holder.show_image.setImageDrawable(holder.show_default_img);
        }
        return convertView;
    }

    public void time_show_handler(ViewHolder holder, Show c) {
        if (c.getAiring_date() != null) {
            final Date d = new Date();
            long minutes = d.getMinutes();
            long seconds = d.getSeconds();



            //Calculating End Date
            long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
            int GRACE_PERIOD = 5;

            Calendar cal = Calendar.getInstance();
            cal.setTime(c.getAiring_date());
            long t = cal.getTimeInMillis();
            Date end_show = new Date(t + (c.getShow_length() * ONE_MINUTE_IN_MILLIS));
            Date today = new Date();
            int show_day = cal.get(Calendar.DAY_OF_WEEK);
            cal.setTime(today);
            int today_day = cal.get(Calendar.DAY_OF_WEEK);
            long min_progress = today.getTime() - c.getAiring_date().getTime();
            holder.chronometer.setBase(SystemClock.elapsedRealtime() - (min_progress));

            Boolean check_date = show_day == today_day;
            Boolean ch = (today.after(c.getAiring_date()) && today.before(end_show));
            Log.e("end show", end_show.toString());
            if (check_date && ch) {

                holder.chronometer.start();

            } else {
                holder.chronometer.stop();
                holder.chronometer.setText("off-air");
            }

            cal.setTime(end_show);
            long et = cal.getTimeInMillis();
            Date grace_period = new Date(et + GRACE_PERIOD * ONE_MINUTE_IN_MILLIS);
            default_color = holder.chronometer.getDrawingCacheBackgroundColor();

            if (today.after(end_show) && today.before(grace_period))
                holder.chronometer.setBackgroundColor(Color.parseColor("#ffb2b2"));
            else
                holder.chronometer.setBackgroundColor(default_color);

            if (today.before(c.getAiring_date()) && today.after(end_show)) {
                holder.chronometer.setBackgroundColor(default_color);
                holder.chronometer.stop();
                holder.chronometer.setText("off-air");
            }
        } else
        {
            holder.chronometer.setBackgroundColor(default_color);
            holder.chronometer.stop();
            holder.chronometer.setText("off-air");
        }
    }

    static class ViewHolder {
        TextView show_name;
        Chronometer chronometer;
        TextView num_watching;
        TextView num_comments;
        ImageView show_image;
        Drawable img;
        Drawable sImage;
        Drawable show_default_img;
        Drawable d_img;
        TextView rating_arrow;
        TextView network_name;
    }
}
