package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.DrawerItemCustomAdapter;
import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 2/9/2016.
 */
public class ShowAdapter extends ArrayAdapter {
    public static final String BROADCAST = "com.scolabs.tenine.android.action.broadcast";
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Show> data = null;
    private int default_color;
    private View convertView = null;
    private ArrayList<Object> showAndViewList;
    private ViewHolder holder;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Long showId = intent.getLongExtra("showId",-999);
            if (action.equals("increment_comment")) {
                new UpdateCommentsNumb().execute(showId);
            }
        }
    };

    public ShowAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        IntentFilter filter = new IntentFilter();
        filter.addAction("increment_comment");
        getContext().registerReceiver(receiver,filter);

    }

    @Override
    public View getView(int pos, View aConvertView, ViewGroup parent) {

        showAndViewList = new ArrayList<>();
        final Show c = data.get(pos);
        convertView = aConvertView;
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
            Global.showViews.put(c.getShowId(),holder);
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
            new GlobalSettings(mContext);
            String img_name = c.getShow_img_location();
            Bitmap map = BitmapFactory.decodeFile(GlobalSettings.SHOWS_IMG_DIR + "/" + img_name);
            if (map != null) {
                holder.sImage = new BitmapDrawable(mContext.getResources(), map);
                holder.show_image.setImageDrawable(holder.sImage);
            } else {
                holder.show_image.setImageDrawable(holder.show_default_img);
            }
        } catch (Exception ex) {
            holder.show_image.setImageDrawable(holder.show_default_img);
            ex.printStackTrace();
        }

        if (c.getRating_arrow() == 1)
            holder.rating_arrow.setCompoundDrawablesWithIntrinsicBounds(holder.img, null, null, null);
        else
            holder.rating_arrow.setCompoundDrawablesWithIntrinsicBounds(holder.d_img, null, null, null);
        holder.rating_arrow.refreshDrawableState();
        time_show_handler(holder, c); //Handling starting show time

        showAndViewList.add(0,c);
        showAndViewList.add(1,convertView);

        return convertView;
    }


    class UpdateCommentsNumb extends AsyncTask<Long, Void, Long> {
        private long numComments;
        @Override
        protected Long doInBackground(Long... params) {
            long showId = params[0];
            long showNumComments = CommentQueries.getCommentCount(showId);
            ShowQueries.updateShowComment(showId,showNumComments);
            numComments = showNumComments;
            Log.i("ShowNumComments "," "+showNumComments);

            return showId;
        }

        @Override
        protected void onPostExecute(Long showId) {
            super.onPostExecute(showId);
            ViewHolder hold = Global.showViews.get(showId);

                hold.num_comments.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.bounce_marks));
                hold.num_comments.setText(String.valueOf(numComments));

            broadcastEvent("new_comment");
        }

    }

    public void broadcastEvent(String type) {
        Intent intent = new Intent(BROADCAST);
        intent.setAction(type);
        mContext.sendBroadcast(intent);
    }

    public BroadcastReceiver getBroadcastReceiver()
    {
        return receiver;
    }


    public void time_show_handler(ViewHolder holder, Show c) {


        Object[] time_array = GlobalSettings.showTimeHandler(c);
        if (time_array != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(c.getAiring_date()));
            //Calculating End Date
            Date end_show = (Date) time_array[1];
            Date today = (Date) time_array[0];
            holder.chronometer.setBase((long) time_array[5]);
            Boolean isShowAiring = ((boolean) time_array[7]);
            Log.e("end show", end_show.toString());
            if (isShowAiring) {
                holder.chronometer.start();
            } else {
                holder.chronometer.stop();
                holder.chronometer.setText("off-air");
            }

            cal.setTime(end_show);
            default_color = holder.chronometer.getDrawingCacheBackgroundColor();

            if (today.before(new Date(c.getAiring_date()))) {
                cal.setTime(new Date(c.getAiring_date()));
                int format = cal.get(Calendar.AM_PM);
                String am_pm = format == 0 ? " am" : " pm";
                holder.chronometer.setBackgroundColor(default_color);
                holder.chronometer.stop();
                holder.chronometer.setText("" + (cal.get(Calendar.HOUR) < 10 ? "0" +
                        cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR)) + ":"
                        + (cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE)) + am_pm);
            }
        } else {
            holder.chronometer.setBackgroundColor(default_color);
            holder.chronometer.stop();
            holder.chronometer.setText("off-air");
        }
    }

    public void refreshAndAddShowList(ArrayList<Show> shows) {
        this.addAll(shows);
        this.notifyDataSetChanged();
    }

    public ViewHolder getViewHolder()
    {
        return holder;
    }


    public static class ViewHolder {
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
