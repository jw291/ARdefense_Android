package com.example.jaewonna.defense;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

class GuildProduct {

    private String guildname;
    private String guildmaster;
    private String guildexplain;
    public GuildProduct(String guildname, String guildmaster, String guildexplain){
        this.guildname = guildname;
        this.guildmaster = guildmaster;
        this.guildexplain = guildexplain;
    }

    public String getGuildname() {
        return this.guildname;
    }

    public String getGuildmaster() {
        return this.guildmaster;
    }

    public String getGuildexplain(){return this.guildexplain; }
}

class GuildListViewHolder extends RecyclerView.ViewHolder {

    public TextView guildname,guildmaster,guildexplain;
    public Button button;
    public GuildListViewHolder(View itemView) {
        super(itemView);

        guildname = (TextView) itemView.findViewById(R.id.guildlistname);
        guildmaster = (TextView) itemView.findViewById(R.id.guildlistmaster);
        guildexplain = (TextView)itemView.findViewById(R.id.guildlistexplain);
        button = (Button)itemView.findViewById(R.id.guildlistbutton);

    }
}

class GuildListAdapter extends RecyclerView.Adapter<GuildListViewHolder> {

    private ArrayList<GuildProduct> verticalDatas;
    private Context context;
    public void setData(ArrayList<GuildProduct> list, Context context){
        verticalDatas = list;
        this.context = context;
    }

    @Override
    public GuildListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

// 사용할 아이템의 뷰를 생성해준다.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guild_list_item, parent, false);

        GuildListViewHolder holder = new GuildListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final GuildListViewHolder holder, final int position) {
        final GuildProduct data = verticalDatas.get(position);

        holder.guildname.setText(data.getGuildname());
        holder.guildmaster.setText(data.getGuildmaster());
        holder.guildexplain.setText(data.getGuildexplain());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToDatabase(MainActivity.myname,verticalDatas.get(position).getGuildname());
                Toast.makeText(context, ""+verticalDatas.get(position).getGuildmaster(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return verticalDatas.size();
    }

    public void insertToDatabase(String user_id, String guild_name){
        class InsertData extends AsyncTask<String,Void,String>
        {
        //    ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
      //          loading = ProgressDialog.show(context,"기다려주세요",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
    //            loading.dismiss();
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String user_id = (String)params[0];
                    String guild_name = (String)params[1];

                    //내 웹서버 url 입력
                    String link = "http://13.124.189.174/guildapply.php";

                    //database에 넣을 데이터
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                    data += "&" + URLEncoder.encode("guild_name", "UTF-8") + "=" + URLEncoder.encode(guild_name, "UTF-8");

                    //url 연결
                    URL url = new URL(link);
                    URLConnection urlConnection = url.openConnection();

                    //연결한 url에 outputstreamwriter로 값을 쓸 수 있다.
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                    //내가 정해놓은 데이터를 씀.
                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    //read server Response
                    while((line = reader.readLine())!=null){
                        stringBuilder.append(line);
                        System.out.println("line = "+line);
                        break;
                    }
                    return stringBuilder.toString();
                }catch (Exception e){
                    return new String("Exception : "+e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(user_id,guild_name);
    }
}
public class GuildList extends AppCompatActivity {
    RecyclerView mVerticalView;
    GuildListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<GuildProduct> data;
    //mysql data variables
    private static final String PRODUCT_URL = "http://13.124.189.174/GuildList.php";

    private void loadchatdata(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PRODUCT_URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray dataarray = new JSONArray(response);

                    for(int i=0; i<dataarray.length(); i++){
                        JSONObject dataobject = dataarray.getJSONObject(i);

                        System.out.println("loadchatdata"+dataobject);
                        String guild_name = dataobject.getString("guild_name");
                        String guild_master = dataobject.getString("guild_master");
                        String guild_explain = dataobject.getString("guild_explain");

                        GuildProduct GuildProduct = new GuildProduct(guild_name,guild_master,guild_explain);
                        System.out.println(GuildProduct);
                        data.add(GuildProduct);
                    }

                    // set Data
                    mAdapter.setData(data,getApplicationContext());
                    mVerticalView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GuildList.this, "음??", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(GuildList.this).add(stringRequest);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild_list);

        System.out.println("guildlist"+" oncreate 시작");
        mVerticalView = (RecyclerView)findViewById(R.id.guildlistrecyclerview);

        data = new ArrayList<>();
        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());


        // setLayoutManager
        mVerticalView.setLayoutManager(mLayoutManager);
        System.out.println("adapter"+" adpater 불러오기 전");
        mAdapter = new GuildListAdapter();
        // set Adapter
        System.out.println("dataload하기 전" + "");
        loadchatdata();

    }
}
