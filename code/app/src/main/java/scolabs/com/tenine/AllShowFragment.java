package scolabs.com.tenine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.remoteOperations.ShowMgt;
import scolabs.com.tenine.utils.GlobalSettings;


/**
 * Created by scolary on 3/1/2016.
 */
public class AllShowFragment extends Activity {
    InputStream ims = null;
    private ArrayList<Show> showList;
    private ArrayList<Show> allShows;
    private ArrayList<Show> myShowList;
    private AllShowAdapter allShowAdapter;
    private boolean desc_clicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allshow_layout);
        Context ctx = this;
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
            final ImageButton remove_show = (ImageButton) listItem.findViewById(R.id.remove_btn);
            final ImageView showImg = (ImageView) listItem.findViewById(R.id.show_image);
            final TextView latest_airing_view = (TextView) listItem.findViewById(R.id.latest_airing);
            final Drawable d;

            Typeface type = Typeface.createFromAsset(AllShowFragment.this.getAssets(), "bariol.ttf");
            Typeface type_bold = Typeface.create(type, Typeface.BOLD_ITALIC);
            TextView showName = (TextView) listItem.findViewById(R.id.show_name);
            showName.setTypeface(type_bold);
            showName.setText(show.getName());
            d = getContext().getResources().getDrawable(R.drawable.show_image_default);
            Date show_date = new Date(show.getAiring_date());
            DateFormat df = DateFormat.getDateTimeInstance();

            //Setting Color for the View: Airing view
            if (show_date.before(new Date())) {
                //latest_airing_view.setTextColor(getResources().getColor(R.color.button_material_light));
                latest_airing_view.setTextColor(Color.parseColor("#808080"));
                latest_airing_view.setText("Latest Episode Airing Date: \n" + df.format(show_date));
            } else {
                latest_airing_view.setTextColor(Color.parseColor("#258b87"));
                latest_airing_view.setText("Next Episode Airing Date: \n" + df.format(show_date));
            }

            try {
                //ims = mContext.getAssets().open(show.getShow_img_location());
                new GlobalSettings(mContext);
                String img_name = show.getShow_img_location();
                Bitmap map = BitmapFactory.decodeFile(GlobalSettings.SHOWS_IMG_DIR + "/" + img_name);
                if (map != null) {
                    //d = Drawable.createFromStream(ims, null);
                    //showImg.setImageDrawable(d);
                    showImg.setImageBitmap(map);
                } else {
                    showImg.setImageDrawable(d);
                }
            } catch (Exception ex) {
                showImg.setImageDrawable(d);
                Log.i("No image found", "No image found for this show");
            }

            view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!desc_clicked) {

                        desc_view.setVisibility(View.VISIBLE);
                        Typeface type = Typeface.createFromAsset(AllShowFragment.this.getAssets(), "bariol.ttf");
                        desc_view.setText(show.getComment_content());
                        desc_view.setTypeface(type);
                        desc_view.setAnimation(bounce);
                        desc_clicked = true;
                    } else {
                        desc_view.setVisibility(View.GONE);
                        desc_clicked = false;
                    }
                }
            });

            add_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long id = GlobalSettings.getLoginUser().getUserId();
                    int attempts = 3;
                    boolean success = false;
                    boolean join_success = false;
                    if (GlobalSettings.checkConnection(getBaseContext())) {
                        while (!success && attempts > 0) {

                            success = new ShowMgt().followShow(id, show.getShowId()); // Response from Server
                            join_success = new ShowMgt().joinShowRoom(show.getName());
                            attempts--; // Decrement attempts if failure
                        }
                        if (success && join_success) {
                            new UserShow(id, show.getShowId()).save(); // cache the information
                            myShowList.add(show);
                            Show[] arrShow = {show};
                            EventBus.getDefault().post(arrShow);
                            Toast.makeText(mContext, "Show successfully added!!!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mContext, "Error Occured, Try Later!!!", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(mContext, "No Internet:\nCheck Your Internet Connection", Toast.LENGTH_LONG).show();
                }
            });

            remove_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final long id = GlobalSettings.getLoginUser().getUserId();
                    final UserShow sw = ShowQueries.getUserShowById(id, show.getShowId());
                    if (sw != null) {

                        new AlertDialog.Builder(AllShowFragment.this)
                                //.setTitle("Lock This Show")
                                .setMessage("You are about to remove the show: \n" + show.getName())
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (GlobalSettings.checkConnection(getBaseContext())) {
                                            boolean success = new ShowMgt().unfollowShow(id, show.getShowId());
                                            if (success) {
                                                sw.delete(); //delete show
                                                myShowList.remove(show);
                                                allShowAdapter.notifyDataSetChanged();
                                                Toast.makeText(mContext, "Show successfully removed!!!", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(mContext, "Error Occured While trying to Unfollow Show, " +
                                                        "Try later !!!", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(mContext, "No Internet:\nCheck Your Internet Connection", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else
                        Toast.makeText(mContext, "Action Not Allowed", Toast.LENGTH_SHORT).show();
                    System.gc();//Garbage collector invocation.
                }
            });
            return listItem;
        }

    }

    private class LoadShows extends AsyncTask<String, Void, String> {
        private TextView feedback;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            feedback = (TextView) findViewById(R.id.allshow_feedback);
            progressBar.setVisibility(View.VISIBLE);
            feedback.setText("Loading Shows...");
            feedback.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

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
            tab1.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_background4));

            desc_clicked = false;

            ListView list = (ListView) findViewById(R.id.allshow_list);
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
                public void onClick(View v) {//All shows
                    // v.setEnabled(false);
                    tab1.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_background4));
                    tab2.setBackgroundDrawable(button_bgd);
                    showList = null;
                    showList = allShows;
                    allShowAdapter.notifyDataSetChanged();
                    if (showList.size() > 0) {
                        feedback.setVisibility(View.GONE);
                    } else {
                        feedback.setVisibility(View.VISIBLE);
                        feedback.setText("No Shows...");
                    }
                }
            });

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {// My shows
                    tab2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_background4));
                    tab1.setBackgroundDrawable(button_bgd);
                    showList = null;
                    showList = myShowList;
                    allShowAdapter.notifyDataSetChanged();
                    if (myShowList.size() > 0) {
                        feedback.setVisibility(View.GONE);
                    } else {
                        feedback.setVisibility(View.VISIBLE);
                        feedback.setText("No Shows...\n Subscribe to more shows for a greater experience");
                    }

                }
            });

            progressBar.setVisibility(View.GONE);
            feedback.setVisibility(View.GONE);
        }
    }

}