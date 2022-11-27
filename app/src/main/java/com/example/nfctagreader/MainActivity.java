package com.example.nfctagreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
   NfcAdapter nfcAdapter;
   PendingIntent pendingIntent;
   IntentFilter writingTagFilters[];
   boolean writeMode;
   Tag myTag;
Context context;
TextView edit_message;
TextView nfc_contents;
Button ActivateButton;
Button SubButton;
Button AddButton;
TextView BalanceCount;
ImageView imageView;
    String text="";
    int userid;
    String username;
    float balance;
    List<User> usersList=new ArrayList<User>();
    int userCounter=0;
    int currentuserid;
    User currentuser;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       imageView=(ImageView) findViewById(R.id.imageView);
       imageView.setImageResource(R.drawable.nfc);
        edit_message=(TextView) findViewById(R.id.edit_message);
       nfc_contents= (TextView) findViewById(R.id.nfc_contents);
        ActivateButton= findViewById(R.id.ActivateButton);
         SubButton=findViewById(R.id.subbalance);
        AddButton=findViewById(R.id.addbalance);
      BalanceCount=findViewById(R.id.balancecount);

     context=this;






     AddButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
    currentuser.addBalance(Float.parseFloat(BalanceCount.getText().toString()));
     usersList.set(currentuserid-1,currentuser);
             nfc_contents.setText("ID:"+currentuser.getId()+"\n"+"\n"+"Name:"+currentuser.getName()+"\n"+"\n"+"Balance:"+currentuser.getBalance());
             imageView.setImageResource(R.drawable.ba);
         }
     });
     SubButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             currentuser.subBalance(Float.parseFloat(BalanceCount.getText().toString()));
             usersList.set(currentuserid-1,currentuser);
             nfc_contents.setText("ID:"+currentuser.getId()+"\n"+"\n"+"Name:"+currentuser.getName()+"\n"+"\n"+"Balance:"+currentuser.getBalance());
             imageView.setImageResource(R.drawable.bs);
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
                usersList.add(u);
                Toast.makeText(context,"user added",Toast.LENGTH_LONG).show();
            nfc_contents.setText("ID:"+currentuser.getId()+"\n"+"\n"+"Name:"+currentuser.getName()+"\n"+"\n"+"Balance:"+currentuser.getBalance());




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

       //String tagId=new String(msgs[0].getRecords()[0].getType());
    byte[]payload=msgs[0].getRecords()[0].getPayload();
    String textEncoding=((payload[0]&128)==0)?"UTF-8":"UTF-16";
    int languageCodeLength=payload[0]& 0063;
    //String languageCode=new String(payload,1,languageCodeLength,"US-ASCII");
    try{
       //get the text
        text=new String(payload,languageCodeLength+1,payload.length-languageCodeLength-1,textEncoding);
    }catch (Exception e){
        Log.e("Unsupportedencoding",e.toString());
    }


    imageView.setImageResource(R.drawable.tg);


if(usersList.size()>=Integer.parseInt(text)) {
    currentuserid = Integer.parseInt(text);
    currentuser = usersList.get(currentuserid - 1);

    nfc_contents.setText("Userid: " + text);
    nfc_contents.setText("ID:" + currentuser.getId() + "\n" + "\n" + "Name:" + currentuser.getName() + "\n" + "\n" + "Balance:" + currentuser.getBalance());

}else{
    imageView.setImageResource(R.drawable.ng);
    Toast.makeText(context,"cant find this user",Toast.LENGTH_LONG).show();
}





   }
private void write(String text, Tag tag) throws IOException,FormatException{


    NdefRecord[] records={ createRecord(text) };
    NdefMessage message=new NdefMessage(records);
    Ndef ndef= Ndef.get(tag);
    ndef.connect();
    ndef.writeNdefMessage(message);
    ndef.close();

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

    private void WriteModeOn(){
       writeMode=true;
       nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);
    }
private void WriteModeOff(){
       writeMode=false;
       nfcAdapter.disableForegroundDispatch(this);
}







}