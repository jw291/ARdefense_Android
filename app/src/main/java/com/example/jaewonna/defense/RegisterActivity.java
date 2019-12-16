package com.example.jaewonna.defense;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_id;
    private EditText et_pw;
    private EditText et_repw;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = (EditText)findViewById(R.id.id_register_et);
        et_pw = (EditText)findViewById(R.id.pw_register_et);
        et_repw = (EditText)findViewById(R.id.repw_register_et);
        et_name = (EditText)findViewById(R.id.nickname_register_et);
    }

    //폼에서 값을 얻어내 데이터베이스에 넣는 메서드
    public void insert(View view){
        String id = et_id.getText().toString();
        String pw = et_pw.getText().toString();
        String pw2 = et_repw.getText().toString();
        String name = et_name.getText().toString();

        insertToDatabase(id,pw,pw2,name);


    }

    public void insertToDatabase(String id, String pw,String pw2, String name){
        class InsertData extends AsyncTask<String,Void,String>
        {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,"기다려주세요",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String id = (String)params[0];
                    String pw = (String)params[1];
                    String pw2 = (String)params[2];
                    String name = (String)params[3];

                    //내 웹서버 url 입력
                    String link = "http://13.124.189.174/androidregister.php";

                    //database에 넣을 데이터
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("pw", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
                    data += "&" + URLEncoder.encode("pw2", "UTF-8") + "=" + URLEncoder.encode(pw2, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");

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

                    System.out.println("값 받아오기");
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    //read server Response
                    while((line = reader.readLine())!=null){
                        stringBuilder.append(line);
                        System.out.println("line = "+line);
                        if(line.equals("회원가입 완료")){
                            System.out.println("이프문 진입");
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
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
        task.execute(id,pw,pw2,name);
    }
}
