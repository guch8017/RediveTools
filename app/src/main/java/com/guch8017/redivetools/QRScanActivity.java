package com.guch8017.redivetools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private ZXingView mZXingView;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        query = new Query(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //setTitle("扫描结果为：" + result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            String M3 = jsonObject.optString("M3");
            String Nn = jsonObject.getString("Nn");
            String MH = jsonObject.getString("MH");
            String description = jsonObject.getString("description");
            int server = jsonObject.optInt("server");
            DBAccountData data = new DBAccountData();
            data.server = server;
            data.M3 = M3;
            data.MH = MH;
            data.Nn = Nn;
            data.description = description;
            Toast.makeText(this, "成功", Toast.LENGTH_LONG).show();
            query.InsertLog(data);
            finish();
        }catch (Exception e){
            e.printStackTrace();
            //mZXingView.startSpot();
            Toast.makeText(this, "QRCode 解析失败", Toast.LENGTH_LONG).show();
        }
        //mZXingView.startSpot(); // 开始识别
        //finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机失败", Toast.LENGTH_LONG).show();
    }

}
