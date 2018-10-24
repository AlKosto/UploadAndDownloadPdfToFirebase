package com.example.imran.uploadpdftofirebase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    Button selectFile, uplad, fetch;
    TextView notificaton;
    Uri pdfUri;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage= FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        selectFile = findViewById(R.id.selectFile);
        uplad= findViewById(R.id.upload);
        notificaton= findViewById(R.id.notification);
        fetch=(Button)findViewById(R.id.fetchFile) ;


        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MyRecyclerViewActivity.class));

            }
        });

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                {
                    selectPDF();
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  9);
                }

            }
        });



        uplad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pdfUri != null) {
                    uploadPdf(pdfUri);
                }else {
                    Toast.makeText(MainActivity.this, "You have to select a file", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void uploadPdf(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName =System.currentTimeMillis()+ ".pdf";
        final String fileName1 =System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference();

        storageReference.child("Files").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url = taskSnapshot.getDownloadUrl().toString();
                        DatabaseReference reference = database.getReference();
                        reference.child(fileName1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(MainActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "File not uploaded1"  +e, Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int currentProgress =(int) (100*taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);


            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }else {
            Toast.makeText(this, "Provide permission to read external storage.. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==86 && resultCode == RESULT_OK && data!= null){
            pdfUri = data.getData();

            notificaton.setText("A file is Selected :\n" +data.getData().getLastPathSegment());

        }else {
            Toast.makeText(this, "select the file", Toast.LENGTH_SHORT).show();
        }
    }
}






























