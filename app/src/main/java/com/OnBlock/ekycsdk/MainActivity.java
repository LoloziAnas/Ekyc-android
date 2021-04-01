package com.OnBlock.ekycsdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.onblock.onblock_ekyc.Ekyc;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    final String TAG = "MainActivity.java";
    private static final int REQUEST_CODE = 101;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //binding ui features
        imageView = findViewById(R.id.Image);
        textView = findViewById(R.id.textView);
        Button btnPress = findViewById(R.id.btn_press);

        btnPress.setOnClickListener(v -> {
            Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE);

        });
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }

    }
    private void selectImage(Context context){
        final CharSequence  [] options = {"Take picture", "Choose from gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose picture");
        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Take picture")){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            }
            if (options[which].equals("Choose from gallery")){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
            if (options[which].equals("Cancel")){
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void runRecognizer(Bitmap bitmap){
        //instancing an object of FirebaseVisionImage
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //instancing a FirebaseVisionTextRecognizer object
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        //creating task to precess the image
        Task<FirebaseVisionText> result = textRecognizer.processImage(image);
        result.addOnSuccessListener(firebaseVisionText -> {
            String resultText = firebaseVisionText.getText();
            textView.setText(resultText);


        })
        .addOnFailureListener(e -> Log.d(TAG,e.getMessage()));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Bundle bundle = data.getExtras();
        //from bundle extract the image
        Bitmap bitmap = (Bitmap) bundle.get("data");
        imageView.setImageBitmap(bitmap);
        runRecognizer(bitmap);

    }
}