package scolabs.com.tennine.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import scolabs.com.tennine.R;
import scolabs.com.tennine.model.Comment;

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

        View listItem = convertView;
        NumberFormat nfm = NumberFormat.getInstance();

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

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
}
