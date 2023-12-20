package org.rmj.guanzongroup.authentication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.Config.AppStatusConfig;
import org.rmj.g3appdriver.Config.AppVersionConfig;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.authentication.pojo.LoginCredentials;
import org.rmj.guanzongroup.authentication.R;
import org.rmj.guanzongroup.authentication.Callback.LoginCallback;
import org.rmj.guanzongroup.authentication.ViewModel.VMLogin;

import java.util.Objects;

public class Activity_Login extends AppCompatActivity implements LoginCallback {
    private TextInputEditText tie_username;
    private TextInputEditText tie_password;
    private TextInputEditText tie_mobileno;
    private MaterialTextView lblVersion;
    private MaterialTextView mtv_createaccount;
    private MaterialTextView mtv_forgotpassw;
    private MaterialButton btn_log;
    private VMLogin mViewModel;
    private LoadDialog podialog;
    private AppVersionConfig poAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mViewModel = new ViewModelProvider(this).get(VMLogin.class);
        podialog = new LoadDialog(this);
        poAppVersion = AppVersionConfig.getInstance(this);

        AppStatusConfig.getInstance(this).setTestStatus(true);

        tie_username = findViewById(R.id.username);
        tie_password = findViewById(R.id.password);
        tie_mobileno = findViewById(R.id.mobileno);
        lblVersion = findViewById(R.id.lbl_versionInfo);
        mtv_createaccount = findViewById(R.id.mtv_createaccount);
        mtv_forgotpassw = findViewById(R.id.mtv_forgotpassw);
        btn_log = findViewById(R.id.btn_log);

        tie_mobileno.setText(mViewModel.getMobileNo());
        tie_mobileno.setVisibility(mViewModel.hasMobileNo());

        lblVersion.setText(poAppVersion.getVersionName()); //display app version
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(tie_username.getText()).toString();
                String password = Objects.requireNonNull(tie_password.getText()).toString();
                String mobileno = Objects.requireNonNull(tie_mobileno.getText()).toString();

                //start login session
                mViewModel.Login(new LoginCredentials(email,password, mobileno), Activity_Login.this);
            }
        });
        mtv_createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_CreateAccount.class);
                startActivity(intent);
            }
        });
        mtv_forgotpassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_ForgotPassword.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void OnAuthenticationLoad(String Title, String Message) {
        podialog.initDialog(Title, Message, false);
        podialog.show();
    }
    @Override
    public void OnSuccessLoginResult() {
        podialog.dismiss();

        Intent loIntent = new Intent();
        this.setResult(Activity.RESULT_OK, loIntent);
        this.finish();
    }
    @Override
    public void OnFailedLoginResult(String message) {
        podialog.dismiss();

        MessageBox loMessage = new MessageBox(this);
        loMessage.initDialog();
        loMessage.setTitle("Telemarketing App");
        loMessage.setMessage(message);
        loMessage.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
        loMessage.show();
    }
}