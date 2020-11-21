package com.project.real_calculator;


import com.project.real_calculator.calculator.*;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.UserModel;
import com.project.real_calculator.encryption.*;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    EditText disp;
    Button clear;
    private String str = "";
    boolean flag = true;
    public static final String DEFAULT = "N/A";
    String hash, salt, iv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disp = (EditText) findViewById(R.id.Text_Display);
        clear = (Button) findViewById(R.id.Clear_Button);
        Log.d("startup1:","start");
        onStartUp();
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
                // test
                String dirPath = getExternalFilesDir("mydir/s").getAbsolutePath();
                //String dirPath = getExternalFilesDir().getAbsolutePath() + File.separator + "testfolder";
                File newFolder = new File(dirPath);
                if (!newFolder.exists()){
                    newFolder.mkdirs();
                }

                File newFile = new File(dirPath, "myText2.txt");

                if(!newFile.exists()){
                    try {
                        newFile.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try  {
                    FileOutputStream fOut = new FileOutputStream(newFile);
                    OutputStreamWriter outputWriter=new OutputStreamWriter(fOut);
                    outputWriter.write("sadfds");
                    outputWriter.close();

                    //display file saved message
                    //Toast.makeText(getBaseContext(), "File saved successfully!",
                            //Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }


                DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this);
                List<UserModel> e = dbHelper.getUsers();
                String str = "id: "+e.get(0).getId()+" mkey: "+ Arrays.toString(e.get(0).getBmkey()) +" pw: "+e.get(0).getPassword();
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

                // end test

                final String temp = disp.getText().toString();

                if(flag) {disp.setText("");}
                if (!temp.equals(getString(R.string.zero_button)))
                {
                    setDisp(temp, getString(R.string.zero_button));
                }
                setToCE();
            }
            break;
            case One_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.one_button));
                setToCE();
            }
            break;
            case Two_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.two_button));
                setToCE();
            }
            break;
            case Three_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.three_button));
                setToCE();
            }
            break;
            case Four_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.four_button));
                setToCE();
            }
            break;
            case Five_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.five_button));
                setToCE();
            }
            break;
            case Six_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.six_button));
                setToCE();
            }
            break;
            case Seven_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.seven_button));
                setToCE();
            }
            break;
            case Eight_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.eight_button));
                setToCE();
            }
            break;
            case Nine_Button:
            {
                if(flag) {disp.setText("");}
                checkZero(getString(R.string.nine_button));
                setToCE();
            }
            break;
            case Left_Bracket:
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
            case Right_Bracket:
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
            case Percent_Button:
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
            case Clear_Button:
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
            case Divide_Button:
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
            case Times_Button:
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
            case Minus_Button:
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
            case Plus_Button:
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
            case Decimal_Button:
            {
                if(flag) {disp.setText(getString(R.string.zero_button));}
                setDisp(disp.getText().toString(), getString(R.string.decimal_button));
                setToCE();
            }
            break;
            case Equal_Button:
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

    public void onStartUp()
    {
        final DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this);
        //UserModel userModel = new UserModel(0,"iv","mkey","pw");
        //boolean g = dbHelper.addUser(userModel);

        //check if user table first row exist
        if (!dbHelper.userExist()) //if user doesn't exist
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
                    //set passwords
                    else
                    {
                        // generate iv and hash
                        iv = Util.makeRandomString(64);
                        hash = Util.makePasswordHash(pas);
                        // generate mkey and encrypt it
                        String mkey = Util.makeRandomString(128);
                        byte[] bmkey = Util.encryptToByte(pas, iv, mkey);

                        // add user to db
                        UserModel newUser = new UserModel(iv, bmkey, hash);
                        boolean success = dbHelper.addUser(newUser);

                        //List<UserModel> asdf = dbHelper.getUsers();
                        //byte[] b = asdf.get(0).getBmkey();
                        if (success) {
                            Toast.makeText(getApplicationContext(), "Password set.", Toast.LENGTH_SHORT).show();
                            /*
                            Log.d("mkey",mkey);
                            Log.d("bytestring", new String(b, StandardCharsets.UTF_8));
                            Log.d("byte[]", Arrays.toString(b));
                            AES.decrypt(b);
                            Log.d("decypted", new String(AES.getDecryptedBytes(), StandardCharsets.ISO_8859_1));*/
                        }
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
