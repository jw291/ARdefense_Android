package com.example.jaewonna.defense;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private LoginButton facebookloginButton;
    private CallbackManager callbackManager;
    SessionCallback callback;
    private Button fakekakao;
    private Button fakefacebook;
    private com.kakao.usermgmt.LoginButton kakaologinButton;
    private Button login_register;

    //로그인
    ViewFlipper viewFlipper;
    Button loginbutton;
    EditText userid, userpw;
    HttpPost httpPost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpClient;
    ArrayList<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        setContentView(R.layout.activity_login);

        fakekakao = (Button)findViewById(R.id.fake_kakao_login_button);
        fakekakao.setOnClickListener(this);
        fakefacebook = (Button)findViewById(R.id.fake_facebook_login_button);
        fakefacebook.setOnClickListener(this);
        kakaologinButton = (com.kakao.usermgmt.LoginButton) findViewById(R.id.kakao_login_button);


        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자
        facebookloginButton = (LoginButton)findViewById(R.id.facebook_login_button); //페이스북 로그인 버튼
        //유저 정보, 친구정보, 이메일 정보등을 수집하기 위해서는 허가(퍼미션)를 받아야 합니다.
        facebookloginButton.setReadPermissions("public_profile", "user_friends","email");
        //버튼에 바로 콜백을 등록하는 경우 LoginManager에 콜백을 등록하지 않아도됩니다.
        //반면에 커스텀으로 만든 버튼을 사용할 경우 아래보면 CustomloginButton OnClickListener안에 LoginManager를 이용해서
        //로그인 처리를 해주어야 합니다.
        facebookloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { //로그인 성공시 호출되는 메소드
                Log.e("토큰",loginResult.getAccessToken().getToken());
                Log.e("유저아이디",loginResult.getAccessToken().getUserId());
                Log.e("퍼미션 리스트",loginResult.getAccessToken().getPermissions()+"");
                final Profile profile = Profile.getCurrentProfile();
                //loginResult.getAccessToken() 정보를 가지고 유저 정보를 가져올수 있습니다.
                GraphRequest request =GraphRequest.newMeRequest(loginResult.getAccessToken() ,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Log.e("user profile",object.toString());
                                    String facebookname = profile.getName();
                                    String facebookprofileimg = profile.getProfilePictureUri(200,200).toString();
                                    //Toast.makeText(LoginActivity.this, facebookname, Toast.LENGTH_LONG).show();
                                    //Toast.makeText(LoginActivity.this, facebookprofileimg, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    facebookinfo(intent,facebookname,facebookprofileimg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.executeAsync();


            }

            @Override
            public void onError(FacebookException error) { }

            @Override
            public void onCancel() { }
        });


        /**카카오톡 로그아웃 요청**/
        //한번 로그인이 성공하면 세션 정보가 남아있어서 로그인창이 뜨지 않고 바로 onSuccess()메서드를 호출합니다.
        //테스트 하시기 편하라고 매번 로그아웃 요청을 수행하도록 코드를 넣었습니다 ^^
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후 하고싶은 내용 코딩 ~
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        login_register = (Button)findViewById(R.id.login_register);
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        /**간편로그인이 아닌 직접 로그인**/
        //회원가입에서 작성한 아이디 비밀번호와 비교하여 로그인
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loginbutton = (Button)findViewById(R.id.Login_button);
        userid = (EditText)findViewById(R.id.id_login_et);
        userpw = (EditText)findViewById(R.id.pw_login_et);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this,"","Validating user....",true);
                //쓰레드를 이용해서 login()메소드를 호출하고 있는데
                //login()메소드에서도 쓰레드가 이용되고 있다.
                //쓰레드 내에 쓰레드는 이용할 수 없기 때문에
                //Handler handler = new Handler(Looper.getMainLooper());를 이용해서 호출하였다.
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login();
                    }
                },0);
            }
        });
    }
    /**로그인 apache client 라이브러리 이용**/
    void login() {
        try {
            httpClient = new DefaultHttpClient(); //apache httpclient이용
            httpPost = new HttpPost("http://13.124.189.174/androidlogin.php");//url연결
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userid", userid.getText().toString()));//"id","pw"는 php에서 post로 전달받을 변수명이므로 정확히 기재
            nameValuePairs.add(new BasicNameValuePair("userpw", userpw.getText().toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httpPost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    //tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            if (response.equalsIgnoreCase("User Found")) { //User Found값이 반환되면 로그인 성
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent loginintent = new Intent(LoginActivity.this, MainActivity.class);
                logininfo(loginintent, userid.getText().toString());

            } else {
                Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fake_kakao_login_button:
                kakaologinButton.performClick();
                break;
            case R.id.fake_facebook_login_button:
                facebookloginButton.performClick();
                break;
        }
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                    Log.e("UserProfile", userProfile.toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    String kakaoname = userProfile.getNickname();
                    String kakaoimg = userProfile.getProfileImagePath();
                    kakaoinfo(intent,kakaoname,kakaoimg);
                    finish();
                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ
        }
    }

    public void logininfo(Intent intent, String name){
        intent.putExtra("myname",name);
        startActivity(intent);
    }
    public void facebookinfo(Intent intent , String facebookname, String facebookimg){
        intent.putExtra("myname",facebookname);
        intent.putExtra("profileimg",facebookimg);
        startActivity(intent);
    }

    public void kakaoinfo(Intent intent , String kakaoname, String kakaoimg){
        intent.putExtra("myname",kakaoname);
        intent.putExtra("profileimg",kakaoimg);
        startActivity(intent);
    }

}