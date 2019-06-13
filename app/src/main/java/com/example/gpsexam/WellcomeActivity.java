package com.example.gpsexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class WellcomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String Tag ="Registro" ;

    private Button btnCreateAccount;
    private Button btnSignIn;

    private EditText edtEmail;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        //Get buttons from view
        btnCreateAccount = (Button)  findViewById(R.id.btnCreateAccount);
        btnSignIn = (Button)  findViewById(R.id.btnSignIn);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        inicialize();


        //Metodo btnCreateAccount
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(edtEmail.getText().toString(), edtPassword.getText().toString());


            }
        });

    }

    private void inicialize(){
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.w(Tag, "onAuthStateChanged - signed_in"+ firebaseUser.getUid());
                    Log.w(Tag, "onAuthStateChanged - signed_in"+ firebaseUser.getEmail());
                } else {
                    Log.w(Tag, "onAuthStateChanged - signed_out");
                }
            }
        };
    }


    private void  createAccount(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(WellcomeActivity.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WellcomeActivity.this, Posision.class));//Manda a llamar la activity de La geolocalizacion
                } else{ if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(WellcomeActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WellcomeActivity.this, "Cuenta no creada", Toast.LENGTH_SHORT).show();
                }
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}
