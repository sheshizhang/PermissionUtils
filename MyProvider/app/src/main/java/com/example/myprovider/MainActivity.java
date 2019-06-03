package com.example.myprovider;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.permissionlibrary.FrPermission;

import java.security.Permissions;

public class MainActivity extends AppCompatActivity {
    public static final int SDCARD_CODE=10;
    public static final String TAG="TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onclickBtn1(View view) {
        FrPermission.with(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .addRequestCode(SDCARD_CODE)
                .request(new FrPermission.FrPermissionCallback() {
                    @Override
                    public void permissionSuccess(int requsetCode) {
                        Log.w(TAG,"开启权限成功");
                    }

                    @Override
                    public void permissionFail(int requestCode) {
                        Log.w(TAG,"开启权限失败");
                    }
                });
    }
}
