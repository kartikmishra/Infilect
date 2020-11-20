package com.lokalhy.infilect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CapturedImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_image);


        ImageView close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath(), "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView imageView = findViewById(R.id.imageCaptured);
            imageView.setImageBitmap(b);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            startService(byteArray);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    private void startService(byte[] b) {
        Intent serviceIntent = new Intent(this, UploadImageService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private Bitmap rotate(Bitmap decodeBitmap) {

        int w = decodeBitmap.getWidth();
        int h = decodeBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);


        return Bitmap.createBitmap(decodeBitmap,0,0,w,h,matrix,true);
    }
}