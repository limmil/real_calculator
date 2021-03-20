package com.limmil.real_calculator;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.limmil.real_calculator.calculator.*;
import com.limmil.real_calculator.database.DataBaseHelper;
import com.limmil.real_calculator.database.models.UserModel;
import com.limmil.real_calculator.encryption.*;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    EditText disp;
    Button clear, login;
    ProgressBar progressBar;
    private String str = "";
    boolean flag = true;
    boolean threadRunning = false;
    String hash, iv;
    byte[] mkey;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disp = (EditText) findViewById(R.id.Text_Display);
        disp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                disp.setFocusableInTouchMode(true);
                return false;
            }
        });

        clear = (Button) findViewById(R.id.Clear_Button);
        progressBar = findViewById(R.id.Verify_Progress);
        login = findViewById(R.id.Percent_Button);
        setLoginButton(login);
        appIntro();
    }


    public void input(View v)
    {
        //switch cases wants final variable
        final int Zero_Button = R.id.Zero_Button;
        final int One_Button = R.id.One_Button;
        final int Two_Button = R.id.Two_Button;
        final int Three_Button = R.id.Three_Button;
        final int Four_Button = R.id.Four_Button;
        final int Five_Button = R.id.Five_Button;
        final int Six_Button = R.id.Six_Button;
        final int Seven_Button = R.id.Seven_Button;
        final int Eight_Button = R.id.Eight_Button;
        final int Nine_Button = R.id.Nine_Button;
        final int Left_Bracket = R.id.Left_Bracket;
        final int Right_Bracket = R.id.Right_Bracket;
        final int Percent_Button = R.id.Percent_Button;
        final int Clear_Button = R.id.Clear_Button;
        final int Divide_Button = R.id.Divide_Button;
        final int Times_Button = R.id.Times_Button;
        final int Minus_Button = R.id.Minus_Button;
        final int Plus_Button = R.id.Plus_Button;
        final int Decimal_Button = R.id.Decimal_Button;
        final int Equal_Button = R.id.Equal_Button;

        switch (v.getId())
        {
            case Zero_Button:
            {
                disp.setFocusable(false);

                if(flag) {disp.setText("");}
                setDisp(disp.getText().toString(), getString(R.string.zero_button));
                setToCE();
            }
            break;
            case One_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.one_button));
                setToCE();
            }
            break;
            case Two_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.two_button));
                setToCE();
            }
            break;
            case Three_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.three_button));
                setToCE();
            }
            break;
            case Four_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.four_button));
                setToCE();
            }
            break;
            case Five_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.five_button));
                setToCE();
            }
            break;
            case Six_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.six_button));
                setToCE();
            }
            break;
            case Seven_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.seven_button));
                setToCE();
            }
            break;
            case Eight_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.eight_button));
                setToCE();
            }
            break;
            case Nine_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.nine_button));
                setToCE();
            }
            break;
            case Left_Bracket:
            {
                disp.setFocusable(false);
                if(!disp.getText().toString().equals("error"))
                {
                    checkZero(getString(R.string.left_bracket));
                    setToCE();
                }
                else
                {
                    setDisp("",getString(R.string.zero_button));
                }
            }
            break;
            case Right_Bracket:
            {
                disp.setFocusable(false);
                final String temp = disp.getText().toString();

                if(flag) {disp.setText(getString(R.string.zero_button));}
                if(!(temp.equals(getString(R.string.zero_button))
                        || temp.equals(""))
                        || temp.equals(getString(R.string.ERROR)))
                {
                    setDisp(temp, getString(R.string.right_bracket));
                }
            }
            break;
            //button to access~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            case Percent_Button:
            {
                final String pass = disp.getText().toString();
                if (!pass.equals("error"))
                {

                    if (!(pass.equals("")))
                    {
                        setDisp(pass, getString(R.string.percent_button));
                    }
                    setToCE();
                }
                else
                {
                    setDisp("", getString(R.string.zero_button));
                }

            }
            break;
            case Clear_Button:
            {
                final String temp = disp.getText().toString();

                if(flag)
                {
                    setDisp("", "");
                }
                else
                {
                    if(temp.length() == 1)
                    {
                        setDisp("", "");
                    }
                    else
                    {
                        if(temp.equals(""))
                        {
                            setDisp("", "");
                        }
                        else
                        {
                            str = temp.substring(0, temp.length() - 1);
                            setDisp("", str);
                        }
                    }
                }
                setToCE();
            }
            break;
            case Divide_Button:
            {
                final String temp = disp.getText().toString();
                disp.setFocusable(false);

                if(!temp.equals("error"))
                {
                    if (!(temp.equals("")))
                    {
                        setDisp(temp, getString(R.string.divide_button));
                    }
                    setToCE();
                }
                else
                {
                    setDisp("",getString(R.string.zero_button));
                }
            }
            break;
            case Times_Button:
            {
                final String temp = disp.getText().toString();
                disp.setFocusable(false);

                if(!temp.equals("error"))
                {
                    if(!(temp.equals("")))
                    {
                        setDisp(temp, getString(R.string.times_button));
                    }
                    setToCE();
                }
                else
                {
                    setDisp("",getString(R.string.zero_button));
                }
            }
            break;
            case Minus_Button:
            {
                final String temp = disp.getText().toString();
                disp.setFocusable(false);

                if(!temp.equals("error"))
                {
                    if (temp.equals(getString(R.string.zero_button)))
                    {
                        setDisp("", getString(R.string.minus_button));
                    }
                    else
                    {
                        setDisp(temp, getString(R.string.minus_button));
                    }
                    setToCE();
                }
                else
                {
                    setDisp("",getString(R.string.zero_button));
                }
            }
            break;
            case Plus_Button:
            {
                final String temp = disp.getText().toString();
                disp.setFocusable(false);

                if(!temp.equals("error"))
                {
                    setDisp(temp, getString(R.string.plus_button));
                    setToCE();
                }
                else
                {
                    setDisp("",getString(R.string.zero_button));
                }
            }
            break;
            case Decimal_Button:
            {
                disp.setFocusable(false);
                if(flag) {disp.setText("");}
                setDisp(disp.getText().toString(), getString(R.string.decimal_button));
                setToCE();
            }
            break;
            case Equal_Button:
            {
                disp.setFocusable(false);
                str = disp.getText().toString();

                if (!str.isEmpty()) {
                    str = str.replace(getString(R.string.divide_button), "/");
                    str = str.replace(getString(R.string.times_button), "*");
                    str = str.replace(getString(R.string.percent_button), "/100");
                    str = str.replace(getString((R.string.PowTen)), "*10^");
                    str = Calculate.compute(str);
                    if (str.equals("error")) {
                        disp.setText(getString(R.string.ERROR));
                    } else {
                        disp.setText(str);
                    }
                }

                disp.setSelection(disp.getText().length());
                setToAC();
            }
            break;
            default:
            {
                disp.setFocusable(false);
                str = "";
                disp.setText(getString(R.string.zero_button));
                disp.setSelection(disp.getText().length());
            }
            break;

        }
    }

    public void setDisp(String getDisp, String addDisp)
    {
        str = getDisp;
        String out = str + addDisp;
        disp.setText(out);
        disp.setSelection(disp.getText().length());
    }

    public void checkZero(String input)
    {
        if (disp.getText().toString().equals(getString(R.string.zero_button)))
        {
            setDisp("", input);
        }
        else
        {
            setDisp(disp.getText().toString(), input);
        }
    }

    public void setToAC()
    {
        if(!flag)
        {
            clear.setText(getString(R.string.all_clear_button));
        }
        flag = true;
    }

    public void setToCE()
    {
        if(flag)
        {
            clear.setText(getString(R.string.erase_button));
        }
        flag = false;
    }

    public void onLogin()
    {
        dbHelper = new DataBaseHelper(getApplicationContext());

        //check if user table first row exist
        if (!dbHelper.userExist()) //if user doesn't exist
        {
            //open user input dialog
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setTitle("Set a password");
            dialog.setContentView(R.layout.dialog_setpassword);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            //setting button and EditText from dialog
            final EditText setpass = (EditText) dialog.findViewById(R.id.setpass);
            final EditText confirm = (EditText) dialog.findViewById(R.id.confirm);
            final TextView pwStrength = (TextView) dialog.findViewById(R.id.password_strength);
            final Button okButton = (Button) dialog.findViewById(R.id.okButton);
            RadioButton strongRButton = dialog.findViewById(R.id.strong);
            final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
            ImageView helpButton = dialog.findViewById(R.id.help);
            strongRButton.setChecked(true);

            setpass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int iPasswordScore = Util.calculatePasswordStrength(setpass.getText().toString());
                    String strength = "Weak Password";
                    if(iPasswordScore < 5){
                        strength = "Weak Password";
                        pwStrength.setTextColor(Color.parseColor("#e72f2f"));
                    }else if(iPasswordScore > 4 && iPasswordScore < 8){
                        strength = "Good Password";
                        pwStrength.setTextColor(Color.parseColor("#feb937"));
                    }else{
                        strength = "Strong Password";
                        pwStrength.setTextColor(Color.parseColor("#95b645"));
                    }
                    pwStrength.setText(strength);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            confirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        //do what you want on the press of 'done'
                        okButton.performClick();
                    }
                    return false;
                }
            });

            final Toast helpToast = Toast.makeText(getApplicationContext(),
                "Stronger hash strength will take longer to verify password.",
                Toast.LENGTH_LONG);

            // avoid toast spamming
            final Toast dnmToast = Toast.makeText(getApplicationContext(),
                    "Does not match.",
                    Toast.LENGTH_SHORT);
            final Toast lenCheckToast = Toast.makeText(getApplicationContext(),
                    "Password needs to be at least 6 characters long.",
                    Toast.LENGTH_SHORT);

            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dnmToast.cancel();
                    lenCheckToast.cancel();
                    helpToast.show();
                }
            });

            okButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    final String pas = setpass.getText().toString();
                    String con = confirm.getText().toString();

                    //see if passwords match
                    if(!pas.equals(con))
                    {
                        lenCheckToast.cancel();
                        helpToast.cancel();
                        dnmToast.show();
                    }
                    //check password length
                    else if(pas.length() < 6)
                    {
                        dnmToast.cancel();
                        helpToast.cancel();
                        lenCheckToast.show();
                    }
                    //set passwords
                    else
                    {
                        int tempStrength;
                        if (radioGroup.getCheckedRadioButtonId() == R.id.weak) {
                            tempStrength = 10;
                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.good){
                            tempStrength = 11;
                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.strong){
                            tempStrength = 12;
                        } else{
                            tempStrength = 12;
                        }
                        final int hashStrength = tempStrength;

                        dialog.cancel();
                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
                                "Loading", "Setting up keys", true);
                        new Thread(){
                            public void run(){
                                // generate iv and hash
                                iv = Util.makeRandomString(64);
                                hash = Util.makePasswordHash(pas, hashStrength);
                                // generate mkey and encrypt it
                                String plainKey = Util.makeRandomStringFromRandomBytes(4096);
                                mkey = Util.encryptToByte(pas, iv, plainKey);

                                // add user to db
                                UserModel newUser = new UserModel(iv, mkey, hash);
                                boolean success = dbHelper.addUser(newUser);
                                if (success) {
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Password set.", Toast.LENGTH_SHORT).show();
                                            //make tip dialog
                                            final Dialog td = new Dialog(MainActivity.this);
                                            td.setTitle("Password set");
                                            td.setContentView(R.layout.dialog_setpass_successful_message);
                                            td.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            td.show();

                                            //set ok button2
                                            Button okButton2 = (Button) td.findViewById(R.id.okButton2);
                                            okButton2.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    td.cancel();
                                                }
                                            });

                                            final Dialog dialogTwo = new Dialog(MainActivity.this);
                                            dialogTwo.setContentView(R.layout.intro_two);
                                            dialogTwo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            dialogTwo.show();
                                            Button introTwoButton = dialogTwo.findViewById(R.id.introTwoButton);
                                            introTwoButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogTwo.dismiss();
                                                }
                                            });
                                            ImageView showCase = dialogTwo.findViewById(R.id.imageView4);
                                            Glide.with(getApplicationContext())
                                                    .load(R.drawable.intro_two)
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .skipMemoryCache(true)
                                                    .into(showCase);
                                        }
                                    });
                                }
                            }
                        }.start();





                    }


                }
            });
        }
        else{
            // get hash and iv from database
            List<UserModel> users = dbHelper.getUsers();
            iv = users.get(0).getIv();
            hash = users.get(0).getPassword();
            mkey = users.get(0).getBmkey();

        }
    }

    public void setLoginButton(Button button){
        button.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                onLogin();

                final String pass = disp.getText().toString();
                final SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                final boolean spinnerSetting = sharedPreferences.getBoolean("spinner", true);
                if(dbHelper.userExist()) {
                    new Thread() {
                        public void run() {
                            if (!threadRunning) {
                                // run thread one at a time
                                threadRunning = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (spinnerSetting) {
                                            progressBar.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                // heavy lifting
                                boolean result = Util.checkPassword(pass, hash);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                                // password matches
                                if (result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setDisp("", getString(R.string.Access_Granted));
                                            setToAC();
                                        }
                                    });
                                    //opens a new activity
                                    Intent secret = new Intent();
                                    secret.setClassName("com.limmil.real_calculator",
                                            "com.limmil.real_calculator.DrawerActivity");
                                    startActivity(secret);
                                    Util.setMasterKey(pass, iv, mkey);
                                }
                                // switch off flag
                                threadRunning = false;
                            }
                        }
                    }.start();
                }

                return true;
            }
        });
    }

    public void appIntro(){
        final SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final boolean appIntro = sharedPreferences.getBoolean("intro", true);
        if (appIntro){
            // generates app data folder
            dbHelper = new DataBaseHelper(getApplicationContext());
            dbHelper.userExist();

            final Dialog td = new Dialog(MainActivity.this);
            td.setTitle("Password setup");
            // reusing layout
            td.setContentView(R.layout.dialog_setpass_successful_message);
            td.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            td.show();

            //set ok button2
            Button okButton2 = (Button) td.findViewById(R.id.okButton2);
            okButton2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    td.cancel();
                }
            });
            TextView msg = td.findViewById(R.id.successMessage);
            msg.setText(getString(R.string.setup_msg));

            final Dialog dialogOne = new Dialog(MainActivity.this);
            dialogOne.setContentView(R.layout.intro_one);
            dialogOne.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogOne.show();
            Button introOneButton = dialogOne.findViewById(R.id.introOneButton);
            introOneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogOne.dismiss();
                }
            });
            ImageView showCase = dialogOne.findViewById(R.id.imageView3);
            Glide.with(getApplicationContext())
                    .load(R.drawable.intro_one)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(showCase);

            sharedPreferences.edit().putBoolean("intro", false).apply();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (disp.getText().toString().equals(getString(R.string.Access_Granted))){
            disp.setText("");
            AES.clear();
        }
    }
}
