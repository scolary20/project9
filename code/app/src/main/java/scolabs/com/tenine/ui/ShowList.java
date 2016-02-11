package scolabs.com.tenine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.CommentActivity;
import scolabs.com.tenine.R;
import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 1/29/2016.
 */

public class ShowList extends Fragment {
    ShowAdapter showAdapter;
    private ArrayList<Show> showList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.shows_ui, container, false);
        SearchView search = (SearchView)v.findViewById(R.id.searchView);
        search.setQueryHint("Type your text here");

        loadShows();
        ListView list = (ListView) v.findViewById(R.id.listView);
        showAdapter = new ShowAdapter(getActivity(), R.layout.show_list_item, showList);
        list.setAdapter(showAdapter);
        list.setAlwaysDrawnWithCacheEnabled(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                if (pos == 1) {
                    Intent myIntent = new Intent(getActivity(), CommentActivity.class);
                    startActivityForResult(myIntent, 1);
                }
            }
        });

        return v;
    }

    private void loadShows() {

        // Convert string to date
        SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String strdate2 = "10-02-2016 16:30:42";
        String strdate3 = "10-02-2016 17:00:00";
        Date newdate = null;
        Date nd = null;
        try {
            newdate = dateformat2.parse(strdate2);
            nd = dateformat2.parse(strdate3);
            System.out.println(newdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        showList = new ArrayList<>();
        Show sw = new Show("Breaking Bad", "s3E11", "netflix");
        sw.setShow_length(40);
        sw.setNum_comment(545326);
        sw.setNum_watching(412556345L);
        sw.setAiring_date(newdate);
        sw.save();
            showList.add(sw);
        Show tr = new Show("Empire", "s4E3", "Hulu network");
        tr.setAiring_date(nd);
        tr.setNum_watching(5000);
        tr.setNum_comment(30545);
        tr.setShow_length(27);
        tr.save();
            showList.add(tr);
            showList.add(new Show("How to get away with murder", "s4E2", "Mtv"));
            showList.add(new Show("South park", "s6E7", "EuroSport"));
            showList.add(new Show("Modern Family", "s7E9", "showTime"));
    }
}
