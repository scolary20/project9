package scolabs.com.tenine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.ui.Register;
import scolabs.com.tenine.ui.ShowList;
import scolabs.com.tenine.remoteOperations.ChatSettings;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private final int SELECT_PHOTO = 1;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ImageView profile_pic;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        final Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        final Animation bounce1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        Animation bounced = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        Animation bounced1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

        bounced.setDuration(3000);
        bounced1.setDuration(3000);
        final TextView shows = (TextView) findViewById(R.id.image);
        final TextView airing_show = (TextView) findViewById(R.id.lg_img);

        shows.setAnimation(bounced);
        airing_show.setAnimation(bounced1);

        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(MainActivity.this, AllShowFragment.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        bounce1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTitle = "Today's Airing shows";
                Fragment f = new ShowList();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, f).addToBackStack("Show List")
                        .commit();
                EventBus.getDefault().post("close");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        shows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shows.startAnimation(bounce);
            }
        });

        airing_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                airing_show.startAnimation(bounce1);
            }
        });

        // Header Layout
        TextView header_date = (TextView)findViewById(R.id.header_date);
        TextView username = (TextView)findViewById(R.id.header_username);
        profile_pic = (ImageView) findViewById(R.id.profile_picture);
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });


        // Footer Layout
        TextView logout = (TextView) findViewById(R.id.logout);
        RippleView settings = (RippleView) findViewById(R.id.settings);
        TextView scolabs = (TextView) findViewById(R.id.scolabs);
        LinearLayout box = (LinearLayout) findViewById(R.id.box_linear);
        ImageView pro_pic = (ImageView) findViewById(R.id.profile_picture);
        Typeface sco = Typeface.createFromAsset(getAssets(), "simplifica.ttf");
        Typeface font = Typeface.create(sco, Typeface.BOLD);
        scolabs.setTypeface(font);
        shows.setTypeface(font);
        airing_show.setTypeface(font);

        settings.setOnClickListener(new View.OnClickListener() { //Settings
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, scolabs.com.tenine.Settings.class);
                startActivity(i);
            }
        });

        setBoxAndProBackground(box, pro_pic); //Change profile pic and box background
        EventBus.getDefault().register(this);

        User aUser = GlobalSettings.getLoginUser();
        if(aUser != null) {
            username.setText(aUser.getUsername());
            if (aUser.getProfile_url() != null) {
                try {
                    final Uri imageUri = Uri.parse(aUser.getProfile_url());
                    final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    profile_pic.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        header_date.setText(df.format(new Date()));
        Global.chatSettings = new ChatSettings();
        Global.chatSettings.createConnection(aUser.getUsername(), aUser.getPassword(), this);

    }

    @SuppressWarnings("deprecation")
    public void setBoxAndProBackground(LinearLayout box, ImageView pro_pic) {
        if (mNavigationDrawerFragment != null) {
            int today = ShowQueries.getTodayShowsCount(); // number of shows airing today
            int airing = ShowQueries.getMyAiringShowsCount(); // number of shows airing now
            Log.e("today", "" + today);
            Log.e("airing", "" + airing);

            if (airing == 0 && today == 0) {
                box.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile_red_grenite));
                pro_pic.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile_red_grenite));
            }

            if (today > 0 && airing == 0) {
                box.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile));
                pro_pic.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile));
            }

            if (airing > 0) {
                box.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile_red));
                pro_pic.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_profile_red));
            }

            box.refreshDrawableState();
            pro_pic.refreshDrawableState();

        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //.setTitle("Lock This Show")
                .setMessage("Do you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Today Airing shows";
                Fragment f = new ShowList();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, f).addToBackStack("Show List")
                        .commit();
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Subscribe
    public void onEventLogout(String strg) {
        if (strg.equals("logout"))
            finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,Register.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        User aUser = GlobalSettings.getLoginUser();
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profile_pic.setImageBitmap(selectedImage);
                        aUser.setProfile_url(imageReturnedIntent.getDataString());
                        aUser.save();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}