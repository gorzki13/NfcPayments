package com.example.nfctagreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String Error_Detected="No NFC Tag Detected";
    public static final String Write_Success="Data written";
    public static final String Write_error="write error";
  DatabaseHelper myDb;

   NfcAdapter nfcAdapter;
   PendingIntent pendingIntent;
   IntentFilter writingTagFilters[];
   boolean writeMode=true;
   Tag myTag;
    Context context;
    TextView edit_message;
    TextView nfc_contents;
    TextView idinput;
    Button ActivateButton;

    ImageView imageView;
    String text="";
    List<User> usersList=new ArrayList<User>();
    int userCounter=0;
    int currentuserid;
    User currentuser;
    Button btnviewAll;
    Button btndelete;
    Button btniddelete;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnviewAll = findViewById(R.id.btnviewall);
        imageView=(ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.nfc);
        edit_message=(TextView) findViewById(R.id.edit_message);
        nfc_contents= (TextView) findViewById(R.id.nfc_contents);
        ActivateButton= findViewById(R.id.ActivateButton);

       btndelete=findViewById(R.id.btndelete);
       myDb= new DatabaseHelper(this);
       idinput=findViewById(R.id.idinput);
       btniddelete=findViewById(R.id.btndeletebyid);
       viewAll();





      Intent i = new Intent(this, appmodechoice.class);
       startActivity(i);
     context=this;
       btnviewAll.setOnClickListener(
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Cursor res = myDb.getAllData();

                       if(res.getCount() == 0) {

                           showMessage("Error","Nothing found");
                           Toast.makeText(context,"dd",Toast.LENGTH_LONG).show();
                           return;
                       }

                       StringBuffer buffer = new StringBuffer();
                       while (res.moveToNext()) {
                           buffer.append("Id :"+ res.getString(0)+"\n");
                           buffer.append("TagID :"+ res.getString(1)+"\n");
                           buffer.append("Name :"+ res.getString(2)+"\n");
                           buffer.append("Balance :"+ res.getString(3)+"\n");
                       }
                       showMessage("Data",buffer.toString());
                   }
               }
       );



       btndelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              myDb.deleteData(String.valueOf(currentuserid));
           }
       });

       btniddelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                String text="";
                text=idinput.getText().toString();
               myDb.deleteData(text);
           }

       });

        ActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.setImageResource(R.drawable.ua);
                try{
                    if(myTag==null){

                    }else{
                       write(""+edit_message.getText()+toString(),myTag);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }catch(FormatException e){
                    e.printStackTrace();
                }
                userCounter++;
                User u=new User(userCounter, edit_message.getText().toString(),0.0f);
                currentuser=u;
               currentuserid=userCounter;
               myDb.insertData(String.valueOf(u.getId()), u.getName(), String.valueOf(u.getBalance()));
                usersList.add(u);
                Toast.makeText(context,"user added",Toast.LENGTH_LONG).show();
                Cursor res = myDb.getAllData();
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    if(res.getString(1).equals(String.valueOf(currentuserid))){


                        buffer.append("Id :"+ res.getString(0)+"\n");
                        buffer.append("TagID :"+ res.getString(1)+"\n");
                        buffer.append("Name :"+ res.getString(2)+"\n");
                        buffer.append("Balance :"+ res.getString(3)+"\n");



                    }
                    nfc_contents.setText(buffer);
                }




            }
        });

        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(this,"nfc not found",Toast.LENGTH_LONG).show();
            finish();
        }
        readfromIntent(getIntent());
        pendingIntent=PendingIntent.getActivity(this,0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter tagDetected=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
       writingTagFilters=new IntentFilter[]{tagDetected};
    }





@Override
    protected void  onNewIntent(Intent intent){
       super.onNewIntent(intent);
       setIntent((intent));
       readfromIntent(intent);
       if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
           myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
       }
    }
    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();

    }
    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();

    }



    public void viewAll() {

    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }



    private void WriteModeOn(){
        writeMode=true;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);
    }
    private void WriteModeOff(){
        writeMode=false;
        nfcAdapter.disableForegroundDispatch(this);
    }



    private void readfromIntent(Intent intent){
        String action=intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)||NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            Parcelable[] rawMsgs=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs=null;
            if(rawMsgs!=null){
                msgs =new NdefMessage[rawMsgs.length];
                for(int i=0;i<rawMsgs.length;i++){
                    msgs[i]=(NdefMessage) rawMsgs[i];
                }
                buildTagViews(msgs);
            }
        }
    }

    private void buildTagViews(NdefMessage[] msgs){
        if(msgs==null||msgs.length==0)return;


        byte[]payload=msgs[0].getRecords()[0].getPayload();
        String textEncoding=((payload[0]&128)==0)?"UTF-8":"UTF-16";
        int languageCodeLength=payload[0]& 0063;

        try{

            text=new String(payload,languageCodeLength+1,payload.length-languageCodeLength-1,textEncoding);
        }catch (Exception e){
            Log.e("Unsupported encoding",e.toString());
        }


        imageView.setImageResource(R.drawable.tg);
        boolean find=false;
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if (res.getString(1).equals(text)) {
                currentuserid = Integer.parseInt(text);







                while (res.moveToNext()) {
                    if (res.getString(1).equals(String.valueOf(currentuserid))) {
                        StringBuffer buffer = new StringBuffer();

                        buffer.append("Id :" + res.getString(0) + "\n");
                        buffer.append("TagID :" + res.getString(1) + "\n");
                        buffer.append("Name :" + res.getString(2) + "\n");
                        buffer.append("Balance :" + res.getString(3) + "\n");

                        nfc_contents.setText(buffer);
                        find = true;
                    }



                }
            }
        }
     if(!find) {
            imageView.setImageResource(R.drawable.ng);
            Toast.makeText(context,"cant find this user",Toast.LENGTH_LONG).show();
        }
    }



    private NdefRecord createRecord(String text) throws UnsupportedEncodingException{
        String lang="en";
        byte[] textBytes=text.getBytes();
        byte[] langBytes=lang.getBytes("US-ASCII");
        int langLenght=langBytes.length;
        int textLength=textBytes.length;
        byte[]payload=new byte[1+langLenght+textLength];
        payload[0]=(byte) langLenght;
        System.arraycopy(langBytes,0,payload,1,langLenght);
        System.arraycopy(langBytes,0,payload,1+langLenght,textLength);
        NdefRecord recordNFC=new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload);
        return recordNFC;
    }
    private void write(String text, Tag tag) throws IOException,FormatException{
        NdefRecord[] records={ createRecord(text) };
        NdefMessage message=new NdefMessage(records);
        Ndef ndef= Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();

    }

}