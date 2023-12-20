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

import android.annotation.SuppressLint;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.Config.AppConfig;
import org.rmj.g3appdriver.Config.DeviceConfig;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.lib.authentication.GAuthentication;
import org.rmj.g3appdriver.lib.authentication.factory.Auth;
import org.rmj.g3appdriver.lib.authentication.factory.iAuthenticate;
import org.rmj.g3appdriver.lib.authentication.pojo.LoginCredentials;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.authentication.Callback.LoginCallback;

public class VMLogin extends AndroidViewModel {
    public static final String TAG =  VMLogin.class.getSimpleName();
    private final AppConfig poConfig;
    private final ConnectionUtil poConn;
    private final GAuthentication poAccount;
    private final iAuthenticate poSys;
    private DeviceConfig poDevConfig;
    private final Telephony poTlphony;
    private String message;

    public VMLogin(@NonNull Application application) {
        super(application);

        this.poConfig = AppConfig.getInstance(application);
        this.poConn = new ConnectionUtil(application);

        this.poDevConfig = DeviceConfig.getInstance(application);
        this.poTlphony = new Telephony(application);

        this.poAccount = new GAuthentication(application);
        this.poSys = poAccount.initAppAuthentication().getInstance(Auth.AUTHENTICATE);
    }
    @SuppressLint("NewApi")
    public String getMobileNo(){
        if(poDevConfig.getMobileNO().isEmpty()) {
            return poTlphony.getMobilNumbers(); //returns device mobile number
        } else {
            return poDevConfig.getMobileNO(); //returns config mobile no
        }
    }
    public int hasMobileNo(){
        if(poDevConfig.getMobileNO().equalsIgnoreCase("")) {
            return View.VISIBLE;
        }
        return View.GONE;
    }
    public boolean isAgreed(){
        return poConfig.hasAgreedTermsAndConditions();
    }
    public void setAgreedOnTerms(boolean isAgreed){
        poConfig.setAppAgreement(isAgreed);
    }

    public void Login(LoginCredentials loginCredentials, LoginCallback callback){
        TaskExecutor.Execute(loginCredentials, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnAuthenticationLoad("Guanzon Circle", "Authenticating to ghostrider app. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                /*if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }*/

                int lnResult = poSys.DoAction(args);
                if (lnResult == 0 || lnResult == 2) {
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                boolean isSuccess = (boolean) object;
                if(isSuccess){
                    callback.OnSuccessLoginResult();
                } else {
                    callback.OnFailedLoginResult(message);
                }
            }
        });
    }
}