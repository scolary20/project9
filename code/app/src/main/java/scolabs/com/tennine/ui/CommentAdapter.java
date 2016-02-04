package scolabs.com.tennine.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import scolabs.com.tennine.CommentActivity;
import scolabs.com.tennine.R;
import scolabs.com.tennine.model.Comment;
import scolabs.com.tennine.model.Global;

/**
 * Created by scolary on 2/2/2016.
 */
public class CommentAdapter extends ArrayAdapter
{
    Context mContext;
    int layoutResourceId;
    ArrayList<Comment> data = null;

    public CommentAdapter(Context mContext, int layoutResourceId, ArrayList<Comment> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View listItem; //= convertView;
        NumberFormat nfm = NumberFormat.getInstance();


        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);
        ItemActions(listItem);
        listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                comment_popup(v);
                return false;
            }
        });

        Comment c = data.get(position);

        TextView lbl = (TextView) listItem.findViewById(R.id.content);
        lbl.setText(c.getContent());

        lbl = (TextView) listItem.findViewById(R.id.up_mark);
        if(c.getUps_mark()> 1)
            lbl.setText(nfm.format(c.getUps_mark())+" ups");
        else
            lbl.setText(nfm.format(c.getUps_mark())+" up");

        lbl = (TextView)listItem.findViewById(R.id.down_mark);
        if(c.getDown_mark()>1)
            lbl.setText(nfm.format(c.getDown_mark())+" downs");
        else
            lbl.setText(nfm.format(c.getDown_mark())+" down");

        lbl = (TextView) listItem.findViewById(R.id.comment_date);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        lbl.setText(dateFormat.format(c.getDate()));

        return listItem;
    }

    public void ItemActions(final View listItem)
    {
        final Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_marks);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView up = (TextView)listItem.findViewById(R.id.up_mark);
                TextView down = (TextView)listItem.findViewById(R.id.down_mark);
                final ImageView profile_pic = (ImageView)listItem.findViewById(R.id.commet_pp);
                final LinearLayout lly = (LinearLayout)listItem.findViewById(R.id.rating_linear);
                final TextView content = (TextView)listItem.findViewById(R.id.content);

                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(bounce);
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(bounce);
                    }
                });

                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(profile_pic.getVisibility()==View.GONE)
                        {
                            profile_pic.setVisibility(profile_pic.VISIBLE);
                        }
                        else
                        {
                            profile_pic.setVisibility(profile_pic.GONE);
                        }
                    }
                });

                final int initial_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getHeight(), getContext().getResources().getDisplayMetrics());
                final int initial_width  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getWidth(),getContext().getResources().getDisplayMetrics());

                profile_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(content.getVisibility()==View.GONE){
                            content.setVisibility(content.VISIBLE);
                            lly.setVisibility(lly.VISIBLE);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height/2;
                            params.width =  initial_width/2;
                            profile_pic.setLayoutParams(params);
                        }
                        else
                        {
                            Animation bounce = AnimationUtils.loadAnimation(getContext(),R.anim.image_anim);
                            content.setVisibility(content.GONE);
                            lly.setVisibility(lly.GONE);
                            profile_pic.setAnimation(bounce);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height * 2;
                            params.width =initial_width * 2;
                            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(initial_width*2, initial_height*2);
                            //layoutParams.gravity=Gravity.CENTER;
                            profile_pic.setLayoutParams(params);
                        }
                    }
                });
            }

        });

    }

    public void comment_popup(final View listItem){
        final Dialog dialog = new Dialog(Global.commentActivity);
        dialog.setContentView(R.layout.comment_popup);
        dialog.getWindow().setBackgroundDrawable(Global.commentActivity.getResources().getDrawable(R.drawable.blue_background2));
        dialog.setTitle("Options");
        dialog.setCancelable(true);


        Button hide = (Button)dialog.findViewById(R.id.hide_bt);
        final Button edit = (Button)dialog.findViewById(R.id.edit_btn);
        Button report = (Button)dialog.findViewById(R.id.report_btn);
        Button delete = (Button)dialog.findViewById(R.id.delete_btn);

        hide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setVisibility(listItem.GONE);
                dialog.dismiss();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Global.commentActivity, "Comment Successfully reported", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setVisibility(listItem.GONE);
                dialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
