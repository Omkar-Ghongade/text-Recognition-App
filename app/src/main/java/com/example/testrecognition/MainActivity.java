package com.example.testrecognition

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView mTvLabel;
    private Button btncamera;
    private Button btngallery;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        // TODO this is owned by me

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvLabel = findViewById(R.id.textView);
        btncamera = findViewById(R.id.btncamera);
        btngallery = findViewById(R.id.btngallery);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
        }
        else
        {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 99 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            init();
        }
    }

    public void init()
    {
        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camIntent, 91);
            }
        });


        btngallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galIntent, 92);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 92)
        {
            if(data!=null && data.getData()!=null)
            {
                Uri selImg = data.getData();

                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selImg);
                    extractText(bitmap, 0);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        else if(requestCode == 91)
        {
            if(data!=null && data.getData()!=null)
            {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                extractText(bitmap, 90);
            }
        }

    }

    private void extractText(Bitmap bitmap, int rotation)
    {
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotation);

        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(@NonNull Text text)
            {

                mTvLabel.setText(text.getText());
                Log.d("Omkar","Text In Image : "+text.getText());
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Exception e)
            {
                mTvLabel.setText("Error = "+e);
            }
        });
    }
}
