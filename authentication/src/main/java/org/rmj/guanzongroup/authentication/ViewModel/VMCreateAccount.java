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
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.rmj.g3appdriver.lib.authentication.GAuthentication;
import org.rmj.g3appdriver.lib.authentication.factory.Auth;
import org.rmj.g3appdriver.lib.authentication.factory.iAuthenticate;
import org.rmj.g3appdriver.lib.authentication.pojo.AccountInfo;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.authentication.Callback.CreateAccountCallBack;

public class VMCreateAccount extends AndroidViewModel{
    public static final String TAG = VMCreateAccount.class.getSimpleName();
    private final Application instance;
    private final ConnectionUtil poConn;
    private GAuthentication poAccount;
    private iAuthenticate poSys;
    private String message;

    public VMCreateAccount(@NonNull Application application) {
        super(application);
        this.instance = application;

        this.poConn = new ConnectionUtil(instance);
        this.poAccount = new GAuthentication(instance);
        this.poSys = poAccount.initAppAuthentication().getInstance(Auth.CREATE_ACCOUNT);
    }

    public void SubmitInfo(AccountInfo accountInfo, CreateAccountCallBack callBack){
        TaskExecutor.Execute(accountInfo, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callBack.OnAccountLoad("Guanzon Circle", "Creating Account. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    int lnResult = poSys.DoAction(args);
                    if(lnResult == 0 || lnResult == 2){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                boolean isSuccess = (boolean) object;
                if(!isSuccess){
                    callBack.OnFailedRegistration(message);
                    return;
                }

                callBack.OnSuccessRegistration();
            }
        });
    }
}