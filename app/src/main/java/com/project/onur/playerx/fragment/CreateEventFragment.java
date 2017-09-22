package com.project.onur.playerx.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.project.onur.playerx.ItemData;
import com.project.onur.playerx.R;
import com.project.onur.playerx.SpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by onur on 22.9.2017 at 00:44.
 */

public class CreateEventFragment extends Fragment {

    public CreateEventFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_event, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(getString(R.string.create_event));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        ArrayList<ItemData> list=new ArrayList<>();
        list.add(new ItemData(getString(R.string.sport_text),R.drawable.ic_football_mini));
        list.add(new ItemData(getString(R.string.table_games_text),R.drawable.ic_table_games_mini));
        list.add(new ItemData(getString(R.string.concert_text),R.drawable.ic_concert_mini));
        list.add(new ItemData(getString(R.string.pc_games_text),R.drawable.ic_pc_games_mini));
        list.add(new ItemData(getString(R.string.cinema_text),R.drawable.ic_cinema_mini));
        list.add(new ItemData(getString(R.string.other_text),R.drawable.ic_other_mini));
        Spinner sp=(Spinner)v.findViewById(R.id.spinner);
        SpinnerAdapter adapter=new SpinnerAdapter(getActivity(),
                R.layout.spinner_row,R.id.txt,list);
        sp.setAdapter(adapter);


    }


}
