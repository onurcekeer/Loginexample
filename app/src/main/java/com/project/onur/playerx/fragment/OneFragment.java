package com.project.onur.playerx.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.onur.playerx.R;


public class OneFragment extends Fragment implements View.OnClickListener {


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
        switch (v.getId()) {

            case R.id.lineer_sport:
                Toast.makeText(v.getContext(),"spor",Toast.LENGTH_SHORT).show();
                break;

            case R.id.lineer_table_game:
                Toast.makeText(v.getContext(),"Masa Oyunları",Toast.LENGTH_SHORT).show();
                break;

            case R.id.lineer_concert:
                Toast.makeText(v.getContext(),"Konser",Toast.LENGTH_SHORT).show();
                break;

            case R.id.lineer_pc_game:
                Toast.makeText(v.getContext(),"PC Oyunları",Toast.LENGTH_SHORT).show();
                break;

            case R.id.lineer_other:
                Toast.makeText(v.getContext(),"Diğer",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

}
