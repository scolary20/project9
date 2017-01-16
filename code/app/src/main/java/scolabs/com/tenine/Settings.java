package scolabs.com.tenine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.andexert.library.RippleView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;
import scolabs.com.tenine.databaseQueries.UserQueries;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

public class Settings extends ActionBarActivity {
    private final String MyPREFERENCES = GlobalSettings.getLoginUser().getEmail() + "_settings_prefs";
    private SharedPreferences.Editor editor;
    private Switch show_start;
    private Switch show_end;
    private Switch new_comment;
    private Switch mark;
    private Switch cache_shows;
    private Switch load_shows;
    private SharedPreferences sharedprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_app);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedprefs = GlobalSettings.settingsPreference(this);
        editor = sharedprefs.edit();

        //Logout
        logoutDialog();

        //Notification
        generalNotifSettings();

        //Media-Type
        mediaTypeSetting();

        //Delete Account
        deleteAccount();
    }

    public void deleteAccount()
    {
        final RippleView deleteButton = (RippleView) findViewById(R.id.delete_acc);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User aUser = GlobalSettings.getLoginUser();
                String filePath = getFilesDir().getParent() + "/shared_prefs/" + MyPREFERENCES + ".xml";
                final File deletePrefFile = new File(filePath);
                if (aUser != null) {
                    deleteAccount(aUser, deletePrefFile);
                }
            }
        });
    }

    public void mediaTypeSetting()
    {
        //Show Settings
        List<String> optionsList = Arrays.asList(getResources().getStringArray(R.array.media_type));
        String previousSelection = (String)sharedprefs.getString("media_type","video");
        final Spinner media_type_spinner = (Spinner) findViewById(R.id.media_type_spinner);

        ArrayAdapter<String> adp = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, optionsList);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int item_pos = adp.getPosition(previousSelection);
        media_type_spinner.setAdapter(adp);
        media_type_spinner.setSelection(item_pos);

        media_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = media_type_spinner.getSelectedItem().toString();
                editor.putString("media_type", type);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void logoutDialog() {

        RippleView logout = (RippleView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage("Are you sure?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!logout())
                                    Toast.makeText(Settings.this, "Logout Failed, Try Again", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public boolean logout() {
        String filePath = this.getFilesDir().getParent() + "/shared_prefs/com.scolabs.tenine_preferences.xml";
        final File deletePrefFile = new File(filePath);
        if (deletePrefFile.delete()) {
            finish();
            EventBus.getDefault().post("logout");
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void generalNotifSettings() {
        show_start = (Switch) findViewById(R.id.start_show_switch);
        show_start.setChecked(sharedprefs.getBoolean("show_start",true));

        show_end = (Switch) findViewById(R.id.end_show_switch);
        show_end.setChecked(sharedprefs.getBoolean("show_end",true));

        new_comment = (Switch) findViewById(R.id.new_comment_switch);
        new_comment.setChecked(sharedprefs.getBoolean("new_comment",true));

        mark = (Switch) findViewById(R.id.mark_switch);
        mark.setChecked(sharedprefs.getBoolean("mark",true));

        cache_shows = (Switch) findViewById(R.id.cache_shows);
        cache_shows.setChecked(sharedprefs.getBoolean("cache_shows",true));

        load_shows = (Switch) findViewById(R.id.load_shows);
        load_shows.setChecked(sharedprefs.getBoolean("load",true));

        show_start.setTag("show_start");
        show_end.setTag("show_end");
        new_comment.setTag("new_comment");
        cache_shows.setTag("cache");
        load_shows.setTag("load");
        mark.setTag("mark");

        show_start.setOnCheckedChangeListener(new OnCheckedChangeListner());
        show_end.setOnCheckedChangeListener(new OnCheckedChangeListner());
        new_comment.setOnCheckedChangeListener(new OnCheckedChangeListner());
        mark.setOnCheckedChangeListener(new OnCheckedChangeListner());
        cache_shows.setOnCheckedChangeListener(new OnCheckedChangeListner());
        load_shows.setOnCheckedChangeListener(new OnCheckedChangeListner());

    }


    // Method which Handle Account Dis-activation
    public boolean deleteAccount(final User loginUser, final File deletePrefFile) {
        final Dialog dialog = new Dialog(Settings.this);
        final AtomicBoolean rValue = new AtomicBoolean(false);

        dialog.setContentView(R.layout.login_layout);
        dialog.getWindow().setBackgroundDrawable(Settings.this.getResources().getDrawable(R.drawable.blue_background2));
        dialog.setTitle("Confirm Your Credentials");
        dialog.setCancelable(false);

        ViewFlipper imV = (ViewFlipper) dialog.findViewById(R.id.viewFlipper);
        Button rgbt = (Button) dialog.findViewById(R.id.register_btn);
        Button login = (Button) dialog.findViewById(R.id.sign_btn);
        final EditText email_field = (EditText) dialog.findViewById(R.id.email_field);
        final EditText pass_field = (EditText) dialog.findViewById(R.id.password_field);
        TextView checkText = (TextView) dialog.findViewById(R.id.textView7);
        final TextView error_message = (TextView) dialog.findViewById(R.id.error_messages);

        login.setText("Confirm");
        rgbt.setText("Cancel");
        imV.setVisibility(View.GONE);

        checkText.setText("We will miss you xoxo!!!");
        checkText.setTextSize(15f);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_field.getText().toString().trim();
                String pass = pass_field.getText().toString().trim();
                User aUser = UserQueries.getDbUser("", email, pass, "both"); //Query DB for a potential Match

                if (aUser != null && aUser.getUsername().equals(loginUser.getUsername())) {
                    aUser.delete();
                    editor.clear();
                    deletePrefFile.delete();
                    dialog.dismiss();

                    logout();
                } else {
                    error_message.setText("Wrong Credentials");
                    error_message.setTextColor(Color.RED);
                }
            }
        });

        rgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return rValue.get();
    }

    class OnCheckedChangeListner implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String tag = (String) buttonView.getTag();
            switch (tag) {
                case "show_start":
                    editor.putBoolean("show_start", isChecked);
                    editor.apply();
                    break;
                case "show_end":
                    editor.putBoolean("show_end", isChecked);
                    editor.apply();
                    break;
                case "new_comment":
                    editor.putBoolean("new_comment", isChecked);
                    editor.apply();
                    break;
                case "mark":
                    editor.putBoolean("mark", isChecked);
                    editor.apply();
                    break;
                case "cache":
                    editor.putBoolean("cache_shows", isChecked);
                    editor.apply();
                    break;
                case "load":
                    editor.putBoolean("load", isChecked);
                    editor.apply();
                    break;
            }
            Log.i(tag, " " + isChecked);
        }
    }
}
