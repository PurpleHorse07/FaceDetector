package com.example.sayantan.detection;

import android.content.Intent;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.ProgressDialogHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE = 2;

    FirebaseVisionImage image;
    FirebaseVisionFaceDetectorOptions options;
    FirebaseVisionFaceDetector detector;
    Task<List<FirebaseVisionFace>> result;

    ProgressDialogHolder progressDialogHolder;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.img);
        options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.15f)
                .setTrackingEnabled(true)
                .build();
        detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
    }

    private void carryOn() {
        progressDialogHolder = new ProgressDialogHolder(this);
        progressDialogHolder.showLoadingDialog(R.string.app_name);
        result = detector.detectInImage(image).addOnSuccessListener(
                new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        // Task completed successfully
                        // ...
                        progressDialogHolder.dismissDialog();
                        done(faces);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                progressDialogHolder.dismissDialog();
                                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
        //if(result.isSuccessful())


    }

    private void done(List<FirebaseVisionFace> faces) {
        ArrayList<Float> Y = new ArrayList<>();
        ArrayList<Float> Z = new ArrayList<>();
        ArrayList<String> S = new ArrayList<>();
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            //bounds.
            //Y.add(face.getHeadEulerAngleY());  // Head is rotated to the right rotY degrees
            //Z.add(face.getHeadEulerAngleZ());  // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
            FirebaseVisionPoint leftEarPos = null;
            if (leftEar != null) {
                leftEarPos = leftEar.getPosition();
            }

            S.add(face.getHeadEulerAngleY() + " and " + face.getHeadEulerAngleZ() + "\n" + leftEarPos);
            // If classification was enabled:
            float smileProb = 0;
            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }
            Toast.makeText(this, "" + face.getHeadEulerAngleY() + " and " + face.getHeadEulerAngleZ() + "\n" + leftEarPos + "\n\n" + smileProb, Toast.LENGTH_LONG).show();
            // If face tracking was enabled:
            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                int id = face.getTrackingId();
            }
        }
    }

    public void camera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void gallery(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri img = data.getData();
                try {
                    if (img != null) {
                        //imageView
                        image = FirebaseVisionImage.fromFilePath(this, img);
                        carryOn();
                    } else
                        throw new IOException();
                } catch (IOException e) {
                    Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

}
