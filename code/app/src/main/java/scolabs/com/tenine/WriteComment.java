package scolabs.com.tenine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

        if (getIntent().getIntExtra("type", 0) == 1)
            input.setText(getIntent().getStringExtra("message"));

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
                    String name = Settings.getLoginUser().getUsername();
                    Comment c;
                    Intent cmInt = getIntent();
                    if (cmInt.getIntExtra("type", 0) == 1) {
                        c = (Comment) Global.cmAdapter.getItem(cmInt.getIntExtra("position", -99));
                        c.setContent(cment);
                        c.save();
                    } else {
                        c = new Comment(cment, name, new Date(), showId);
                        Global.cmAdapter.add(c);
                        c.save();
                    }

                    Global.cmAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }
}
