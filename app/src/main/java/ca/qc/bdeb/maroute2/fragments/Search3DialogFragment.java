package ca.qc.bdeb.maroute2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

import ca.qc.bdeb.maroute2.R;
import ca.qc.bdeb.maroute2.models.Direction;
import ca.qc.bdeb.maroute2.models.Route;
import ca.qc.bdeb.maroute2.models.Stop;
import ca.qc.bdeb.maroute2.utils.DbGetter;

/**
 * Created by jason on 6/5/2016.
 */
public class Search3DialogFragment extends DialogFragment {
    SearchView ss_searchview;
    ListView ss_listview;
    ArrayAdapter<Stop> adapter;
    //request code
    private int requestCodeIn = 1;
    // to get the result from dialog


    // for pass in arguments to DialogFragment
    public static Search3DialogFragment newInstance(Route route, Direction direction){
        Search3DialogFragment df = new Search3DialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("route", route);
        bundle.putSerializable("direction",direction);
        df.setArguments(bundle);
        return df;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_select, null);
        //set title for dialog
        requestCodeIn = getTargetRequestCode();

        getDialog().setTitle(R.string.searchTitle);

        //get view by id
        ss_searchview = (SearchView) view.findViewById(R.id.ss_searchview);
        ss_listview = (ListView) view.findViewById(R.id.ss_listview);


        //adapter for listview
        setupListView();


        //ss_listview.setOnClickListener(new HandleClickListView());
        ss_listview.setOnItemClickListener(new HandleClickListView());

        //search hint
        ss_searchview.setQueryHint(getResources().getString(R.string.searchHint));
        ss_searchview.setOnQueryTextListener(new HandleQueryText());


        return view;

        // return super.onCreateView(inflater, container, savedInstanceState);

    }

    private void setupListView() {

        //Route route =(Route) getActivity().getIntent().getExtras().getSerializable("route");
        Route route = (Route)getArguments().getSerializable("route");
        Direction direction = (Direction) getArguments().getSerializable("direction");

        // query direction from db
        DbGetter db = DbGetter.getInstance(getActivity());
        db.open();
        List<Stop> stopList = db.getStopList(route.getId(), direction.getId());
        db.close();

        adapter = new ArrayAdapter<Stop>(getActivity(), android.R.layout.simple_list_item_1, stopList);
        ss_listview.setAdapter(adapter);

    }


    // inner class for ListView click listener
    private class HandleClickListView implements AdapterView.OnItemClickListener {


        @Override
        //public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String value = (String)adapter.getItemAtPosition(position);
            Stop stop = adapter.getItem(position);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("stop", stop);
            intent.putExtras(bundle);

            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();

        }
    }

    // inner class for Searchview listner
    private class HandleQueryText implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.getFilter().filter(newText);
            return false;
        }
    }


}
