package com.example.jaewonna.defense;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GuildChat extends Fragment {
    RecyclerView mVerticalView;
    GuildchatAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<GuildchatProduct> data;

    GuildChatSocketClient guildChatSocketClient;
    GuildChatSendThread guildChatSendThread;
    GuildChatReceiveThread guildChatReceiveThread;
    Handler msgHandler;
    Socket socket;

    Button sendbutton;
    EditText sendeditText;

    String myname;
    String mykey;
    class GuildchatProduct {

        private String name;
        private String content;

        public GuildchatProduct(String name, String content){
            this.name = name;
            this.content = content;
        }


        public String setName(String name) {return this.name = name;}

        public String setContent(String contet){return this.content = content;}

        public String getName() {
            return this.name;
        }

        public String getContent() {
            return this.content;
        }
    }

    class GuildchatViewHolder extends RecyclerView.ViewHolder {

        public TextView name,content;

        public GuildchatViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.guildchatmyname);
            content = (TextView) itemView.findViewById(R.id.guildchatcontent);

        }
    }

    class GuildchatAdapter extends RecyclerView.Adapter<GuildchatViewHolder> {

        private ArrayList<GuildchatProduct> verticalDatas;

        public void setData(ArrayList<GuildchatProduct> list){
            verticalDatas = list;
        }

        @Override
        public GuildchatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

// 사용할 아이템의 뷰를 생성해준다.
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.guild_chat_item, parent, false);

            GuildchatViewHolder holder = new GuildchatViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(GuildchatViewHolder holder, int position) {
            GuildchatProduct data = verticalDatas.get(position);
            String itemname = verticalDatas.get(position).getName();
            System.out.println("itemname"+itemname);
            if(myname.equals(itemname)) {
                holder.name.setTextColor(Color.parseColor("#FF006E02"));
                holder.content.setTextColor(Color.parseColor("#FF006E02"));
                holder.name.setText(data.getName());
                holder.content.setText(data.getContent());
            }else{
                holder.name.setTextColor(Color.parseColor("#FF000000"));
                holder.content.setTextColor(Color.parseColor("#FF000000"));
                holder.name.setText(data.getName());
                holder.content.setText(data.getContent());
            }

        }

        @Override
        public int getItemCount() {
            return verticalDatas.size();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_guild_chat, container, false);
        //System.out.println("find가 되니"+edit);
        sendbutton = (Button)view.findViewById(R.id.guildchatsendbutton);
        sendeditText=(EditText)view.findViewById(R.id.guildchatsendedit);
        return view;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.myname = MainActivity.myname;
        this.mykey = GuildActivitySelect.isguild;

        GuildChatSocketClient guildChatSocketClient = new GuildChatSocketClient();
        guildChatSocketClient.start();

        mVerticalView = (RecyclonviewerView)view.findViewById(R.id.guildchatrecyclerview);


        SQLiteSelect();

        mVerticalView.post(new Runnable() {
            @Override
            public void run() {
                mVerticalView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
        //mVerticalView.smoothScrollToPosition(mAdapter.getItemCount());
        //핸들메세지와 보내기 버튼
        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1111){
                    System.out.println("핸들 메세지로 들어옴");
                    String message = msg.obj.toString();
                    System.out.println(message);
                    StringTokenizer st = new StringTokenizer(message,":");
                    String key = st.nextToken(":");
                    String name = st.nextToken(":");
                    String content = st.nextToken(":");
                    if(!myname.equals(name) ){
                        if(mykey.equals(key)){
                            data.add(new GuildchatProduct(name, content));
                            mAdapter.notifyDataSetChanged();
                            SQLiteInsert(key,name,content);
                        }//Toast.makeText(getActivity(), "내껀 바로 띄운다.", Toast.LENGTH_SHORT).show();
                    }

                    mVerticalView.smoothScrollToPosition(mAdapter.getItemCount());
                }

            }

        };

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendeditText.getText().toString() != null){//메세지 상자가 널이 아니고 빈값도 아니면 보낸
                    System.out.println(sendeditText.getText().toString());
                    data.add(new GuildchatProduct(myname,sendeditText.getText().toString()));
                    mAdapter.notifyDataSetChanged();
                    guildChatSendThread = new GuildChatSendThread(socket, mykey , myname,sendeditText.getText().toString());
                    guildChatSendThread.start();

                    SQLiteInsert(mykey,myname,sendeditText.getText().toString());
                    sendeditText.setText("");

                    mVerticalView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }
        });
    }


    public void SQLiteInsert(String chat_key, String user_id, String user_content){
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into chat(chat_key,user_id,user_content) values ('"+chat_key+"','"+user_id+"','"+user_content+"')");
        db.close();
    }

    public void SQLiteSelect(){
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        data = new ArrayList<>();

        System.out.println("sqliteselect 진입");
        Cursor cursor = db.rawQuery("select user_id, user_content from chat where chat_key = '"+mykey+"'",null);




            while (cursor.moveToNext()) {

                String user_id = cursor.getString(0);
                String user_content = cursor.getString(1);

                System.out.println("uesr_id : " + user_id + " " + "user_content : " + user_content + " ");
                data.add(new GuildchatProduct(user_id, user_content));

            }
            db.close();

        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 기본값이 VERTICAL

        // setLayoutManager
        mVerticalView.setLayoutManager(mLayoutManager);
        mAdapter = new GuildchatAdapter();

        //mysql에서 불러오기
        mAdapter.setData(data);
        // set Adapter
        mVerticalView.setAdapter(mAdapter);

        System.out.println(mAdapter.getItemCount()+"키자마자 포지션 다운");
    }

    //내부 클래스
    //서버의 아이피와 포트 번호가 전달된다.
    class GuildChatSocketClient extends Thread{
        boolean threadAlive; //스레드의 동작 여부
        String ip;
        String port;
        OutputStream outputStream = null;
        BufferedReader br= null;
        DataOutputStream output = null;
        public GuildChatSocketClient(){
            threadAlive = true;
            this.ip= "13.124.189.174";
            this.port = "8888";

        }
        public void run(){
            try{
                //채팅 서버에 접속
                //서버에서 serversocket.accept가 풀리면서 소켓이 만들어짐
                socket = new Socket(ip, Integer.parseInt(port));
                //서버에 메세지를 전다하기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
                //메세지 수신용 스레드 생성
                guildChatReceiveThread = new GuildChatReceiveThread(socket);
                guildChatReceiveThread.start();
                //와이파이 정보 관리자 객체로부터 폰의 mac address를 가져와서
                //채팅 서버에 전
                output.writeUTF(myname);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }//end of socketclinet
    //내부 클래스
    //서버에서 도착한 메세지를 받아서 핸들러한테 넘기고 핸들러가 화면에 표시하는 수신용 핸들러
    class GuildChatReceiveThread extends Thread{
        Socket socket = null;
        DataInputStream input = null;
        public GuildChatReceiveThread(Socket socket){
            this.socket = socket;
            try{
                input = new DataInputStream (socket.getInputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public void run(){
            try{
                while(input != null){
                    //채팅 서버로부터 받은 메세지
                    String msg = input.readUTF();
                    StringTokenizer st = new StringTokenizer(msg,":");
                    String key = st.nextToken(":");
                    System.out.println(msg);
                        if (msg != null) {
                            //핸들러에게 전달할 메세지 객체
                            Message hdmsg = msgHandler.obtainMessage();
                            hdmsg.what = 1111;//메시지의 식별자
                            hdmsg.obj = msg;//메시지의 본문
                            //핸들러에게 메시지 전달(화면 변경 요청)
                            msgHandler.sendMessage(hdmsg);

                        }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }//end of ReceiveThread
    //내부클래스
    class  GuildChatSendThread extends Thread{
        Socket socket;
        String key;
        String name;
        String content;
        DataOutputStream output;
        public GuildChatSendThread(Socket socket,String key,String name, String content){
            this.content = content;
            this.key = key;
            this.name = name;
            this.socket = socket;
            try{
                output = new DataOutputStream(socket.getOutputStream());

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void run(){
            try {
                if(output != null){
                    if(this.content != null){
                        //채팅 서버에 전달
                        //누가 무엇을 보냈다.
                        System.out.println(this.name+":"+this.content);
                        output.writeUTF(this.key+":"+this.name +":"+this.content);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
