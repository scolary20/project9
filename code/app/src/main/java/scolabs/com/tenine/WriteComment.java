package scolabs.com.tenine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.util.Date;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Global;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 2/22/2016.
 */
public class WriteComment extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_comment);
        Button cancel = (Button)findViewById(R.id.cment_cancel_bt);
        Button post = (Button)findViewById(R.id.cment_post_bt);
        final EditText input = (EditText)findViewById(R.id.cment_inputText);
        final Long showId = getIntent().getLongExtra("showId", -99);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input.getText().equals(""))
                {
                    String cment = input.getText().toString().trim();
                    Log.e("showId", "" + showId);
                    String name = Settings.getLoginUser().getUsername();
                    Comment c = new Comment(cment,name,new Date(),showId);
                    c.save();
                    Global.cmAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }
}
