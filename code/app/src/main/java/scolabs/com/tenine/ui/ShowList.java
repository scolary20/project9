package scolabs.com.tenine.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import scolabs.com.tenine.AllShowFragment;
import scolabs.com.tenine.CommentActivity;
import scolabs.com.tenine.Login;
import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 1/29/2016.
 */

public class ShowList extends Fragment {
    private ShowAdapter showAdapter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("show_started")) {
                Log.e("Receiver ", "Working !!!");
                showAdapter.notifyDataSetChanged();
            }
        }
    };
    private ImageView mImageViewFilling;
    private ArrayList<Show> showList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.shows_ui, container, false);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("show_started");
        getActivity().registerReceiver(receiver, filter);

        loadShows();
        ListView list = (ListView) v.findViewById(R.id.listView);
        showAdapter = new ShowAdapter(getActivity(), R.layout.show_list_item, showList);
        list.setAdapter(showAdapter);
        list.setAlwaysDrawnWithCacheEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                Intent myIntent = new Intent(getActivity(), CommentActivity.class);
                Global.showId = showList.get(pos).getShowId();
                Global.show = showList.get(pos);
                startActivityForResult(myIntent, 1);
            }
        });

        setHasOptionsMenu(true);
        LinearLayout view = (LinearLayout) v.findViewById(R.id.linearAnim);
        view.setVisibility(View.GONE);

        if (showList != null && showList.size() < 1) {
            TextView tx = (TextView) v.findViewById(R.id.no_show_message);
            tx.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_marks));
            view.setVisibility(View.VISIBLE);
            mImageViewFilling = (ImageView) v.findViewById(R.id.imageview_animation_list_filling);
            AnimationDrawable up = ((AnimationDrawable) mImageViewFilling.getBackground());
            up.start();
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.unlock_show_action) {
            Intent i = new Intent(getActivity(), AllShowFragment.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.refresh_app) {
            Global.drawerShowThreads.removeAll(Global.drawerShowThreads);
            Intent i = new Intent(getActivity(), Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Toast.makeText(getActivity().getApplicationContext(), "refreshed!", Toast.LENGTH_SHORT);
        }


        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(Show show) {
        showAdapter.remove(show);
        showAdapter.notifyDataSetChanged();
        Log.e("Show has been deleted", " " + show.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("show_started");
        getActivity().registerReceiver(receiver, filter);
    }


    private void loadShows() {

        // Convert string to date
        /*SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String strdate2 = "16-03-2016 03:30:00";
        String strdate3 = "16-03-2016 03:20:00";
        String strdate4 = "16-03-2016 03:55:00";
        String strdate5 = "16-03-2016 03:30:00";
        String strdate6 = "16-03-2016 04:00:00";
        String strdate7 = "16-03-2016 04:30:00";
        String strdate8 = "16-03-2016 05:00:00";
        Date newdate, newdate3, newdate4, newdate5, newdate7, newdate6, newdate8;
        newdate = null;
        newdate3 = null;
        newdate = null;
        newdate4 = null;
        newdate5 = null;
        newdate6 = null;
        newdate7 = null;
        newdate8 = null;
        Date nd = null;
        try {
            newdate = dateformat2.parse(strdate2);
            newdate3 = dateformat2.parse(strdate3);
            newdate4 = dateformat2.parse(strdate4);
            newdate5 = dateformat2.parse(strdate5);
            newdate6 = dateformat2.parse(strdate6);
            newdate7 = dateformat2.parse(strdate7);
            nd = dateformat2.parse(strdate3);
            newdate8 = dateformat2.parse(strdate8);
            System.out.println(newdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        showList = new ArrayList<>();

        Show sw = new Show("Breaking bad", "s5E22", "netflix");
        sw.setShow_length(10);
        sw.setNum_comment(5426);
        sw.setNum_watching(4126345L);
        sw.setAiring_date(newdate);
        sw.setShow_img_location("breaking.png");
        sw.setShow_trailer_location("modern_trailer");
        sw.save();
        showList.add(sw);

        Show tr = new Show("Empire", "s3E3", "Hulu network");
        tr.setAiring_date(nd);
        tr.setNum_watching(50500);
        tr.setNum_comment(3052445);
        tr.setShow_length(9);
        tr.setShow_img_location("empire.png");
        tr.save();
        showList.add(tr);
        tr = new Show("How to get away with murder", "s4E2", "Mtv");
        tr.setShow_length(30);
        tr.setNum_comment(3);
        tr.setNum_watching(500);
        tr.setAiring_date(newdate3);
        tr.setShow_img_location("away.png");
        tr.setShow_trailer_location("empire_trailer");
        tr.save();
        showList.add(tr);

        tr = new Show("bang", "s4E3", "Mtv Kids");
        tr.setShow_length(55);
        tr.setNum_comment(45);
        tr.setNum_watching(600);
        tr.setAiring_date(newdate4);
        tr.setShow_img_location("bang.png");
        tr.setShow_trailer_location("big_bang_trailer");
        tr.save();
        showList.add(tr);

        tr = new Show("Bein mary Jane", "s2E13", "Bet");
        tr.setShow_length(25);
        tr.setNum_comment(450);
        tr.setNum_watching(45);
        tr.setAiring_date(newdate5);
        tr.setShow_img_location("mary.png");
        tr.save();
        showList.add(tr);

        tr = new Show("modern", "s16E13", "HBO network");
        tr.setShow_length(20);
        tr.setNum_comment(4500);
        tr.setNum_watching(45009983);
        tr.setAiring_date(newdate8);
        tr.setShow_trailer_location("modern_trailer");
        tr.setShow_img_location("modern.png");
        tr.save();
        showList.add(tr);

        tr = new Show("Real husband of Hollywood", "s20E13", "Bet");
        tr.setShow_length(60);
        tr.setNum_comment(1004);
        tr.setNum_watching(4598);
        tr.setAiring_date(newdate7);
        tr.setShow_img_location("real.jpg");
        tr.save();
        showList.add(tr);

        tr = new Show("Desperate Housewifes", "s4E1", "Canal+");
        tr.setShow_length(10);
        tr.setNum_comment(2550);
        tr.setNum_watching(989);
        tr.setAiring_date(newdate6);
        tr.setShow_img_location("desperate.png");
        tr.save();
        showList.add(tr);

        tr = new Show("Game of thrones", "s5E1", "Fox network");
        tr.setShow_length(9);
        tr.setNum_comment(25450);
        tr.setNum_watching(9289);
        tr.setAiring_date(newdate6);
        tr.setShow_trailer_location("game_trailer");
        tr.setShow_img_location("game.png");
        tr.save();
        showList.add(tr);*/

        showList = ShowQueries.getShows();
        Log.e("Show list size ",String.valueOf(showList.size()));
    }
}
