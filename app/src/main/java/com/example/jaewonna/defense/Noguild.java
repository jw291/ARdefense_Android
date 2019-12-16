package com.example.jaewonna.defense;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Noguild extends Fragment {

    Button makeguild, guildlist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_noguild, container, false);
        makeguild = (Button)view.findViewById(R.id.guildmake);
        guildlist = (Button)view.findViewById(R.id.guildlist);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeguild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GuildMake.class);
                startActivity(intent);

                //길드 만들기
                //길드를 만들면 member 테이블의 guild칼럼에 길드명이 들어가게된다.
                //guild_tb에 추가된다. (column -> 길드명, 길드장, 한줄평)

            }
        });

        guildlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GuildList.class);
              startActivity(intent);

            }
        });
    }
}
