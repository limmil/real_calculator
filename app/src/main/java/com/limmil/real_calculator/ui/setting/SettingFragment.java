package com.limmil.real_calculator.ui.setting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.DataBaseHelper;
import com.limmil.real_calculator.database.models.UserModel;
import com.limmil.real_calculator.encryption.Util;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SettingFragment extends Fragment {

    public SettingFragment(){ }

    public static SettingFragment newInstance(){
        return new SettingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        SwitchCompat loadingSpinner = root.findViewById(R.id.settingSwitch);
        final TextView changePassword = root.findViewById(R.id.settingChangePassword);

        // init spinner
        final SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        final boolean spinnerSetting = sharedPreferences.getBoolean("spinner", true);
        loadingSpinner.setChecked(spinnerSetting);

        loadingSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set spinner setting
                sharedPreferences.edit().putBoolean("spinner", !spinnerSetting).apply();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show change password dialog
                changePassword();
            }
        });

        return root;
    }

    public void changePassword(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        //open user input dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Reset Password");
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout((6 * width)/7, WRAP_CONTENT);
        dialog.show();


        //setting button and EditText from dialog
        final EditText currentPassword = dialog.findViewById(R.id.currentPassword);
        final EditText resetPassword = dialog.findViewById(R.id.resetPassword);
        final EditText reconfirm = dialog.findViewById(R.id.reconfirm);
        final Button okButton = dialog.findViewById(R.id.reOkButton);
        RadioButton strongRButton = dialog.findViewById(R.id.reStrong);
        final ImageView helpButton = dialog.findViewById(R.id.reHelp);
        final RadioGroup radioGroup = dialog.findViewById(R.id.reRadioGroup);
        strongRButton.setChecked(true);

        reconfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    okButton.performClick();
                }
                return false;
            }
        });

        final Toast helpToast = Toast.makeText(getContext(),
                "Stronger hash strength will take longer to verify password.",
                Toast.LENGTH_LONG);

        // avoid toast spamming
        final Toast dnmToast = Toast.makeText(getContext(),
                "Does not match.",
                Toast.LENGTH_SHORT);
        final Toast lenCheckToast = Toast.makeText(getContext(),
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

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentPasswordStr = currentPassword.getText().toString();
                final String resetPasswordStr = resetPassword.getText().toString();
                final String reconfirmStr = reconfirm.getText().toString();


                //see if passwords match
                if(!resetPasswordStr.equals(reconfirmStr))
                {
                    lenCheckToast.cancel();
                    helpToast.cancel();
                    dnmToast.show();
                }
                //check password length
                else if(resetPasswordStr.length() < 6)
                {
                    dnmToast.cancel();
                    helpToast.cancel();
                    lenCheckToast.show();
                }else{
                    // check current password hash
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                            "Loading", "Checking current password...", true);
                    new Thread(){
                        public void run(){
                            // get hash and iv from database
                            DataBaseHelper db = new DataBaseHelper(requireActivity().getApplicationContext());
                            List<UserModel> users = db.getUsers();
                            String iv = users.get(0).getIv();
                            String hash = users.get(0).getPassword();
                            byte[] mkey = users.get(0).getBmkey();

                            // check password
                            boolean result = Util.checkPassword(currentPasswordStr, hash);
                            if (result){ // reset password

                                int tempStrength;
                                if (radioGroup.getCheckedRadioButtonId() == R.id.reWeak) {
                                    tempStrength = 10;
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.reGood){
                                    tempStrength = 11;
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.reStrong){
                                    tempStrength = 12;
                                } else{
                                    tempStrength = 12;
                                }
                                final int hashStrength = tempStrength;

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.setMessage("Setting up keys");
                                    }
                                });
                                // get master key
                                String masterKey = Util.getMasterKey(currentPasswordStr,iv,mkey);
                                // generate new iv and hash
                                iv = Util.makeRandomString(64);
                                hash = Util.makePasswordHash(resetPasswordStr, hashStrength);
                                // encrypt masterKey with new password
                                mkey = Util.encryptToByte(resetPasswordStr, iv, masterKey);
                                Util.setMasterKey(resetPasswordStr,iv,mkey);

                                // update user in db
                                UserModel updatedUser = new UserModel(iv, mkey, hash);
                                boolean success = db.updateUser(updatedUser);

                                if (success){
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "Updated password!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }.start();
                }
            }
        });

    }


}
