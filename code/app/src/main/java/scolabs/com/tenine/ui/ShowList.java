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
        showList = new ArrayList<>();
        Show sw = new Show("Breaking Bad", "s3E11", "netflix");
        sw.setShow_length(40);

        // Convert string to date
        SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        Date newdate = new Date();
        String strdate2 = "10-02-2015 07:20:42";
        try {
            newdate = dateformat2.parse(strdate2);
        } catch (ParseException e) {
            e.printStackTrace();
            sw.setAiring_date(newdate);

            showList.add(sw);
            showList.add(new Show("Empire", "s2E10", "hulu network"));
            showList.add(new Show("How to get away with murder", "s4E2", "Mtv"));
            showList.add(new Show("South park", "s6E7", "EuroSport"));
            showList.add(new Show("Modern Family", "s7E9", "showTime"));
        }
    }
}
