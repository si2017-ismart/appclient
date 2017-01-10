package com.projetintensif;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String keyFirstName = "firstNameKey";
    public static final String keyLastName = "lastNameKey";
//    public static final String keyEmail = "emailKey";
    public static final String keyGender = "genderKey";

    private EditText ETFirstName;
    private EditText ETLastName;
//    private EditText ETEmail;
    private RadioGroup RGGender;
    private RadioButton RBMale;
    private RadioButton RBFemale;
    private Button BSignup;
    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(keyFirstName,null)!=null) {
            Toast.makeText(this, "already registered", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, AnotherActivity.class);
//            startActivity(intent);
        }

        ETFirstName = (EditText)findViewById(R.id.input_first_name);
        ETLastName  = (EditText)findViewById(R.id.input_last_name);
//        ETEmail     = (EditText)findViewById(R.id.input_email);
        RGGender    = (RadioGroup)findViewById(R.id.radio_group_gender);
        RBMale      = (RadioButton)findViewById(R.id.radioButtonMale);
        RBFemale    = (RadioButton)findViewById(R.id.radioButtonFemale);
        BSignup     = (Button)findViewById(R.id.btn_signup);

        BSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(checkEmptyFields()){
            progressDialog = ProgressDialog.show(SignUp.this, "Signing Up ...", "Please Wait ...", true);
            progressDialog.setCancelable(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Send data to server ....
                        //
                        // ------------------------
                        saveData();

                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                    progressDialog.dismiss();
                }
            }).start();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(SignUp.this);
            alert.setTitle("Empty Fields");
            alert.setMessage("Please fill in all the fields to proceed");
            alert.setPositiveButton("OK", null);
            alert.setCancelable(true);
            alert.show();

        }
    }

    private boolean checkEmptyFields(){
        boolean allFilled;

        if(ETFirstName.getText().toString().isEmpty() ||
                ETLastName.getText().toString().isEmpty() ||
//                ETEmail.getText().toString().isEmpty()||
                RGGender.getCheckedRadioButtonId() == -1){
            allFilled = false;
        }else{
            allFilled = true;
        }
        return allFilled;
    }

    private void saveData(){
        String fname, lname, email, gender;
        fname = ETFirstName.getText().toString();
        lname = ETLastName.getText().toString();
//        email = ETEmail.getText().toString();
        if(RGGender.getCheckedRadioButtonId() == RBFemale.getId())
            gender = "female";
        else{
            gender = "male";
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyFirstName, fname);
        editor.putString(keyLastName, lname);
//        editor.putString(keyEmail, email);
        editor.putString(keyGender, gender);
        editor.commit();
    }
}
