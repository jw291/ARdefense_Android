package com.example.jaewonna.defense;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class GuildActivitySelect {
    public static String isguild;
    public GuildActivitySelect(String name){
        class InsertData extends AsyncTask<String,Void,String>
        {
            //ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String name = (String)params[0];

                  //  System.out.println(name);
                    //내 웹서버 url 입력
                    String link = "http://13.124.189.174/isguild.php";

                    //database에 넣을 데이터
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

                    //System.out.println("urlencode 다음");
                    //url 연결
                    URL url = new URL(link);
                    URLConnection urlConnection = url.openConnection();

                 //   System.out.println("connection 다음");
                    //연결한 url에 outputstreamwriter로 값을 쓸 수 있다.
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                   // System.out.println("outputstream 다음");
                    //내가 정해놓은 데이터를 씀.
                    wr.write(data);
                    wr.flush();

                    //System.out.println("wr 다음");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    StringBuilder stringBuilder = new StringBuilder();
                    isguild = null;
                    //read server Response
                    while((isguild = reader.readLine())!=null){
                        stringBuilder.append(isguild);
                        System.out.println("line = "+isguild);
                        break;
                    }
                    return stringBuilder.toString();
                }catch (Exception e){
                    return new String("Exception : "+e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(name);
    }
}
