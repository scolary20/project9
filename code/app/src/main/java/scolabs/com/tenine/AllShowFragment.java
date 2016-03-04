package scolabs.com.tenine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.utils.Settings;


/**
 * Created by scolary on 3/1/2016.
 */
public class AllShowFragment extends Activity {
    private ArrayList<Show> showList;
    private ArrayList<Show> allShows;
    private ArrayList<Show> myShowList;
    private AllShowAdapter allShowAdapter;
    private boolean desc_clicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allshow_layout);
        new LoadShows().execute("");
    }


    private class AllShowAdapter extends ArrayAdapter {

        protected Context mContext;
        protected int layoutResourceId;
        protected ArrayList<Show> data;

        public AllShowAdapter(Context mContext, int layoutResourceId, ArrayList<Show> data) {
            super(mContext, layoutResourceId, data);
            this.mContext = mContext;
            this.layoutResourceId = layoutResourceId;
            this.data = data;
        }

        @Override
        public int getCount() {
            return showList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Show getItem(int arg0) {
            return showList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup parent) {

            View listItem;
            final Show show = showList.get(pos);
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItem = inflater.inflate(layoutResourceId, parent, false);
            final TextView desc_view = (TextView) listItem.findViewById(R.id.show_description);
            final ImageButton view_btn = (ImageButton) listItem.findViewById(R.id.desc_btn);
            final Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.image_animation);
            final ImageButton add_show = (ImageButton) listItem.findViewById(R.id.unlock_btn);
            final ImageView showImg = (ImageView) listItem.findViewById(R.id.show_image);
            final Drawable d;

            Typeface type = Typeface.createFromAsset(AllShowFragment.this.getAssets(), "bariol.ttf");
            Typeface type_bold = Typeface.create(type, Typeface.BOLD_ITALIC);
            TextView showName = (TextView) listItem.findViewById(R.id.show_name);
            showName.setTypeface(type_bold);
            showName.setText(show.getName());

            InputStream ims = null;
            try {
                ims = mContext.getAssets().open(show.getShow_img_location());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (ims != null) {
                d = Drawable.createFromStream(ims, null);
                showImg.setImageDrawable(d);
            } else {
                d = getContext().getResources().getDrawable(R.drawable.show_image_default);
                showImg.setImageDrawable(d);
            }


            view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!desc_clicked) {

                        desc_view.setVisibility(desc_view.VISIBLE);
                        Typeface type = Typeface.createFromAsset(AllShowFragment.this.getAssets(), "bariol.ttf");
                        desc_view.setText(R.string.login_top_message);
                        desc_view.setTypeface(type);
                        desc_view.setAnimation(bounce);
                        desc_clicked = true;
                    } else {
                        desc_view.setVisibility(desc_view.GONE);
                        desc_clicked = false;
                    }
                }
            });

            add_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long id = Settings.getLoginUser().getId();
                    new UserShow(id, show.getShowId()).save();
                    myShowList.add(show);
                    Show[] arrShow = {show};
                    EventBus.getDefault().post(arrShow);
                    Toast.makeText(mContext, "Show successfully added!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return listItem;
        }

    }

    private class LoadShows extends AsyncTask<String, Void, String> {
        private TextView feedback;
        private ProgressBar progressBar;

        @Override
        protected String doInBackground(String... params) {
            allShows = ShowQueries.getAllShows();
            myShowList = ShowQueries.getMyShows();
            showList = allShows;
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            final Button tab1 = (Button) findViewById(R.id.tab1);
            final Button tab2 = (Button) findViewById(R.id.tab2);
            final ImageButton tab0 = (ImageButton) findViewById(R.id.tab0);
            final Drawable button_bgd = tab2.getBackground();
            desc_clicked = false;


            ListView list = (ListView) findViewById(R.id.allshow_list);
            list.setAdapter(allShowAdapter);
            allShowAdapter = new AllShowAdapter(AllShowFragment.this, R.layout.allshow_item, showList);
            list.setAdapter(allShowAdapter);
            allShowAdapter.setNotifyOnChange(true);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {

                }
            });

            tab0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // v.setEnabled(false);
                    tab2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_background4));
                    tab1.setBackgroundDrawable(button_bgd);
                    showList = null;
                    showList = allShows;
                    allShowAdapter.notifyDataSetChanged();
                    Log.i("Click button", "clicked 1 !!!");
                }
            });

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tab1.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_background4));
                    tab2.setBackgroundDrawable(button_bgd);
                    showList = null;
                    showList = myShowList;
                    allShowAdapter.notifyDataSetChanged();
                    Log.i("Click button", "clicked!!!");
                }
            });

            progressBar.setVisibility(View.GONE);
            if (showList.size() > 0) {
                feedback.setVisibility(View.GONE);
            } else
                feedback.setText("No shows...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            feedback = (TextView) findViewById(R.id.cmt_feedback);
            progressBar.setVisibility(View.VISIBLE);
            feedback.setText("Loading Shows...");
            feedback.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}