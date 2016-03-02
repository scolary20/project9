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

import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.utils.Settings;


/**
 * Created by scolary on 3/1/2016.
 */
public class AllShowFragment extends Fragment {
    private ArrayList<Show> showList;
    private ArrayList<Show> myShowList;
    private AllShowAdapter allShowAdapter;
    private boolean desc_clicked;
    private View layoutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.allshow_layout, null);
        new LoadShows().execute("");
        return layoutView;
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

            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "bariol.ttf");
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
                        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "bariol.ttf");
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
            showList = ShowQueries.getShows();
            myShowList = ShowQueries.getMyShows();

            Log.i("Number of Elements", "" + showList.size());
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            Button tab1 = (Button) layoutView.findViewById(R.id.tab1);
            Button tab2 = (Button) layoutView.findViewById(R.id.tab2);
            desc_clicked = false;


            ListView list = (ListView) layoutView.findViewById(R.id.allshow_list);
            list.setAdapter(allShowAdapter);
            allShowAdapter = new AllShowAdapter(getActivity(), R.layout.allshow_item, showList);
            list.setAdapter(allShowAdapter);
            allShowAdapter.setNotifyOnChange(true);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {

                }
            });

            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // v.setEnabled(false);
                    showList = ShowQueries.getShows();
                    allShowAdapter.notifyDataSetChanged();
                    Log.i("Click button", "clicked 1 !!!");
                }
            });

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            progressBar = (ProgressBar) layoutView.findViewById(R.id.progressBar);
            feedback = (TextView) layoutView.findViewById(R.id.cmt_feedback);
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