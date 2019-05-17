package org.quicksplit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button gotoEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gotoEdit = (Button) findViewById(R.id.btnTestGotoEdit);
        gotoEdit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //Intent modifyUser = new Intent(this, ModifyUserActivity.class);
        //startActivity(modifyUser);
        Intent createGroup = new Intent(this, CreateGroupActivity.class);
        startActivity(createGroup);
    }
}
