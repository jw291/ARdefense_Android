package com.example.jaewonna.defense;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    public static String myname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //툴바 온클릭 리스너
        toolbar.findViewById(R.id.freechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new Freechat()).commit();

            }
        });
        toolbar.findViewById(R.id.guildchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new GuildChat()).commit();

            }
        });
        toolbar.findViewById(R.id.game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        toolbar.findViewById(R.id.guild).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_framelayout,new Guild()).commit();

                }
        });
        //네비게이션 불러오기
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        //네비게이션의 클릭 이벤트 NavigationView이벤트로 하면 됌.
        NavigationView navigationView = findViewById(R.id.main_navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.first) {
                    getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new Freechat()).commit();
                    Toast.makeText(MainActivity.this, "공지사항 클릭", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.second) {
                    getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new secondfragment()).commit();
                    Toast.makeText(MainActivity.this, "사진 클릭", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);//클릭하면 네비게이션이 사라지게 함
                return true;
            }});
        //네비게이션의 헤더는 side_bar.xml
        View nav_header_view = navigationView.getHeaderView(0);
        ImageView nav_header_profile_img = (ImageView)nav_header_view.findViewById(R.id.profileimg);
        TextView nav_header_name_text = (TextView)nav_header_view.findViewById(R.id.myname);

        //헤더에 로그인한 정보 넣기
        Intent intent  = getIntent();
        myname = intent.getExtras().getString("myname");
        String profileimg = intent.getExtras().getString("profileimg");
        nav_header_name_text.setText(myname);
        Glide.with(this).load(profileimg).into(nav_header_profile_img);

        /*
        //스테이지 버튼 온클릭 리스너
        Button.OnClickListener onClickListener = new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.stage1button: Intent intent1 = new Intent(MainActivity.this,UnityPlayerActivity.class);
                    startActivity(intent1);break;
                    case R.id.stage2button:break;
                    case R.id.stage3button:break;
                    case R.id.stage4button:break;
                    case R.id.stage5button:break;
                    case R.id.stage6button:break;
                    case R.id.stage7button:break;
                }
            }
        };
        */
       // Toast.makeText(this, "oncreate 진입", Toast.LENGTH_SHORT).show();
        GuildActivitySelect guildActivitySelect = new GuildActivitySelect(myname);
    }
}
