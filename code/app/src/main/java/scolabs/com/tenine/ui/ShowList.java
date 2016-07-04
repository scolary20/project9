package scolabs.com.tenine.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import scolabs.com.tenine.AllShowFragment;
import scolabs.com.tenine.CommentActivity;
import scolabs.com.tenine.Login;
import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.remoteOperations.PullShowData;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 1/29/2016.
 */

public class ShowList extends Fragment {
    private ShowAdapter showAdapter;
    private ListView listView;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("show_started")) {
                Log.e("Receiver ", "Working !!!");
                showAdapter.notifyDataSetChanged();
                listView.refreshDrawableState();
            }
            if (action.equals("loading_finished")) {
                showAdapter.refreshAndAddShowList(ShowQueries.getShows());
                showAdapter.notifyDataSetChanged();
                listView.refreshDrawableState();
            }
        }
    };
    private ImageView mImageViewFilling;
    private ArrayList<Show> showList = new ArrayList<>();
    private LinearLayout view;
    private View show_UI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show_UI = inflater.inflate(R.layout.shows_ui, container, false);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("show_started");
        getActivity().registerReceiver(receiver, filter);
        setHasOptionsMenu(true);
        listView = (ListView) show_UI.findViewById(R.id.listView);
        showNoShowMessage();
        new PullShows().execute();
        Global.showAdapterCheckFore = showAdapter; //Enable automatic update of show list
        return show_UI;
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

    public void showNoShowMessage() {
        view = (LinearLayout) show_UI.findViewById(R.id.linearAnim);
        view.setVisibility(View.GONE);

        if (showList != null && showList.size() < 1) {
            TextView tx = (TextView) show_UI.findViewById(R.id.no_show_message);
            tx.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_marks));
            view.setVisibility(View.VISIBLE);
            mImageViewFilling = (ImageView) show_UI.findViewById(R.id.imageview_animation_list_filling);
            AnimationDrawable up = ((AnimationDrawable) mImageViewFilling.getBackground());
            up.start();
        }
    }

    class PullShows extends AsyncTask<String, Void, ArrayList<Show>> {
        @Override
        protected ArrayList<Show> doInBackground(String... urls) {
            showList = ShowQueries.getShows();
            if (showList.size() == 0)
                return (showList = new PullShowData(getActivity()).getMyShows(GlobalSettings.getLoginUser().getUserId(), 1));
            else
                return showList;
        }

        @Override
        protected void onPostExecute(final ArrayList<Show> shows) {
            showAdapter = new ShowAdapter(getActivity(), R.layout.show_list_item, shows);
            listView.setAlwaysDrawnWithCacheEnabled(true);
            listView.setAdapter(showAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {
                    Intent myIntent = new Intent(getActivity(), CommentActivity.class);
                    Global.showId = showList.get(pos).getShowId();
                    Global.show = showList.get(pos);
                    startActivityForResult(myIntent, 1);
                }
            });
            Log.d("Today's show size feed", "" + showList.size());
            showAdapter.notifyDataSetChanged();
            showNoShowMessage();
            Global.showListView = listView;
        }
    }
}

