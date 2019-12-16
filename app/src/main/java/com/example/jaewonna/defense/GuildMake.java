package com.example.jaewonna.defense;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.microedition.khronos.egl.EGLDisplay;

public class GuildMake extends AppCompatActivity {

    EditText guildnameet;
    EditText guildexplainet;
    TextView guildmastertx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild_make);

        guildnameet = (EditText)findViewById(R.id.guildname);
        guildexplainet = (EditText)findViewById(R.id.guildexplain);
        guildmastertx = (TextView)findViewById(R.id.myname);
        guildmastertx.setText(MainActivity.myname);
    }

    //폼에서 값을 얻어내 데이터베이스에 넣는 메서드
    public void Guildmake(View view){
        String guildname = guildnameet.getText().toString();
        String guildexplain = guildexplainet.getText().toString();
        String guildmaster = MainActivity.myname;

        insertToGuild(guildname,guildmaster,guildexplain);

        finish();
    }


    public void insertToGuild(String guildname, String guildmaster, String guildexplain){
        class InsertData extends AsyncTask<String,Void,String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GuildMake.this,"기다려주세요",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(GuildMake.this, s, Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String guildname = (String)params[0];
                    String guildmaster = (String)params[1];
                    String guildexplain = (String)params[2];

                    //내 웹서버 url 입력
                    String link = "http://13.124.189.174/Guild.php";

                    //database에 넣을 데이터
                    String data = URLEncoder.encode("guild_name", "UTF-8") + "=" + URLEncoder.encode(guildname, "UTF-8");
                    data += "&" + URLEncoder.encode("guild_master", "UTF-8") + "=" + URLEncoder.encode(guildmaster, "UTF-8");
                    data += "&" + URLEncoder.encode("guild_explain", "UTF-8") + "=" + URLEncoder.encode(guildexplain, "UTF-8");


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
                        if(line.equals("길드 생성 완료")){
                            System.out.println("이프문 진입");
                            Intent intent = new Intent(GuildMake.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            System.out.println("엘스");
                        }
                        break;
                    }
                    return stringBuilder.toString();
                }catch (Exception e){
                    return new String("Exception : "+e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(guildname,guildmaster,guildexplain);
    }
}

