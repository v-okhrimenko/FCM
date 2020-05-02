//package com.example.fcm;
//
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//import com.example.fcm.models.User;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.rengwuxian.materialedittext.MaterialEditText;
//
//public class MainActivity extends AppCompatActivity {
//
//
//    Button btnSignIn, btnRegister;
//
//    FirebaseAuth auth;
//    FirebaseDatabase db;
//    DatabaseReference users;
//    String setmail;
//    ConstraintLayout root;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        btnSignIn = findViewById(R.id.btn_enter);
//        btnRegister = findViewById(R.id.btn_registration);
//
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseDatabase.getInstance();
//        users = db.getReference("Users");
//
//        root = findViewById(R.id.rootelement);
//
//        setmail = getResources().getString(R.string.set_mail_alert);
//
//
//
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showRegistrationWindow();
//            }
//        });
//
//
//
//
//
//    }
//
//    private void showRegistrationWindow() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("Регистрация");
//        dialog.setMessage("Заполните все поля для регистрации");
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View regiserWindow = inflater.inflate(R.layout.registration_layout, null);
//        dialog.setView(regiserWindow);
//
//        final MaterialEditText email = regiserWindow.findViewById(R.id.emailField);
//        final MaterialEditText password = regiserWindow.findViewById(R.id.passlField);
//        final MaterialEditText name = regiserWindow.findViewById(R.id.nameField);
//
//        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int which) {
//                dialogInterface.dismiss();
//            }
//        });
//
//        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int which) {
//                if (TextUtils.isEmpty(email.getText().toString())) {
//                    Snackbar.make(root, setmail, Snackbar.LENGTH_SHORT).show();
//                    return;
//
//                }
//
//                if (TextUtils.isEmpty(name.getText().toString())) {
//                    Snackbar.make(root, "Укажите Ваше имя", Snackbar.LENGTH_SHORT).show();
//                    return;
//
//                }
//
//                if (password.getText().toString().length() < 5) {
//                    Snackbar.make(root, "Укажите пароль более 5ти символов", Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                //Регистрация пользователя
//                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
//                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//                                User user = new User();
//                                user.setEmail(email.getText().toString());
//                                user.setName(name.getText().toString());
//                                user.setPassword(password.getText().toString());
//
//                                users.child(user.getEmail())
//                                        .setValue(user)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//
//                            }
//                        });
//
//
//
//            }
//
//        });
//        dialog.show();
//    }
//
//}
package com.example.fcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fcm.helper.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {




    Button btnSignIn, btnRegister, lostpass, regitration_new;
    EditText emailSi;
    EditText passSi;
    TextView signin;
    TextView problem_enter;


    //REGISTRATION LAYOUT
    Boolean emailOk = false;
    Boolean pass1Ok = false;
    Boolean pass2Ok = false;


    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    String setmail;
    ConstraintLayout root;

    android.app.ProgressDialog progressDialog;
    AlertDialog dialog_1;
    FirebaseUser user;





    private void setLocale(String lang) {

        Locale locale = Resources.getSystem().getConfiguration().locale;

//        if(locale.getCountry().equals( "UA" ) || locale.getCountry().equals( "RU" ) ){
//
//            Locale locale_new = new Locale("ru");
//            Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().
                updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration( config, getBaseContext().getResources().getDisplayMetrics() );
//
//        SharedPreferences.Editor editor = getSharedPreferences( "Settings", MODE_PRIVATE ).edit();
//        editor.putString( "My_Lang", lang );
//        editor.apply();

    }


    public void loadLockale(){
        SharedPreferences prefs = getSharedPreferences( "Settings", Activity.MODE_PRIVATE );
        String language = prefs.getString( "My_Lang" , "");
        setLocale( language );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadLockale();
        setContentView(R.layout.activity_main);



        btnSignIn = findViewById(R.id.btn_enter);
        problem_enter = findViewById( R.id.tv_problem_enter );
        //btnRegister = findViewById(R.id.btn_registration);
        //regitration_new = findViewById(R.id.btn_registration_);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        user = auth.getInstance().getCurrentUser();
        users = db.getReference("Users");

        root = findViewById(R.id.rootelement);

        setmail = getResources().getString(R.string.set_mail_alert);

        emailSi = findViewById(R.id.signInEmail);
        passSi = findViewById(R.id.signInPass);

        //lostpass = findViewById(R.id.btnlostpass);

        signin = findViewById( R.id.textView45 );

        check_auth();


        problem_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
                final View regiserWindow = inflater.inflate( R.layout.forgot_pass_inflater, null );
                builder.setView( regiserWindow );

                final Button newPass_ok = regiserWindow.findViewById( R.id.btn_registr );
                final Button newPass_cencel = regiserWindow.findViewById( R.id.cencel_registartion );

                final EditText email = regiserWindow.findViewById( R.id.et_email_registartion );
                final TextView YouEmailTop = regiserWindow.findViewById( R.id.textView31 );
                final TextView noSuchAccaund = regiserWindow.findViewById( R.id.tv_no_such_account );
                final TextView emailIsntCorrect = regiserWindow.findViewById( R.id.tv_EmailIsntCorrect );


                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();

                email.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emailIsntCorrect.setVisibility( View.INVISIBLE );
                        email.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                        YouEmailTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        noSuchAccaund.setVisibility( View.INVISIBLE );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );

                newPass_ok.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showSpinner( getString( R.string.In_the_process ));

                        hideKeyboardFrom( getApplicationContext(),regiserWindow );

                        if(email.getText().toString().isEmpty()){
                            dialog_1.dismiss();
                            email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                            YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );

                        }

                        else {
                            if (Helper.isValidEmail( email.getText().toString().trim() )){

                                //FIREBASE RESTORE

                                FirebaseAuth.getInstance().sendPasswordResetEmail( email.getText().toString().trim() )
                                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    dialog_1.dismiss();
                                                    dialog.dismiss();
                                                    Snackbar.make(root, getString( R.string.Password_reset_link_sent ), Snackbar.LENGTH_LONG).show();

                                                    //System.out.println( "we send you new pass");


                                                }
                                                else {
                                                    dialog_1.dismiss();
                                                    //System.out.println( task.getException().getMessage());
                                                    email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                                    YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                                    noSuchAccaund.setVisibility( View.VISIBLE );


                                                }
                                            }
                                        } );
                            } else {
                                dialog_1.dismiss();
                                email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                emailIsntCorrect.setVisibility( View.VISIBLE );


                            }
                        }
                    }
                } );

                newPass_cencel.setOnClickListener( v1 -> dialog.dismiss() );

//                if (emailSi.getText().toString().equals("")) {
//                    Snackbar.make(root, "Введите адресс электронной почты в поле ввода почты", Snackbar.LENGTH_LONG).show();
//                } else { sendmail();
//                }
            }
        });

//        private void sendmail() {
//            FirebaseAuth.getInstance().sendPasswordResetEmail(emailSi.getText().toString())
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Snackbar.make(root, getString( R.string.new_pass_send ), Snackbar.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//        }




        signin.setOnClickListener( new View.OnClickListener() {
        //regitration_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
                final View regiserWindow = inflater.inflate( R.layout.regitration_layout_inflater, null );
                builder.setView( regiserWindow );




                final Button reg_ok = regiserWindow.findViewById( R.id.btn_registr );
                final Button reg_cencel = regiserWindow.findViewById( R.id.cencel_registartion );

                final EditText email = regiserWindow.findViewById( R.id.et_email_registartion );
                final TextView YouEmailTop = regiserWindow.findViewById( R.id.textView31 );

                final EditText pass1 = regiserWindow.findViewById( R.id.et_pass_registartion );
                final TextView YouPassTop = regiserWindow.findViewById( R.id.textView25 );
                final TextView passNoMatch = regiserWindow.findViewById( R.id.tv_miniminSixChar2 );
                final TextView passNoConteinSpase = regiserWindow.findViewById( R.id.tv_miniminSixChar3 );


                final EditText pass2 = regiserWindow.findViewById( R.id.et_repeat_pass_registartion );
                final TextView repeatYouPassTop = regiserWindow.findViewById( R.id.tv_new_pass_label );
                final TextView passNoMatch2 = regiserWindow.findViewById( R.id.tv_error_new_password2 );


                final TextView enterYouEmailTXT = regiserWindow.findViewById( R.id.tv_needEmail );
                final TextView emailIsntCorrect = regiserWindow.findViewById( R.id.tv_EmailIsntCorrect );

                final TextView minSixChar = regiserWindow.findViewById( R.id.tv_miniminSixChar );

                final ImageView check_ok = regiserWindow.findViewById( R.id.iv_password_check_ok );
                final ImageView check_ok2 = regiserWindow.findViewById( R.id.iv_password2_check_ok );
                final ImageView done = regiserWindow.findViewById( R.id.iv_password2_check_ok );



                check_ok.setVisibility( View.INVISIBLE );
                check_ok2.setVisibility( View.INVISIBLE );

                final Integer[] x = {0};



                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.setCancelable( false );
                dialog.show();


                pass2.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pass2.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                        pass2.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        repeatYouPassTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        passNoMatch2.setVisibility( View.INVISIBLE );


                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );

                pass1.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pass2.getText().clear();
                        passNoMatch.setVisibility( View.INVISIBLE );
                        passNoMatch2.setVisibility( View.INVISIBLE );
                        pass1.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                        pass1.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        //pass2.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                        //pass2.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        YouPassTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                        //repeatYouPassTop.setTextColor( getResources().getColor( R.color.blue_lite ) );

                        x[0] = (pass1.getText().toString()).length();

                        if (s.toString().contains(" ")){
                            passNoConteinSpase.setVisibility( View.VISIBLE );
                            minSixChar.setVisibility( View.INVISIBLE );
                            pass1.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                            YouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                        } else {
                            passNoConteinSpase.setVisibility( View.INVISIBLE );
                            minSixChar.setVisibility( View.VISIBLE );
                            pass1.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                            //pass1.setTextColor( getResources().getColor( R.color.blue_lite ) );
                            YouPassTop.setTextColor( getResources().getColor( R.color.blue_lite ) );

                        }

                        if (x[0]==0) {
                            minSixChar.setVisibility( View.INVISIBLE );

                        }
                        if (x[0] >= 6) {
                            minSixChar.setVisibility( View.INVISIBLE );

                        } else {
                            if (s.toString().contains(" ")) {
                                minSixChar.setVisibility( View.INVISIBLE );
                            } else {
                                minSixChar.setVisibility( View.VISIBLE );
                                passNoMatch.setVisibility( View.INVISIBLE );
                            }



                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );

                email.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emailIsntCorrect.setVisibility( View.INVISIBLE );
                        email.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                        YouEmailTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );

                reg_cencel.setOnClickListener( v1 -> dialog.dismiss() );

                reg_ok.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isCorrectPAss(pass1.getText().toString().trim(), pass2.getText().toString().trim() ) == true
                                && Helper.isValidEmail( email.getText().toString().trim() ) == true){
                            hideKeyboardFrom( getApplicationContext(), regiserWindow );
                            showSpinner("Регистрация ...");
                            regitrUser();
                            //dialog.dismiss();
                        } else {

                            //EMAIL CHECK
                            if (email.getText().toString().trim().isEmpty()){

                                email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                            } else {
                                if(Helper.isValidEmail( email.getText().toString().trim() ) == true) {
                                    System.out.println( "EMAIL is OK" );
                                    emailIsntCorrect.setVisibility( View.INVISIBLE );
                                    email.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                                    YouEmailTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                                    //emailOk = true;

                                } else {
                                    email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                    YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                    emailIsntCorrect.setVisibility( View.VISIBLE );
                                    emailOk = false;

                                }

                            }

                            // PASS CHECK

                            if (pass1.getText().toString().trim().isEmpty() || pass1.getText().toString().trim().length()<5 ){

                                pass1.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                YouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                            }

                            if (pass2.getText().toString().trim().isEmpty()){

                                pass2.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                            }

                            if (pass1.getText().toString().trim().length()>5 && pass2.getText().toString().isEmpty()) {
                                pass2.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );

                            }

                            if (pass1.getText().toString().contains( " " )) {
                                pass2.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                passNoConteinSpase.setVisibility( View.VISIBLE );
                                repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );

                            }



                            if (!pass2.getText().toString().trim().equals( pass1.getText().toString().trim() ) && pass1.length()>5){
                                //pass1.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                pass2.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                //YouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                //repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                //passNoMatch.setVisibility( View.VISIBLE );
                                passNoMatch2.setVisibility( View.VISIBLE );

                            }

                        }
                    }

                    private void regitrUser() {

                        auth.createUserWithEmailAndPassword(email.getText().toString(), pass1.getText().toString())

                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {


                                            //Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                            emailSi.setText(email.getText().toString());
                                            passSi.setText(pass1.getText().toString());

                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                            FirebaseUser user_mail = auth.getCurrentUser();

                                            user_mail.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                dialog.dismiss();
                                                                dialog_1.dismiss();

                                                                //dialog.dismiss();
                                                                //dialog_1.dismiss();
                                                                Snackbar.make(root, getString( R.string.user_add_mail_send ), Snackbar.LENGTH_LONG).show();
                                                                ;
                                                            }
                                                        }
                                                    });

                                        } else  {
                                            Snackbar.make(root, getString( R.string.User_with_such_an_email_exists ), Snackbar.LENGTH_SHORT).show();
                                            email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                            YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                            dialog_1.dismiss();


                                        }
                                    }
                                } );
//                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//
//                                    @Override
//                                    public void onSuccess(final AuthResult authResult) {
//
//
////                                progressDialog.dismiss();
//                                        dialog.dismiss();
//                                        dialog_1.dismiss();
//
//
//                                        emailSi.setText(email.getText().toString());
//                                        passSi.setText(pass1.getText().toString());
//
//                                        User user = new User();
//                                        user.setEmail(email.getText().toString());
//                                        //user.setName(name.getText().toString());
//                                        user.setPassword(pass1.getText().toString());
//
//                                        FirebaseAuth auth = FirebaseAuth.getInstance();
//                                        FirebaseUser user_mail = auth.getCurrentUser();
//
//                                        user_mail.sendEmailVerification()
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                                        if (task.isSuccessful()) {
//                                                            dialog.dismiss();
//                                                            dialog_1.dismiss();
//
//                                                            Snackbar.make(root, "Письмо для активации отправлено на Ваш e-mail!", Snackbar.LENGTH_SHORT).show();
//                                                            ;
//                                                        }
//                                                    }
//                                                });
//                                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Login")
//                                                .setValue(user)
//
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//
//                                                        Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
//
//
//                                                    }
//                                                });
//
//
//
//                                    }
//
//                                });

                    }
                } );



            }});

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showSpinner( getString( R.string.enter_ ) );
                hideKeyboard( MainActivity.this );
                registration();



            }
        });

//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showRegistrationWindow();
//            }
//        });



    }

    private void check_auth() {

        if (auth.getCurrentUser() != null) {
            // already signed in

            System.out.println("ВНУТРИ");

            boolean emailVerified = user.isEmailVerified();

            if (emailVerified==true) {

                System.out.println( "TRYUU" );

                startActivity(new Intent(MainActivity.this, CalendarMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            } else {


//                AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
//                LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
//                final View regiserWindow = inflater.inflate( R.layout.no_activation_inflater, null );
//                builder.setView( regiserWindow );
//
//                final Button send = regiserWindow.findViewById( R.id.btn_registr );
//                final Button cencel = regiserWindow.findViewById( R.id.cencel_registartion );
//
//
//                final AlertDialog dialog = builder.create();
//                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//                dialog.setCancelable( false );
//                dialog.show();
//
//                send.setOnClickListener( new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        showSpinner( getString( R.string.In_the_process ));
//
//                        FirebaseAuth auth = FirebaseAuth.getInstance();
//                        FirebaseUser user_mail = auth.getCurrentUser();
//
//                        user_mail.sendEmailVerification()
//
//                                .addOnFailureListener( new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        System.out.println( e.getMessage().trim() );
//                                        dialog.dismiss();
//                                        dialog_1.dismiss();
//                                        Snackbar.make(root, getString( R.string.somsing_wrong ), Snackbar.LENGTH_LONG).show();
//                                    }
//                                } )
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if (task.isSuccessful()) {
//                                            dialog.dismiss();
//                                            dialog_1.dismiss();
//
//                                            Snackbar.make(root, getString( R.string.email_with_a_activation_sent_email ), Snackbar.LENGTH_LONG).show();
//
//                                        } else {
//                                            System.out.println( task.getException().getMessage() );
//                                        }
//                                    }
//                                });
//
//                        //resend();
//
//                    }
//                } );
//
//                cencel.setOnClickListener( v -> dialog.dismiss() );
//
//
            }

        } else {
            // not signed in
            System.out.println("НАДО РЕГЕСТРИРОВАТСЯ");
//            View view = findViewById(R.id.Drawer_layo);
//            String undo = getString(R.string.UNDO);
//            String itemdel = getString(R.string.Itemdeleted);
//            Snackbar snackbar = Snackbar.make(view, "Ваш аккакнт не активирован. Отпавить повторно ссылку для активации?", Snackbar.LENGTH_LONG);
//            snackbar.setAction("Отправить", v -> resend());
//            snackbar.show();
        }
    }

    private void resend() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user_mail = auth.getCurrentUser();
        //String user_mail = emailSi.getText().toString();
        //System.out.println( user_mail.getEmail() );

        user_mail.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //dialog.dismiss();
                            //dialog_1.dismiss();

                            Snackbar.make(root, getString( R.string.resend_activation ), Snackbar.LENGTH_LONG).show();

                        }
                    }
                });
    }




    private void registration() {

        final TextInputEditText email = findViewById(R.id.signInEmail);
        final TextInputEditText password = findViewById(R.id.signInPass);


        if(email.getText().toString().equals("")|| password.getText().toString().equals("")){
            //dialog_1.dismiss();
            Snackbar.make(root, getString( R.string.Enter_your_email_and_password ), Snackbar.LENGTH_SHORT).show();
           hideKeyboard( MainActivity.this );
           //dialog_1.dismiss();
        } else
            //showSpinner(getString( R.string.enter_ ));
            //showSpinner( getString( R.string.enter_ ) );
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            boolean emailVerified = user.isEmailVerified();
                            System.out.println("--------------"+emailVerified);
                            if (emailVerified==true) {


                                startActivity(new Intent(MainActivity.this, CalendarMainActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                                //dialog_1.dismiss();
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                                LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
                                final View regiserWindow = inflater.inflate( R.layout.no_activation_inflater, null );
                                builder.setView( regiserWindow );

                                final Button send = regiserWindow.findViewById( R.id.btn_registr );
                                final Button cencel = regiserWindow.findViewById( R.id.cencel_registartion );


                                final AlertDialog dialog = builder.create();
                                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                dialog.setCancelable( false );
                                dialog.show();

                                send.setOnClickListener( new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //showSpinner( getString( R.string.In_the_process ));

                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        FirebaseUser user_mail = auth.getCurrentUser();

                                        user_mail.sendEmailVerification()

                                                .addOnFailureListener( new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println( e.getMessage().trim() );
                                                        dialog.dismiss();
                                                        //dialog_1.dismiss();
                                                        Snackbar.make(root, getString( R.string.somsing_wrong ), Snackbar.LENGTH_LONG).show();
                                                    }
                                                } )
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            dialog.dismiss();
                                                            //dialog_1.dismiss();

                                                            Snackbar.make(root, getString( R.string.email_with_a_activation_sent_email ), Snackbar.LENGTH_LONG).show();

                                                        } else {
                                                            System.out.println( task.getException().getMessage() );
                                                        }
                                                    }
                                                });

                                        //resend();

                                    }
                                } );

                                cencel.setOnClickListener( v -> dialog.dismiss() );

//                                dialog_1.dismiss();
//
//                                //Snackbar.make(root, "Активируйте Ваш аккаунт", Snackbar.LENGTH_LONG).show();
//                                //View view = findViewById(R.id.Drawer_layo);
//                                String undo = getString(R.string.UNDO);
//
//
//                                String itemdel = getString(R.string.Itemdeleted);
//                                Snackbar snackbar = Snackbar.make(root, getString( R.string.resend_activation ), Snackbar.LENGTH_LONG);
//
//
//
//                                snackbar.setAction(getString( R.string.send ), v -> resend());
//                                snackbar.show();



                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //dialog_1.dismiss();
                            Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
            });
    }
    private  void showSpinner(String message) {
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(message);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog_1 = builder.create();
        dialog_1.show();
        Window window = dialog_1.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog_1.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog_1.getWindow().setAttributes(layoutParams);
        }
//        progressDialog = new android.app.ProgressDialog(MainActivity.this);
//        progressDialog.setProgressStyle(STYLE_SPINNER);
//        progressDialog.setMessage("Идет регистрация");
//        progressDialog.setCancelable(true);
//        progressDialog.setIndeterminate(true);
//        progressDialog.show();
    }





    public static boolean isCorrectPAss(String p1, String p2 )
    {


        if (p1.equals( p2 ) && p1.length()>5 && (!p1.contains( " " ))){
            return true;
        }
        else {
            return false;
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
