/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.authLibrary
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.authentication.ViewModel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.rmj.g3appdriver.lib.authentication.GAuthentication;
import org.rmj.g3appdriver.lib.authentication.factory.Auth;
import org.rmj.g3appdriver.lib.authentication.factory.iAuthenticate;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMForgotPassword extends AndroidViewModel {
    public static final String TAG = VMForgotPassword.class.getSimpleName();
    private Application instance;
    private final ConnectionUtil poConn;
    private GAuthentication poAccount;
    private iAuthenticate poSys;
    private String message;
    public VMForgotPassword(@NonNull Application application) {
        super(application);
        this.instance = application;

        this.poConn = new ConnectionUtil(instance);
        this.poAccount = new GAuthentication(instance);
        this.poSys = poAccount.initAppAuthentication().getInstance(Auth.FORGOT_PASSWORD);
    }
    public boolean isEmailValid(String Email){
        return TextUtils.isEmpty(Email) && Patterns.EMAIL_ADDRESS.matcher(Email).matches();
    }

    public void RequestPassword(String Email, RequestPasswordCallback mListener){
        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.Execute(Email, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                mListener.OnSendRequest("Send Request", "Sending Request . . .");
            }

            @Override
            public Object DoInBackground(Object args) {
                String sEmail = (String) args;

                if (isEmailValid(sEmail) == false){
                    message = "Email is invalid";
                    return false;
                }

                if (!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                if (poSys.DoAction(sEmail) == 0 || poSys.DoAction(sEmail) == 2){
                    message = poSys.getMessage();
                    return false;
                }else {
                    message = poSys.getMessage();
                    return true;
                }
            }
            @Override
            public void OnPostExecute(Object object) {
                Boolean result = (Boolean) object;

                if (result.equals(false)){
                    mListener.OnFailedRequest(message);
                }else {
                    mListener.OnSuccessRequest();
                }
            }
        });
    }

    public interface RequestPasswordCallback{
        void OnSendRequest(String title, String message);
        void OnSuccessRequest();
        void OnFailedRequest(String message);
    }
}