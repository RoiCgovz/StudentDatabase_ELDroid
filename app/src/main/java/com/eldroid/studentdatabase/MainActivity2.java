package com.eldroid.studentdatabase;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity2 extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName;
    private Spinner spinnerCourse;
    private Uri selectedImageUri;
    private StudentDatabaseHelper dbHelper;
    private int editId = -1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Declarations
        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.editName);
        spinnerCourse = findViewById(R.id.courseSpinner);
        Button btnSave = findViewById(R.id.saveButton);
        Button btnCancel = findViewById(R.id.cancelButton);
        dbHelper = new StudentDatabaseHelper(this);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.courses_spinner, R.layout.spinner_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri pickedUri = result.getData().getData();
                        if (pickedUri != null) {
                            try {
                                // Load and display image immediately
                                InputStream inputStream = getContentResolver().openInputStream(pickedUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                imgProfile.setImageBitmap(bitmap);
                                inputStream.close();

                                // Save URI for later internal storage copy
                                selectedImageUri = pickedUri;

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openImagePicker();
                    else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        );

        // ImageView click to pick image
        imgProfile.setOnClickListener(v -> checkPermissionAndPick());

        // Load student if editing
        editId = getIntent().getIntExtra("id", -1);
        if (editId != -1) loadStudentData(editId);

        // Buttons
        btnSave.setOnClickListener(v -> saveStudent());
        btnCancel.setOnClickListener(v -> finish());
    }

    // Check permission and Open image picker
    private void checkPermissionAndPick() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Load student from database
    private void loadStudentData(int id) {
        Student s = dbHelper.getStudentById(id);
        if (s != null) {
            etName.setText(s.getName());
            selectedImageUri = s.getImageUri() != null ? Uri.parse(s.getImageUri()) : null;

            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgProfile.setImageBitmap(bitmap);
                    assert inputStream != null;
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    imgProfile.setImageResource(R.drawable.ic_person);
                }
            } else {
                imgProfile.setImageResource(R.drawable.ic_person);
            }

            for (int i = 0; i < spinnerCourse.getCount(); i++) {
                if (spinnerCourse.getItemAtPosition(i).toString().equals(s.getCourse())) {
                    spinnerCourse.setSelection(i);
                    break;
                }
            }
        }
    }

    // Save student
    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String course = spinnerCourse.getSelectedItem().toString();

        // Validate name
        if (name.isEmpty()) {
            etName.setError("Name must not be empty");
            etName.requestFocus();
            return;
        }

        // Validate image
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save image internally
        String imageUriStr = null;
        if (selectedImageUri != null) {
            Uri savedUri = saveImageToInternalStorage(selectedImageUri);
            if (savedUri != null) {
                imageUriStr = savedUri.toString();
                selectedImageUri = savedUri;
            }
        }

        long result;
        if (editId == -1) {
            result = dbHelper.insertStudent(name, course, imageUriStr);
        } else {
            result = dbHelper.updateStudent(editId, name, course, imageUriStr);
        }

        if (result != -1) {
            Toast.makeText(this, "Student saved successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save student", Toast.LENGTH_SHORT).show();
        }
    }

    // Copy image to internal storage
    private Uri saveImageToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file); // safe to reopen anytime
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
