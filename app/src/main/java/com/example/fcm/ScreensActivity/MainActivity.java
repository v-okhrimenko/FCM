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
package com.example.fcm.ScreensActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;

    private FirebaseAuth auth;
    private ConstraintLayout root;
    private AlertDialog alertDialog;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        Helper.hideSystemUI( decorView );

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        root = findViewById(R.id.rootelement);
        FirebaseDatabase.getInstance().setPersistenceEnabled( true );

        Button btn_SignIn = findViewById( R.id.btn_enter );
        TextView btn_forgotPassword = findViewById( R.id.tv_problem_enter );

        userEmail = findViewById(R.id.signInEmail);
        userPassword = findViewById(R.id.signInPass);
        TextView btn_signUp = findViewById( R.id.textView45 ); // button registration

        checkIfUserAuthorised();

        btn_forgotPassword.setOnClickListener( v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
            LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
            final View regiserwindow = inflater.inflate( R.layout.forgot_pass_inflater, null );
            builder.setView( regiserwindow );

            final Button btn_newPasswordOk = regiserwindow.findViewById( R.id.btn_registr );
            final Button btn_newPasswordCancel = regiserwindow.findViewById( R.id.cencel_registartion );
            final EditText et_UserEmail = regiserwindow.findViewById( R.id.et_email_registartion );
            final TextView tv_YouEmailTop = regiserwindow.findViewById( R.id.textView31 );
            final TextView tv_noSuchAccount = regiserwindow.findViewById( R.id.tv_no_such_account );
            final TextView tv_emailIsntCorrect = regiserwindow.findViewById( R.id.tv_EmailIsntCorrect );

            final AlertDialog sendNewPasswordDialog = builder.create();
            sendNewPasswordDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            sendNewPasswordDialog.setCancelable( false );
            sendNewPasswordDialog.show();

            et_UserEmail.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tv_emailIsntCorrect.setVisibility( View.INVISIBLE );
                    et_UserEmail.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                    tv_YouEmailTop.setTextColor( getResources().getColor( R.color.blue_lite ) );
                    tv_noSuchAccount.setVisibility( View.INVISIBLE );
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            } );
            btn_newPasswordOk.setOnClickListener( v12 -> {

                showSpinner( getString( R.string.In_the_process ));

                hideKeyboardFrom( getApplicationContext(),regiserwindow );

                if(et_UserEmail.getText().toString().isEmpty()){
                    alertDialog.dismiss();
                    et_UserEmail.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                    tv_YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );

                }

                else {
                    if (Helper.isValidEmail( et_UserEmail.getText().toString().trim() )){

                        FirebaseAuth.getInstance().sendPasswordResetEmail( et_UserEmail.getText().toString().trim() )
                                .addOnCompleteListener( task -> {
                                    if (task.isSuccessful()){

                                        alertDialog.dismiss();
                                        sendNewPasswordDialog.dismiss();
                                        Snackbar.make(root, getString( R.string.Password_reset_link_sent ), Snackbar.LENGTH_LONG).show();

                                    } else {
                                        alertDialog.dismiss();
                                        et_UserEmail.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                        tv_YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                        tv_noSuchAccount.setVisibility( View.VISIBLE );
                                    }
                                } );
                    } else {
                        alertDialog.dismiss();
                        et_UserEmail.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                        tv_YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                        tv_emailIsntCorrect.setVisibility( View.VISIBLE );
                    }
                }
            } );
            btn_newPasswordCancel.setOnClickListener( v1 -> sendNewPasswordDialog.dismiss() );
        } );
        btn_signUp.setOnClickListener( v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
            LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
            final View regiserWindow = inflater.inflate( R.layout.regitration_layout_inflater, null );
            builder.setView( regiserWindow );

            final Button btn_registrationOk = regiserWindow.findViewById( R.id.btn_registr );
            final Button btn_registrationCencel = regiserWindow.findViewById( R.id.cencel_registartion );

            final EditText email = regiserWindow.findViewById( R.id.et_email_registartion );
            final TextView YouEmailTop = regiserWindow.findViewById( R.id.textView31 );

            final EditText pass1 = regiserWindow.findViewById( R.id.et_pass_registartion );
            final TextView YouPassTop = regiserWindow.findViewById( R.id.textView25 );
            final TextView passNoMatch = regiserWindow.findViewById( R.id.tv_miniminSixChar2 );
            final TextView passNoConteinSpase = regiserWindow.findViewById( R.id.tv_miniminSixChar3 );
            final EditText pass2 = regiserWindow.findViewById( R.id.et_repeat_pass_registartion );
            final TextView repeatYouPassTop = regiserWindow.findViewById( R.id.tv_new_pass_label );
            final TextView passNoMatch2 = regiserWindow.findViewById( R.id.tv_error_new_password2 );
            final TextView emailIsntCorrect = regiserWindow.findViewById( R.id.tv_EmailIsntCorrect );
            final TextView minSixChar = regiserWindow.findViewById( R.id.tv_miniminSixChar );
            final ImageView check_ok = regiserWindow.findViewById( R.id.iv_password_check_ok );
            final ImageView check_ok2 = regiserWindow.findViewById( R.id.iv_password2_check_ok );

            check_ok.setVisibility( View.INVISIBLE );
            check_ok2.setVisibility( View.INVISIBLE );

            final Integer[] x = {0};

            final AlertDialog registrationDialog = builder.create();
            registrationDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            registrationDialog.setCancelable( false );
            registrationDialog.show();

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
                    YouPassTop.setTextColor( getResources().getColor( R.color.blue_lite ) );

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

            btn_registrationCencel.setOnClickListener( v1 -> registrationDialog.dismiss() );
            btn_registrationOk.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isCorrectPAss( pass1.getText().toString().trim(), pass2.getText().toString().trim() )
                            && Helper.isValidEmail( email.getText().toString().trim() )){
                        hideKeyboardFrom( getApplicationContext(), regiserWindow );
                        showSpinner("Регистрация ...");
                        startUserRegistration();
                    } else {

                        //EMAIL CHECK
                        if (email.getText().toString().trim().isEmpty()){

                            email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                            YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                        } else {
                            if(Helper.isValidEmail( email.getText().toString().trim() )) {
                                emailIsntCorrect.setVisibility( View.INVISIBLE );
                                email.setBackground(getDrawable( R.drawable.text_edit_light_blue  ) );
                                YouEmailTop.setTextColor( getResources().getColor( R.color.blue_lite ) );

                            } else {
                                email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                emailIsntCorrect.setVisibility( View.VISIBLE );
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
                            pass2.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                            repeatYouPassTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                            passNoMatch2.setVisibility( View.VISIBLE );
                        }
                    }
                }

                private void startUserRegistration() {

                    auth.createUserWithEmailAndPassword(email.getText().toString(), pass1.getText().toString())

                            .addOnCompleteListener( task -> {
                                if (task.isSuccessful()) {
                                    userEmail.setText(email.getText().toString());
                                    userPassword.setText(pass1.getText().toString());

                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    FirebaseUser user_mail = auth.getCurrentUser();
                                    user_mail.sendEmailVerification()
                                            .addOnCompleteListener( task1 -> {

                                                if (task1.isSuccessful()) {
                                                    registrationDialog.dismiss();
                                                    alertDialog.dismiss();
                                                    Snackbar.make(root, getString( R.string.user_add_mail_send ), Snackbar.LENGTH_LONG).show();
                                                }
                                            } );

                                } else  {
                                    Snackbar.make(root, getString( R.string.User_with_such_an_email_exists ), Snackbar.LENGTH_SHORT).show();
                                    email.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                                    YouEmailTop.setTextColor( getResources().getColor( R.color.alert_bottom ) );
                                    alertDialog.dismiss();
                                }
                            } );
                }
            } );
        } );
        btn_SignIn.setOnClickListener( v -> {
            hideKeyboard( MainActivity.this );
            signIn();

        } );
    }

    private void checkIfUserAuthorised() {

        if (auth.getCurrentUser() != null) {
            boolean emailVerified = firebaseUser.isEmailVerified();

            if (emailVerified) {
                FirebaseDatabase.getInstance().setPersistenceEnabled( true );
                startActivity(new Intent(MainActivity.this, CalendarMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        }
    }

    private void signIn() {

        if(userEmail.getText().toString().equals("")|| userPassword.getText().toString().equals("")){
            Snackbar.make(root, getString( R.string.Enter_your_email_and_password ), Snackbar.LENGTH_SHORT).show();
           hideKeyboard( MainActivity.this );
        } else
            auth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                    .addOnSuccessListener( authResult -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        boolean emailVerified = user.isEmailVerified();
                        if (emailVerified) {
                            startActivity(new Intent(MainActivity.this, CalendarMainActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
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

                            send.setOnClickListener( v -> {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user_mail = auth.getCurrentUser();
                                user_mail.sendEmailVerification()

                                        .addOnFailureListener( e -> {
                                            //System.out.println( e.getMessage().trim() );
                                            dialog.dismiss();
                                            Snackbar.make(root, getString( R.string.somsing_wrong ), Snackbar.LENGTH_LONG).show();
                                        } )
                                        .addOnCompleteListener( task -> {

                                            if (task.isSuccessful()) {

                                                dialog.dismiss();
                                                Snackbar.make(root, getString( R.string.email_with_a_activation_sent_email ), Snackbar.LENGTH_LONG).show();

                                            } else {
                                                System.out.println( task.getException().getMessage() );
                                            }
                                        } );
                            } );

                            cencel.setOnClickListener( v -> dialog.dismiss() );
                        }

                    } ).addOnFailureListener( e -> Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show() );
    }
    private void showSpinner(String message) {
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

        alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom( alertDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }
    public static boolean isCorrectPAss(String passwordOne, String passwordTwo ) {
        return passwordOne.equals( passwordTwo ) && passwordOne.length() > 5 && (!passwordOne.contains( " " ));
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
