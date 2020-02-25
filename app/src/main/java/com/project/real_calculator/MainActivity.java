package com.project.real_calculator;


import com.project.real_calculator.calculator.*;
import com.project.real_calculator.encryption.*;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    EditText disp;
    Button clear;
    private String str = "";
    boolean flag = true;
    public static final String DEFAULT = "N/A";
    String hash, salt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disp = (EditText) findViewById(R.id.Text_Display);
        clear = (Button) findViewById(R.id.Clear_Button);
        setPass();
    }


    public void input(View v)
    {
        switch (v.getId())
        {
            case R.id.Zero_Button:
            {
                final String temp = disp.getText().toString();

                if(flag) {disp.setText("");}
                if (!temp.equals(getString(R.string.zero_button)))
                {
                    setDisp(temp, getString(R.string.zero_button));
                }
                setToCE();
            }
            break;
            case R.id.One_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.one_button));
                setToCE();
            }
            break;
            case R.id.Two_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.two_button));
                setToCE();
            }
            break;
            case R.id.Three_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.three_button));
                setToCE();
            }
            break;
            case R.id.Four_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.four_button));
                setToCE();
            }
            break;
            case R.id.Five_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.five_button));
                setToCE();
            }
            break;
            case R.id.Six_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.six_button));
                setToCE();
            }
            break;
            case R.id.Seven_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.seven_button));
                setToCE();
            }
            break;
            case R.id.Eight_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.eight_button));
                setToCE();
            }
            break;
            case R.id.Nine_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.nine_button));
                setToCE();
            }
            break;
            case R.id.Left_Bracket:
            {
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
            case R.id.Right_Bracket:
            {
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
            case R.id.Percent_Button:
            {
                final String pass = disp.getText().toString();
                if( !(pass.equals(DEFAULT)) && Util.makeHashSha256( pass + salt ).equals(hash) )
                {
                    setDisp("",getString(R.string.Access_Granted));
                    setToAC();
                    //opens a new activity
                    Intent secret = new Intent();
                    secret.setClassName("com.project.real_calculator", "com.project.real_calculator.DrawerActivity");
                    startActivity(secret);
                    AES.setKey(pass);
                    AES.setIV(salt);
                }
                else
                {
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
            }
            break;
            case R.id.Clear_Button:
            {
                final String temp = disp.getText().toString();

                if(flag)
                {
                    setDisp("", getString(R.string.zero_button));
                }
                else
                {
                    if(temp.length() == 1)
                    {
                        setDisp("", getString(R.string.zero_button));
                    }
                    else
                    {
                        if(temp.equals(""))
                        {
                            setDisp("", getString(R.string.zero_button));
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
            case R.id.Divide_Button:
            {
                final String temp = disp.getText().toString();

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
            case R.id.Times_Button:
            {
                final String temp = disp.getText().toString();

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
            case R.id.Minus_Button:
            {
                final String temp = disp.getText().toString();

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
            case R.id.Plus_Button:
            {
                final String temp = disp.getText().toString();

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
            case R.id.Decimal_Button:
            {
                if(flag) {disp.setText(getString(R.string.zero_button));}
                setDisp(disp.getText().toString(), getString(R.string.decimal_button));
                setToCE();
            }
            break;
            case R.id.Equal_Button:
            {
                str = disp.getText().toString();
                str = str.replace(getString(R.string.divide_button), "/");
                str = str.replace(getString(R.string.times_button), "*");
                str = str.replace(getString(R.string.percent_button), "/100");
                str = str.replace(getString((R.string.PowTen)), "*10^");
                str = Calculate.compute(str);
                if (str.equals("error"))
                {
                    disp.setText(getString(R.string.ERROR));
                }
                else
                {
                    disp.setText(str);
                }

                disp.setSelection(disp.getText().length());
                setToAC();
            }
            break;
            default:
            {
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
        disp.setText(str + addDisp);
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

    public void setPass()
    {
        //look for the hash
        final SharedPreferences sharedPreferences = getSharedPreferences("KeyData", Context.MODE_PRIVATE);
        hash = sharedPreferences.getString("hash", DEFAULT);
        salt = sharedPreferences.getString("salt", DEFAULT);

        //set hashes and salt if found nothing
        if(hash.equals(DEFAULT) || salt.equals(DEFAULT))
        {
            //open user input dialog
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setTitle("Set a password");
            dialog.setContentView(R.layout.setpass_dialog);
            dialog.show();

            //setting button and EditText from dialog
            final EditText setpass = (EditText) dialog.findViewById(R.id.setpass);
            final EditText confirm = (EditText) dialog.findViewById(R.id.confirm);
            Button okButton = (Button) dialog.findViewById(R.id.okButton);

            okButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    String pas = setpass.getText().toString();
                    String con = confirm.getText().toString();

                    //see if passwords match
                    if(!pas.equals(con))
                    {
                        Toast.makeText(getApplicationContext(), "Does not match.", Toast.LENGTH_SHORT).show();
                    }
                    //check password length
                    else if(pas.length() < 6 || con.length() < 6)
                    {
                        Toast.makeText(getApplicationContext(), "Password needs to be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                    }
                    //set hash and salt
                    else
                    {
                        salt = Util.makeRandomString(128);
                        hash = Util.makeHashSha256(pas + salt);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("hash", hash);
                        editor.putString("salt", salt);

                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Password set.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                        //make tip dialog
                        final Dialog td = new Dialog(MainActivity.this);
                        td.setTitle("Password set");
                        td.setContentView(R.layout.setpass_successful_message);
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
                    }


                }
            });


        }
    }
}
