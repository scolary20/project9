package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.R;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.GlobalSettings;

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
            holder.sImage = null;
            holder.show_default_img = getContext().getResources().getDrawable(R.drawable.show_image_default);
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
        InputStream ims = null;
        try {
            ims = mContext.getAssets().open(c.getShow_img_location());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ims != null) {
            holder.sImage = Drawable.createFromStream(ims, null);
            holder.show_image.setImageDrawable(holder.sImage);
        } else
            holder.show_image.setImageDrawable(holder.show_default_img);

        if (c.getRating_arrow() == 1)
            holder.rating_arrow.setCompoundDrawablesWithIntrinsicBounds(holder.img, null, null, null);
        else
            holder.rating_arrow.setCompoundDrawablesWithIntrinsicBounds(holder.d_img, null, null, null);
        holder.rating_arrow.refreshDrawableState();
        time_show_handler(holder, c); //Handling starting show time


        return convertView;
    }


    public void time_show_handler(ViewHolder holder, Show c) {


        Object[] time_array = GlobalSettings.showTimeHandler(c);
        if (time_array != null) {
            final Date d = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(c.getAiring_date());
            //Calculating End Date
            long t = cal.getTimeInMillis();
            Date end_show = (Date) time_array[1];
            Date today = (Date) time_array[0];
            holder.chronometer.setBase((long) time_array[5]);

            Boolean check_date = (boolean) time_array[6];
            Boolean ch = ((boolean) time_array[7]);
            Log.e("end show", end_show.toString());
            if (check_date && ch) {

                holder.chronometer.start();

            } else {
                holder.chronometer.stop();
                holder.chronometer.setText("off-air");
            }

            cal.setTime(end_show);
            default_color = holder.chronometer.getDrawingCacheBackgroundColor();

            if (today.before(c.getAiring_date())) {
                cal.setTime(c.getAiring_date());
                int format = cal.get(Calendar.AM_PM);
                String am_pm = format == 0 ? " am" : " pm";
                holder.chronometer.setBackgroundColor(default_color);
                holder.chronometer.stop();
                holder.chronometer.setText("" + (cal.get(Calendar.HOUR) < 10 ? "0" +
                        cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                        + (cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + am_pm);
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
