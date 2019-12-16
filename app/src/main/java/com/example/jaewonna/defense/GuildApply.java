package com.example.jaewonna.defense;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GuildApply extends AppCompatActivity {


    //-----------------------------------
    RecyclerView mVerticalView;
    GuildApplyAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<GuildApplyProduct> data;
    HttpPost httpPost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpClient;
    ArrayList<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    public void GuildApplyUser() {
        try {
            httpClient = new DefaultHttpClient(); //apache httpclient이용
            httpPost = new HttpPost("http://13.124.189.174/guildapplylist.php");//url연결
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("guild_name", GuildActivitySelect.isguild));//"id","pw"는 php에서 post로 전달받을 변수명이므로 정확히 기재

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httpPost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            JSONArray dataarray = new JSONArray(response);

            System.out.println("dataarray"+dataarray);
            for(int i=0; i<dataarray.length(); i++){
                JSONObject dataobject = dataarray.getJSONObject(i);

                String userid = dataobject.getString("user_id");

                System.out.println("유저 뽑아옴"+userid);
                data.add(new GuildApplyProduct(userid));
            }
            mAdapter.notifyDataSetChanged();
            mAdapter.setData(data,getApplicationContext());
            mVerticalView.setAdapter(mAdapter);

        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild_apply);

        mVerticalView = (RecyclerView) findViewById(R.id.guildapplyrecyclerview);
        data = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // setLayoutManager
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new GuildApplyAdapter();
        GuildApplyUser();



    }


    class GuildApplyProduct {

        private String applyusername;

        public GuildApplyProduct(String applyusername) {
            this.applyusername = applyusername;
        }

        public String getApplyusername() {
            return this.applyusername;
        }
    }

    class GuildApplyViewHolder extends RecyclerView.ViewHolder {

        public TextView guildname;
        public Button agree, disagree;

        public GuildApplyViewHolder(View itemView) {
            super(itemView);

            guildname = (TextView) itemView.findViewById(R.id.applyname);
            agree = (Button) itemView.findViewById(R.id.agree);
            disagree = (Button) itemView.findViewById(R.id.disagree);

        }
    }

    class GuildApplyAdapter extends RecyclerView.Adapter<GuildApplyViewHolder> {

        private ArrayList<GuildApplyProduct> verticalDatas;
        private Context context;

        public void setData(ArrayList<GuildApplyProduct> list, Context context) {
            verticalDatas = list;
            this.context = context;
        }

        @Override
        public GuildApplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

// 사용할 아이템의 뷰를 생성해준다.
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.guildapply_list_item, parent, false);

            GuildApplyViewHolder holder = new GuildApplyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(GuildApplyViewHolder holder, final int position) {
            final GuildApplyProduct data = verticalDatas.get(position);

            holder.guildname.setText(data.getApplyusername());
            holder.agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "동의" + position, Toast.LENGTH_SHORT).show();
                    //1)data.position에서 이름을 뽑아와 update guild_name where user_id = data.position이름 을 넣는다.
                    //2)guildapply테이블에서 삭제시킨다.
                    guildagree(verticalDatas.get(position).getApplyusername());
                    deleteapplylist(verticalDatas.get(position).getApplyusername());
                    verticalDatas.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            });
            holder.disagree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "비동의", Toast.LENGTH_SHORT).show();
                    //1)guildapply테이블에서 삭제시킨다.
                   deleteapplylist(verticalDatas.get(position).getApplyusername());
                    verticalDatas.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return verticalDatas.size();
        }

        public void guildagree(String username){
            try {
                httpClient = new DefaultHttpClient(); //apache httpclient이용
                httpPost = new HttpPost("http://13.124.189.174/guildagree.php");//url연결
                nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_id",username));
                nameValuePairs.add(new BasicNameValuePair("guild_name", GuildActivitySelect.isguild));//"id","pw"는 php에서 post로 전달받을 변수명이므로 정확히 기재

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                final String response = httpClient.execute(httpPost, responseHandler);

               // Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

            }
            catch(Exception e)
            {
                dialog.dismiss();
                System.out.println("Exception : " + e.getMessage());
            }
        }

        public void deleteapplylist(String username){
            try {
                httpClient = new DefaultHttpClient(); //apache httpclient이용
                httpPost = new HttpPost("http://13.124.189.174/guildapplylistdelete.php");//url연결
                nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user_id",username));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                final String response = httpClient.execute(httpPost, responseHandler);

              //  Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

            }
            catch(Exception e)
            {
                dialog.dismiss();
                System.out.println("Exception : " + e.getMessage());
            }
        }

    }
}