package org.secmem.gn.ctos.samdwich.keyboard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import org.secmem.gn.ctos.samdwich.R;


/**
 * Created by 김희중 on 2016-01-11.
 */
public class KeyboardPopupService extends Service{

    private static WindowManager mManager;
    private WindowManager.LayoutParams mParams;
    private static ImageView mImage;
    private int mPrimaryCode;
    private int mKeyboardWidth;
    private int mKeyboardHeight;
    private float mDownKeyRotate;
    private int mHandedness;
    private Bitmap extendImage;
    private Bitmap[] bitmaps;

    private final String TAG = "KeyboardPopupService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mPrimaryCode = Integer.parseInt(intent.getStringExtra("primaryCode").trim());
        mKeyboardWidth = intent.getIntExtra("keyboardWidth", 150);
        mKeyboardHeight = intent.getIntExtra("keyboardHeight", 150);
        mDownKeyRotate = intent.getFloatExtra("downKeyRotate", 0);
        mHandedness = intent.getIntExtra("handedness", 0); //0 : right, 1 : left;

        Log.i(TAG, "pc : " + mPrimaryCode + ", rot float : " + Math.toDegrees(mDownKeyRotate)+"DO");
        if(Build.VERSION.SDK_INT >= 23){
            //Log.i(TAG, "can ?  : " + Settings.canDrawOverlays(this));
        }

        if (mImage!=null) {
            onDestroy();
        }

        mImage = new ImageView(this);
        extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.popimg);

        //ㅁ
        if(mPrimaryCode == 12609){
            extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.mieum_extend);
        }
        //ㄴ
        else if(mPrimaryCode == 12596){
            extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.nieun_extend);
        }
        //ㅇ
        else if(mPrimaryCode == 12615){
            extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.ieung_extend);
        }
        //ㅅ
        else if(mPrimaryCode == 12613){
            extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.siot_extend);
        }
        //ㄱ
        else if(mPrimaryCode == 12593){
            extendImage = BitmapFactory.decodeResource(getResources(), R.drawable.giyeok_extend);
        }

        Matrix mat = new Matrix();
        mat.postRotate((float) (270+Math.toDegrees(mDownKeyRotate)));
        //mat.postScale((float) 0.6, (float) 0.6);

        extendImage = Bitmap.createScaledBitmap(extendImage, mKeyboardHeight / 3, mKeyboardHeight / 3, true);
        extendImage = Bitmap.createBitmap(extendImage, 0, 0, extendImage.getWidth(), extendImage.getHeight(), mat, true);
        mImage.setImageBitmap(extendImage);

        /*
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mImage = new ImageView(this);
        mImage = (ImageView)inflater.in
                WindowManager.LayoutParams.WRAP_CONTENT,flate(R.id.giyeok_extend_layout, null);
        Log.i(TAG, "LAYOUT = " + (LinearLayout.LayoutParams)mImage.getLayoutParams());
        */
        //mPrimaryCode에 따라서 다른 image 나타나도록

        mImage.setMaxHeight(1);
        mImage.setAlpha((float) 0.9999999);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        //Log.i(TAG, "display size x : " + metrics.widthPixels + ", y : " + metrics.heightPixels); //width가 가로, height가 세로
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        //right
        if(mHandedness == 0) {
            mParams.x = -screenWidth / 2 + mKeyboardHeight / 3; //최좌단
            mParams.y = screenHeight / 2 - mKeyboardHeight / 2; //최하단
        }
        else{
            mParams.x = +screenWidth / 2 - mKeyboardHeight / 3; //최좌단
            mParams.y = screenHeight / 2 - mKeyboardHeight / 2; //최하단
        }

        try {
            mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mManager.addView(mImage, mParams);
        }catch(Exception e)
        {
            Intent permissionIntent = new Intent(this, ExceptionActivity.class);
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissionIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Start");

        onDestroy();
        /*
        Intent intent = new Intent(this, KeyboardPopupService.class);

        Log.i(TAG, "pc : " + intent.getStringExtra("primaryCode"));
        //mPrimaryCode = Integer.parseInt(intent.getStringExtra("primaryCode").trim());
        //Log.i(TAG, "ck : " + mPrimaryCode);

        */
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "Service Destroy");

        if (mImage != null) {
            try {
                mManager.removeViewImmediate(mImage);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally {
                Log.i(TAG, "Recycle");
                extendImage.recycle();
            }
        }
        mImage = null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
