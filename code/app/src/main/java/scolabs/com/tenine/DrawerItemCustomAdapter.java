package scolabs.com.tenine;

import android.app.Activity;
import android.content.Context;
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

import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 1/27/2016.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<Show>
{
    Context mContext;
    int layoutResourceId;
    ArrayList<Show> data = null;
    ProgressBar progressBar;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        Show show = data.get(position);
        Drawable d;


        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
        TextView length = (TextView) listItem.findViewById(R.id.time_progress);
        length.setText("" + show.getShow_length() + " min");
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

        progressBar = (ProgressBar) listItem.findViewById(R.id.progressBar);
        final int show_length = (int) (show.getShow_length() * 60 * 1000);
        progressBar.setMax(show_length);
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while (jumpTime < show_length) {
                    try {
                        sleep(1000);
                        jumpTime += 1000;
                        progressBar.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        return listItem;
    }


}
