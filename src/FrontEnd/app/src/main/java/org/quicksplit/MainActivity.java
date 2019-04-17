package org.quicksplit;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mLabelErrorUserName;
    private EditText mTextUserName;
    private TextInputLayout mLabelErrorPassword;
    private EditText mTextPassword;
    private Button mButtonLogin;
    private TextView mTextViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewRegister = (TextView) this.findViewById(R.id.txtView_register);
        mTextViewRegister.setOnClickListener(this);

        mButtonLogin = (Button) this.findViewById(R.id.btn_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
