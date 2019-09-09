package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pathibharatechnology.smartkishan.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDiscussionPostActivity extends AppCompatActivity {

    EditText contentText;
    String content;
    ImageView image;
    Button uploadButton;
    FloatingActionButton addImageButton;
    Uri uri;
    Bitmap bitmap;
    StorageReference storageReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion_post);

        contentText = findViewById(R.id.contentTextId);
        image = findViewById(R.id.contentImageID);
        uploadButton = findViewById(R.id.uploadId);
        addImageButton = findViewById(R.id.addImageId);
        progressBar = findViewById(R.id.progressBarId);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (validate()) {
                    addDiscussionToDatabase();
                }
            }
        });


    }

    private void addDiscussionToDatabase() {

        progressBar.setVisibility(View.VISIBLE);
        final DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.setPostUploaderUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String discussionId = FirebaseDatabase.getInstance().getReference().child("discussions").push().getKey();
        discussionDTO.setPostId(discussionId);
        discussionDTO.setContent(content);
        discussionDTO.setCommentCount(0);

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date date = new Date();
        String strDate = dateFormat.format(date);

        discussionDTO.setDate(strDate);


        discussionDTO.setLikeCount(0);

        if (bitmap != null) {

            storageReference = FirebaseStorage.getInstance().getReference().child("discussion_pictures").child(discussionId)
                    .child(String.valueOf(System.currentTimeMillis()));
            storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        String downloadurl = uri.toString();
                        discussionDTO.setImageUrl(downloadurl);
                        uploadDiscussion(discussionDTO);
                    }

                }
            });
        } else {
            discussionDTO.setImageUrl("");
            uploadDiscussion(discussionDTO);

        }

    }

    private void uploadDiscussion(DiscussionDTO discussionDTO) {

        FirebaseDatabase.getInstance().getReference().child("discussions")
                .child(discussionDTO.getPostId())
                .setValue(discussionDTO)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(AddDiscussionPostActivity.this, DiscussionActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });

    }

    private boolean validate() {
        boolean isValid = false;
        content = contentText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            contentText.setError("Required");
            progressBar.setVisibility(View.GONE);
        } else {
            isValid = true;
        }
        return isValid;

    }


    //image selection tasks
    private void selectImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    private void cropRequest() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {

                Snackbar.make(getWindow().getDecorView().getRootView(), "आवश्यक अनुमतिहरू प्रदान गरिएको छैन", Snackbar.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                cropRequest();
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "आवश्यक अनुमतिहरू प्रदान गरिएको छैन", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uri = resultUri;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    image.setImageBitmap(bitmap);
                    image.setVisibility(View.VISIBLE);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }


    }


}
