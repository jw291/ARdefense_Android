package com.example.jaewonna.defense;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Guild extends Fragment{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GuildActivitySelect guildActivitySelect = new GuildActivitySelect(MainActivity.myname);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_guild, container, false);

        System.out.println(GuildActivitySelect.isguild+"길드명");
        if(GuildActivitySelect.isguild == null) {
            getFragmentManager().beginTransaction().replace(R.id.guildframelayout, new Noguild()).commit();
        }else {
            getFragmentManager().beginTransaction().replace(R.id.guildframelayout, new Isguild()).commit();
        }
        //만약 길드가 있으면 다른 isguild framelayout
        //만약 길드가 없다면 noguild framlayout


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
