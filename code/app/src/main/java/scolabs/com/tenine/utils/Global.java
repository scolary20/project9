package scolabs.com.tenine.utils;

import android.content.Context;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.ui.CommentAdapter;
import scolabs.com.tenine.ui.ShowAdapter;

/**
 * Created by scolary on 1/30/2016.
 */
public class Global {
    static public ListView lsView;
    static public Context commentActivity;
    static public Context commentListContext;
    static public long showId;
    static public Show show;
    static public ProgressBar progressBar;
    static public TextView txt;
    static public CommentAdapter cmAdapter;
    public static ArrayList<Thread> drawerShowThreads = new ArrayList<>();
    public static int notificationsCount = 0;
    public static ShowAdapter showAdapter;
    public static String email;
}
