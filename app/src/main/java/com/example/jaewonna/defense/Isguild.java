package com.example.jaewonna.defense;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
class IsGuildProduct {

    private String membername;
    public IsGuildProduct(String membername){
        this.membername = membername;
    }

    public String getMembername() {
        return this.membername;
    }
}

class IsGuildViewHolder extends RecyclerView.ViewHolder {

    public TextView guildmember;
    public IsGuildViewHolder(View itemView) {
        super(itemView);

        guildmember = (TextView) itemView.findViewById(R.id.guildmember);

    }
}

class IsGuildAdapter extends RecyclerView.Adapter<IsGuildViewHolder> {

    private ArrayList<IsGuildProduct> verticalDatas;
    private Context context;
    public void setData(ArrayList<IsGuildProduct> list, Context context){
        verticalDatas = list;
        this.context = context;
    }

    @Override
    public IsGuildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

// 사용할 아이템의 뷰를 생성해준다.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guild_member_list, parent, false);

        IsGuildViewHolder holder = new IsGuildViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final IsGuildViewHolder holder, final int position) {
        final IsGuildProduct data = verticalDatas.get(position);

        holder.guildmember.setText(data.getMembername());
    }

    @Override
    public int getItemCount() {
        return verticalDatas.size();
    }


}
public class Isguild extends Fragment {
    RecyclerView mVerticalView;
    IsGuildAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<IsGuildProduct> data;

    TextView guildname;
    ImageButton guildapply;

    HttpPost httpPost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpClient;
    ArrayList<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    String guildmaster;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_isguild, container, false);
        guildname = (TextView)view.findViewById(R.id.guildinfoname);

        guildapply = (ImageButton) view.findViewById(R.id.guildapplybutton);
        Settingbuttonvisible();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        guildname.setText(GuildActivitySelect.isguild);
        System.out.println("onviewcreated"+MainActivity.myname+" "+guildmaster.toString());
        if(MainActivity.myname.equals(guildmaster)){
            guildapply.setVisibility(View.VISIBLE);
        }else {
            guildapply.setVisibility(View.GONE);
        }
        guildapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GuildApply.class);
                startActivity(intent);
            }
        });

        mVerticalView = (RecyclerView)view.findViewById(R.id.isguildrecyclerview);
        data = new ArrayList<>();
        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 기본값이 VERTICAL

        // setLayoutManager
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new IsGuildAdapter();

        GuildMemberList();
    }


    public void Settingbuttonvisible(){
        try {
            httpClient = new DefaultHttpClient(); //apache httpclient이용
            httpPost = new HttpPost("http://13.124.189.174/settingbuttonvisible.php");//url연결
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("guild_name", GuildActivitySelect.isguild));//"id","pw"는 php에서 post로 전달받을 변수명이므로 정확히 기재

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httpPost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            guildmaster = response;

        }
        catch(Exception e)
        {
//            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void GuildMemberList() {
        try {
            httpClient = new DefaultHttpClient(); //apache httpclient이용
            httpPost = new HttpPost("http://13.124.189.174/guildmemberlist.php");//url연결
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("guild_name", GuildActivitySelect.isguild));//"id","pw"는 php에서 post로 전달받을 변수명이므로 정확히 기재

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httpPost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            JSONArray dataarray = new JSONArray(response);

            System.out.println("GuildMemberList"+dataarray);
            for(int i=0; i<dataarray.length(); i++){
                JSONObject dataobject = dataarray.getJSONObject(i);

                String userid = dataobject.getString("user_id");

                System.out.println("GuildMemberList 유저이름"+userid);
                data.add(new IsGuildProduct(userid));
            }
            mAdapter.notifyDataSetChanged();
            mAdapter.setData(data,getActivity());
            mVerticalView.setAdapter(mAdapter);

        }
        catch(Exception e)
        {
//            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
}
