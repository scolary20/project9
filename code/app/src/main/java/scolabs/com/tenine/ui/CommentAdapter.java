package scolabs.com.tenine.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import scolabs.com.tenine.R;
import scolabs.com.tenine.WriteComment;
import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 2/2/2016.
 */
public class CommentAdapter extends ArrayAdapter {
    protected final String DOWN = "down";
    protected final String UP = "up";
    protected Context mContext;
    protected int layoutResourceId;
    protected ArrayList<Comment> data = null;
    protected CommentAdapter cmd = null;
    protected DateFormat dateFormat;
    protected NumberFormat nfm;

    public CommentAdapter(Context mContext, int layoutResourceId, ArrayList<Comment> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        cmd = this;
        dateFormat = new SimpleDateFormat("hh:mm a");
        nfm = NumberFormat.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View listItem; //= convertView;


        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);
        itemActions(listItem, position);
        listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                comment_popup(v, position);
                return false;
            }
        });

        Comment c = data.get(position);
        Resources rct = mContext.getResources();

        TextView lbl = (TextView) listItem.findViewById(R.id.content);
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "bariol.ttf");
        lbl.setTypeface(type);
        lbl.setText(c.getContent());


        lbl = (TextView) listItem.findViewById(R.id.up_mark);
        if (c.isMarked_up() == 1)
            lbl.setCompoundDrawablesWithIntrinsicBounds(rct.getDrawable(R.drawable.up_mark_liked), null, null, null);
        else
            lbl.setCompoundDrawablesWithIntrinsicBounds(rct.getDrawable(R.drawable.up_mark), null, null, null);

        if (c.getUps_mark() > 1) {
            lbl.setText(nfm.format(c.getUps_mark()) + " ups");
        } else
            lbl.setText(c.getUps_mark() + " up");

        lbl = (TextView) listItem.findViewById(R.id.down_mark);
        if (c.isMarked_down() == 1)
            lbl.setCompoundDrawablesWithIntrinsicBounds(rct.getDrawable(R.drawable.down_mark_dislike), null, null, null);
        else
            lbl.setCompoundDrawablesWithIntrinsicBounds(rct.getDrawable(R.drawable.down_mark), null, null, null);

        if (c.getDown_mark() > 1)
            lbl.setText(nfm.format(c.getDown_mark()) + " downs");
        else
            lbl.setText(c.getDown_mark() + " down");

        lbl = (TextView) listItem.findViewById(R.id.comment_date);
        lbl.setText(dateFormat.format(c.getDate()));
        TextView commentatorName = (TextView) listItem.findViewById(R.id.commentator_name);
        commentatorName.setText(c.getCommentator());
        ImageView pro_pic = (ImageView) listItem.findViewById(R.id.commet_pp);
        setProfilePic(mContext, pro_pic, c);

        return listItem;
    }

    public void itemActions(final View listItem, final int position) {
        final Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_marks);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView up = (TextView) listItem.findViewById(R.id.up_mark);
                final TextView down = (TextView) listItem.findViewById(R.id.down_mark);
                final TextView name = (TextView) listItem.findViewById(R.id.commentator_name);
                final ImageView profile_pic = (ImageView) listItem.findViewById(R.id.commet_pp);
                final LinearLayout lly = (LinearLayout) listItem.findViewById(R.id.rating_linear);
                final TextView content = (TextView) listItem.findViewById(R.id.content);
                final TextView down_mark_label = (TextView) listItem.findViewById(R.id.down_mark);
                final TextView up_mark_label = (TextView) listItem.findViewById(R.id.up_mark);


                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Resources ctx = mContext.getResources(); //Get resources
                        Comment cmt = data.get(position);
                        if (cmt.isMarked_up() == 0 && cmt.isMarked_down() == 0) {
                            v.startAnimation(bounce);
                            up.setCompoundDrawablesWithIntrinsicBounds(ctx.getDrawable(R.drawable.up_mark_liked), null, null, null);

                            CommentQueries.updateComment(cmt.getCmtId(), 1, UP);
                            cmt.setMarked_up(1);
                            cmt.setUps_mark(cmt.getUps_mark() + 1);

                            if (cmt.getUps_mark() > 1)
                                up_mark_label.setText(nfm.format(cmt.getUps_mark()) + " ups");
                            else
                                up_mark_label.setText(cmt.getUps_mark() + " up");

                        } else {
                            up.setCompoundDrawablesWithIntrinsicBounds(ctx.getDrawable(R.drawable.up_mark), null, null, null);
                            CommentQueries.updateComment(cmt.getCmtId(), 0, UP);

                            if (cmt.isMarked_up() == 1)
                                cmt.setUps_mark(cmt.getUps_mark() - 1);
                            cmt.setMarked_up(0);

                            if (cmt.getUps_mark() > 1)
                                up_mark_label.setText(nfm.format(cmt.getUps_mark()) + " ups");
                            else
                                up_mark_label.setText(cmt.getUps_mark() + " up");
                        }
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Resources ctx = mContext.getResources(); //Get resources

                        Comment cmt = data.get(position);
                        if (cmt.isMarked_down() == 0) {
                            v.startAnimation(bounce);
                            down.setCompoundDrawablesWithIntrinsicBounds(ctx.getDrawable(R.drawable.down_mark_dislike), null, null, null);
                            CommentQueries.updateComment(cmt.getCmtId(), 1, DOWN);
                            cmt.setMarked_down(1);
                            cmt.setDown_mark(cmt.getDown_mark() + 1); //Set number of marks

                            if (cmt.getDown_mark() > 1)
                                down_mark_label.setText(nfm.format(cmt.getDown_mark()) + " downs");
                            else
                                down_mark_label.setText(cmt.getDown_mark() + " down");

                            if (cmt.isMarked_up() == 1) {
                                up.setCompoundDrawablesWithIntrinsicBounds(ctx.getDrawable(R.drawable.up_mark), null, null, null);
                                CommentQueries.updateComment(cmt.getCmtId(), 0, UP);
                                cmt.setMarked_up(0);
                                cmt.setUps_mark(cmt.getUps_mark() - 1); // set number of marks

                                if (cmt.getUps_mark() > 1) {
                                    up_mark_label.setText(nfm.format(cmt.getUps_mark()) + " ups");
                                } else {
                                    up_mark_label.setText(cmt.getUps_mark() + " up");
                                }
                            }
                        } else {
                            down.setCompoundDrawablesWithIntrinsicBounds(ctx.getDrawable(R.drawable.down_mark), null, null, null);
                            CommentQueries.updateComment(cmt.getCmtId(), 0, DOWN);
                            cmt.setMarked_down(0);
                            cmt.setDown_mark(cmt.getDown_mark() - 1); // set number of marks

                            if (cmt.getDown_mark() > 1)
                                down_mark_label.setText(nfm.format(cmt.getDown_mark()) + " downs");
                            else
                                down_mark_label.setText(cmt.getDown_mark() + " down");
                        }
                    }
                });

                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (profile_pic.getVisibility() == View.GONE) {
                            profile_pic.setVisibility(profile_pic.VISIBLE);
                            name.setVisibility(name.VISIBLE);
                        } else {
                            profile_pic.setVisibility(profile_pic.GONE);
                            name.setVisibility(name.GONE);
                        }
                    }
                });

                final int initial_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getHeight(), getContext().getResources().getDisplayMetrics());
                final int initial_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, profile_pic.getWidth(), getContext().getResources().getDisplayMetrics());

                profile_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (content.getVisibility() == View.GONE) {
                            content.setVisibility(content.VISIBLE);
                            lly.setVisibility(lly.VISIBLE);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height / 2;
                            params.width = initial_width / 2;
                            profile_pic.setLayoutParams(params);
                        } else {
                            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.image_anim);
                            content.setVisibility(content.GONE);
                            lly.setVisibility(lly.GONE);
                            profile_pic.setAnimation(bounce);
                            ViewGroup.LayoutParams params = profile_pic.getLayoutParams();
                            params.height = initial_height * 2;
                            params.width = initial_width * 2;
                            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(initial_width*2, initial_height*2);
                            //layoutParams.gravity=Gravity.CENTER;
                            profile_pic.setLayoutParams(params);
                        }
                    }
                });
            }

        });

    }

    public void comment_popup(final View listItem, final int position) {
        final Dialog dialog = new Dialog(Global.commentActivity);
        dialog.setContentView(R.layout.comment_popup);
        dialog.getWindow().setBackgroundDrawable(Global.commentActivity.getResources().getDrawable(R.drawable.blue_background2));
        dialog.setTitle("Options");
        dialog.setCancelable(true);


        Button hide = (Button) dialog.findViewById(R.id.hide_bt);
        final Button edit = (Button) dialog.findViewById(R.id.edit_btn);
        Button report = (Button) dialog.findViewById(R.id.report_btn);
        Button delete = (Button) dialog.findViewById(R.id.delete_btn);

        hide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setVisibility(listItem.GONE);
                data.remove(data.get(position));
                cmd.notifyDataSetChanged();
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
                data.get(position).delete();
                data.remove(data.get(position));
                cmd.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Comment c = data.get(position);
                if (c.getCommentator().equalsIgnoreCase(GlobalSettings.getLoginUser().getUsername())) {
                    Intent i = new Intent(getContext(), WriteComment.class);
                    i.putExtra("showId", c.getShowId());
                    i.putExtra("message", c.getContent());
                    i.putExtra("type", 1);
                    i.putExtra("position", position);
                    getContext().startActivity(i);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Error: You cannot modify this comment \nYou're not the owner", Toast.LENGTH_LONG);
                }
            }
        });
        dialog.show();
    }

    public void setProfilePic(Context mContext, ImageView profile_pic, Comment c) {
        User aUser = GlobalSettings.getLoginUser();
        if (c.getCommentator().equalsIgnoreCase(aUser.getUsername())) {
            try {
                final Uri imageUri = Uri.parse(aUser.getProfilurl());
                final InputStream imageStream = mContext.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profile_pic.setImageBitmap(selectedImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
