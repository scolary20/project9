package scolabs.com.tennine.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import scolabs.com.tennine.CommentActivity;
import scolabs.com.tennine.R;
import scolabs.com.tennine.model.Show;

/**
 * Created by scolary on 1/30/2016.
 */
public class CommentList extends FragmentActivity
{
    private ArrayList<Show> showList;
    ShowAdapter showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadShows();
        ListView list = (ListView) findViewById(R.id.listView);
        showAdapter = new ShowAdapter();
        list.setAdapter(showAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {

            }
        });
    }

    private void loadShows() {
        showList = new ArrayList<Show>();
        Show sw = new Show("How to get awa with murder", "s3E11", "netflix");
        sw.setShow_length(40);
        showList.add(sw);
        showList.add(new Show("Empire", "s2E10", "hulu network"));
        showList.add(new Show("Empire", "s2E10", "Mnet"));
        showList.add(new Show("Empire", "s2E10", "Mnet"));
        showList.add(new Show("Empire", "s2E10", "netflix"));
        showList.add(new Show("Empire", "s2E10", "Mnet"));
    }

    /**
     * The Class CutsomAdapter is the adapter class fo"cts ListView. The
     * currently implementation of this adapter simply display static dummy
     * contents. You need to write the code for displaying actual contents.
     */
    private class ShowAdapter extends BaseAdapter {

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return showList.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Show getItem(int arg0) {
            return showList.get(arg0);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            if (v == null)
                v = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.show_list_item, null);

            Show c = getItem(pos);
            TextView lbl = (TextView) v.findViewById(R.id.show_name);
            Chronometer chronometer = (Chronometer) v.findViewById(R.id.chronometer);
            lbl.setText(c.getName());
            lbl.append(" " + c.getSeason());

            lbl = (TextView) v.findViewById(R.id.network_name);
            lbl.setText(c.getNetwork());

            if (c.getNetwork().equalsIgnoreCase("netflix")) {
                Drawable img = getResources().getDrawable(R.drawable.up_rating);
                img.setBounds(0, 0, 62, 62);
                lbl = (TextView) v.findViewById(R.id.rating_arrow);
                lbl.setCompoundDrawables(img, null, null, null);
                lbl.refreshDrawableState();
                //long minutes = c.getAiring_date().getMinutes();
                //chronometer.setBase(SystemClock.elapsedRealtime() - (minutes * 60000 + 0 * 1000));
                //long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.start();
            } else {
                Drawable img = getResources().getDrawable(R.drawable.down_rating);
                img.setBounds(0, 0, 62, 62);
                lbl = (TextView) v.findViewById(R.id.rating_arrow);
                lbl.setCompoundDrawables(img, null, null, null);
                lbl.refreshDrawableState();
                chronometer.stop();
                chronometer.setText("off-air");
            }

            return v;
        }
    }
}
