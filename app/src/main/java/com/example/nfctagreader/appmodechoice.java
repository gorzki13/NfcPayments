package com.example.nfctagreader;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class appmodechoice extends AppCompatActivity {
    Button register;
    Button terminal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmodechoice);
        Intent i = new Intent(this, Terminal.class);
      register=findViewById(R.id.register);
         terminal=findViewById(R.id.terminal);

        terminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(i);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });



    }
}