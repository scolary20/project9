package scolabs.com.tenine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 1/27/2016.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<Show> {
    private final long ONE_MINUTE_IN_MILLIS = 60000;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Show> data = null;
    private ArrayList<Thread> threads;
    private DrawerItemCustomAdapter adapter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("show_started")) {
                Log.e("Receiver ", "drawerItem received!!!");
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
            }
        }
    };

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        threads = Global.drawerShowThreads;
        adapter = this;
        EventBus.getDefault().register(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("show_started");
        mContext.registerReceiver(receiver, filter);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View listItem = convertView;
        final Show show = data.get(position);
        Drawable d;
        if (listItem == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItem = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
            holder.textViewName = (TextView) listItem.findViewById(R.id.textViewName);
            holder.length = (TextView) listItem.findViewById(R.id.time_progress);
            holder.type = Typeface.createFromAsset(mContext.getAssets(), "simplifica.ttf");
            holder.font = Typeface.create(holder.type, Typeface.BOLD);
            listItem.setTag(holder);
            holder.progressBar = null;
            holder.progressBar = (ProgressBar) listItem.findViewById(R.id.progressBar);
            holder.progressBar.setTag(show.getId());
            holder.show_default_img = getContext().getResources().getDrawable(R.drawable.show_image_default);
        } else
            holder = (ViewHolder) listItem.getTag();
        holder.length.setText("" + show.getShow_length() + " min");
        holder.textViewName.setTypeface(holder.font);
        holder.textViewName.setText(show.getName());

        try {
            new GlobalSettings(mContext);
            String img_name = show.getShow_img_location();
            Bitmap map = BitmapFactory.decodeFile(GlobalSettings.SHOWS_IMG_DIR + "/" + img_name);
            if (map != null) {
                holder.sImage = new BitmapDrawable(mContext.getResources(), map);
                holder.imageViewIcon.setImageDrawable(holder.sImage);
            } else {
                holder.imageViewIcon.setImageDrawable(holder.show_default_img);
            }
        } catch (Exception ex) {
            holder.imageViewIcon.setImageDrawable(holder.show_default_img);
            ex.printStackTrace();
        }

        final int show_length = (int) (show.getShow_length() * ONE_MINUTE_IN_MILLIS);
        holder.progressBar.setMax(show_length);

        if (threads == null || threads.size() == 0) {
            myShowProgressHander(show_length, show, position, holder.progressBar);
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
                myShowProgressHander(show_length, show, position, holder.progressBar);
                System.gc();
            }
        }

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mContext, CommentActivity.class);
                Global.show = show;
                mContext.startActivity(myIntent);
            }
        });

        return listItem;
    }

    public void myShowProgressHander(final int show_length, final Show show, final int position, final ProgressBar progressBar) {
        Thread t = new Thread(new Runnable() {
            long actual_start = (long) (GlobalSettings.showTimeHandler(show)[4]);
            int jumpTime = (int) actual_start;
            int length = show_length + 1000;

            @Override
            public void run() {
                while (jumpTime < length) {
                    try {
                        Thread.currentThread().sleep(1000);
                        jumpTime += 1000;
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar.getTag() == show.getId())
                                    progressBar.setProgress(jumpTime);
                            }
                        });
                        if (jumpTime > show_length) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    data.remove(data.get(position));
                                    //adapter.remove(show);
                                    EventBus.getDefault().post(show);
                                    adapter.notifyDataSetChanged();
                                    Log.e("Event finished", show.getName());
                                }
                            });
                            break;
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
        if ((boolean) GlobalSettings.showTimeHandler(arrShow[0])[6]) {
            if ((boolean) GlobalSettings.showTimeHandler(arrShow[0])[7]) {
                data.add(arrShow[0]);
                adapter.notifyDataSetChanged();
            }
        }
    }

    static class ViewHolder {
        ProgressBar progressBar;
        ImageView imageViewIcon;
        TextView textViewName;
        Drawable show_default_img;
        Drawable sImage;
        TextView length;
        Typeface font;
        Typeface type;
    }
}
