package scolabs.com.tenine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

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

        ImageButton send = (ImageButton) findViewById(R.id.send);
        ImageView back = (ImageView) findViewById(R.id.back);
        final EditText input = (EditText)findViewById(R.id.cment_inputText);
        final Long showId = getIntent().getLongExtra("showId", -99);

        if (getIntent().getIntExtra("type", 0) == 1)
            input.setText(getIntent().getStringExtra("message"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input.getText().equals(""))
                {
                    String cment = input.getText().toString().trim();
                    String name = GlobalSettings.getLoginUser().getUsername();
                    Comment c;
                    Intent cmInt = getIntent();
                    if (cmInt.getIntExtra("type", 0) == 1) {//UPDATE
                        c = (Comment) Global.cmAdapter.getItem(cmInt.getIntExtra("position", -99));
                        c.setContent(cment);
                        c.save();
                    } else {//NEW COMMENT
                        c = new Comment(cment, name, new Date(), showId);
                        Global.cmAdapter.add(c);
                        c.save();
                    }

                    Global.cmAdapter.notifyDataSetChanged();
                    Global.txt.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }
}
