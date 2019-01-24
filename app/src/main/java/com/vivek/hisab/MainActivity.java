package com.vivek.hisab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.text.TextUtils;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button login;
    private EditText email;
    private EditText password;
    private TextView signup;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login1);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signup = (TextView) findViewById(R.id.signup);
        progressDialog = new ProgressDialog(this);

        if(mAuth.getCurrentUser() != null){
            // start profile acivity
        }
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

    }

    private void userlogin(){
        String mailId = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(mailId)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG);
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT);
        }

        progressDialog.setMessage("Registering please wait ..........");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(mailId,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Login sucessfull",Toast.LENGTH_SHORT).show();
                            // start profile activity
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(v == signup){
            finish();
            startActivity(new Intent(this,SignupActivity.class));
        }
        if(v == login){
            userlogin();
        }
    }
}
