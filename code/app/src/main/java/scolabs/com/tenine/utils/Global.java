package scolabs.com.tenine.utils;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.remoteOperations.ChatSettings;
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
    public static int showStart_notifiCount = 0;
    public static int showEnd_notifCount = 0;
    public static int cmt_notifCount = 0;
    public static int cmtMarked_notifCount = 0;
    public static ShowAdapter showAdapter;
    public static String email;
    public static ChatSettings chatSettings;
    public static String applicationName;
    public static LinearLayout linearList;
    public static ShowAdapter showAdapterCheckFore;
    public static ListView showListView;
    public static HashMap<Long,ShowAdapter.ViewHolder> showViews = new HashMap<>();
}
