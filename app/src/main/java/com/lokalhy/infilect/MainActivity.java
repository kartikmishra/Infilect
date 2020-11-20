package com.lokalhy.infilect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder mSurfaceHolder;
    ImageView captureBtn;
    Camera.PictureCallback jpegCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},100);
        } else {
            inititalizeUiAndCamera();
        }
    }

    private void inititalizeUiAndCamera() {
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        captureBtn = findViewById(R.id.btn_capture);

        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Intent intent = new Intent(MainActivity.this, CapturedImageActivity.class);
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory, "profile" + ".jpg");
                if (file.exists()) {
                    file.delete();
                }
                Log.d("path", directory.toString());
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(file, false);
                    Bitmap decodeBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap rotateBitmap = rotate(decodeBitmap);
                    rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    fos.flush();
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                return;
            }
        };
    }

    private Bitmap rotate(Bitmap decodeBitmap) {

        int w = decodeBitmap.getWidth();
        int h = decodeBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);


        return Bitmap.createBitmap(decodeBitmap,0,0,w,h,matrix,true);
    }

    private void captureImage() {
        camera.takePicture(null,null,jpegCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);


//        Camera.Size bestSize = null;
//        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
//        bestSize = sizeList.get(0);
//
//        for (int i=1; i < sizeList.size(); i++) {
//            if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
//                bestSize = sizeList.get(i);
//            }
//        }
//
//        parameters.setPreviewSize(bestSize.width,bestSize.height);

        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   inititalizeUiAndCamera();
                } else {
                    Toast.makeText(this,"Please provide permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}