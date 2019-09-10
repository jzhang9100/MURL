package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class TextVision {
    private FirebaseVisionImage myImage = null;
    private CharSequence web = ".com";
    private FirebaseVisionText txt;
    private String url;

    public TextVision(Bitmap image) {
        myImage = FirebaseVisionImage.fromBitmap(image);
        recongizeText(myImage);
        String url = processText(txt);
        if (!(url.startsWith("http")))
            url = "http://" + url;
        this.url = url;
    }

    private String processText(FirebaseVisionText result) {
        String resultText = result.getText();
        String url = null;
        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                if (lineText.contains(web)) {
                    url = lineText;
                    return url;
                }
            }
        }
        return url;
    }

    private void recongizeText(FirebaseVisionImage image) {
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                txt = firebaseVisionText;
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });

    }

    public String getUrl(){
        return this.url;
    }
}
