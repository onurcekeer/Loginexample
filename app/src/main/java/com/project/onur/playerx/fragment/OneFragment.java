package com.project.onur.playerx.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.onur.playerx.R;

import java.util.List;


public class OneFragment extends Fragment implements View.OnClickListener {



    LinearLayout scroolView_layout;
    LinearLayout selectedItem_layout;
    TextView text_category ;
    ImageView clear_category;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        perform(view);
        return view;
    }

    private void perform(View v) {

        scroolView_layout = (LinearLayout)v.findViewById(R.id.scrollView_layout);
        selectedItem_layout = (LinearLayout)v.findViewById(R.id.selecteditem);
        text_category = (TextView)v.findViewById(R.id.categoryName);
        clear_category = (ImageView)v.findViewById(R.id.clear);

        clear_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem_layout.setVisibility(View.INVISIBLE);
                scroolView_layout.setVisibility(View.VISIBLE);
            }
        });


        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_home));

        View sport = v.findViewById(R.id.lineer_sport);
        sport.setOnClickListener(this);
        View table_games = v.findViewById(R.id.lineer_table_game);
        table_games.setOnClickListener(this);
        View concert = v.findViewById(R.id.lineer_concert);
        concert.setOnClickListener(this);
        View pc_games = v.findViewById(R.id.lineer_pc_game);
        pc_games.setOnClickListener(this);
        View other = v.findViewById(R.id.lineer_other);
        other.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        scroolView_layout.setVisibility(View.INVISIBLE);
        selectedItem_layout.setVisibility(View.VISIBLE);

        switch (v.getId()) {

            case R.id.lineer_sport:
                text_category.setText(R.string.sport_text);
                break;

            case R.id.lineer_table_game:
                text_category.setText(R.string.table_games_text);
                break;

            case R.id.lineer_concert:
                text_category.setText(R.string.concert_text);
                break;

            case R.id.lineer_pc_game:
                text_category.setText(R.string.pc_games_text);
                break;

            case R.id.lineer_other:
                text_category.setText(R.string.other_text);
                break;
            default:
                break;
        }
    }

}
