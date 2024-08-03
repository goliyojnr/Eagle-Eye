package com.cholera.eagleeye.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cholera.eagleeye.R;
import com.cholera.eagleeye.services.VolleyMultipartRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CameraFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ImageView imageView;
    private Button captureButton;
    private Button uploadButton;

    private Bitmap capturedImage;
    private Uri capturedVideoUri;

    private RequestQueue requestQueue;

    private static final String SERVER_URL = "http://your_server_url_here/upload";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView);
        captureButton = view.findViewById(R.id.capture_button);
        uploadButton = view.findViewById(R.id.upload_button);

        requestQueue = Volley.newRequestQueue(requireContext());

        // Check and request camera permission if not granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        captureButton.setOnClickListener(v -> dispatchTakePictureOrVideoIntent());

        uploadButton.setOnClickListener(v -> {
            if (capturedImage != null) {
                uploadImage();
            } else if (capturedVideoUri != null) {
                uploadVideo();
            } else {
                Toast.makeText(requireContext(), "No media to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureOrVideoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // Option to choose an image from the gallery
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(takePictureIntent, "Capture Image or Video");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takeVideoIntent, pickPhotoIntent});

        startActivityForResult(chooser, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                capturedVideoUri = data.getData();
                try {
                    capturedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), capturedVideoUri);
                    imageView.setImageBitmap(capturedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (data != null && data.getExtras() != null) {
                capturedImage = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(capturedImage);
            }
        }
    }

    private void uploadImage() {
        if (capturedImage == null) {
            Toast.makeText(requireContext(), "No image to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
        params.put("file", new VolleyMultipartRequest.DataPart("image.jpg", imageData, "image/jpeg"));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, SERVER_URL,
                response -> {
                    try {
                        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonResponse = new JSONObject(jsonString);
                        String result = jsonResponse.getString("result");
                        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String errorMessage = "Image Upload failed: " + error.getMessage();
                    Log.e("UPLOAD_ERROR", errorMessage);
                    Toast.makeText(requireContext(), "Image Upload failed", Toast.LENGTH_SHORT).show();
                }, params);

        requestQueue.add(multipartRequest);
    }

    private void uploadVideo() {
        if (capturedVideoUri == null) {
            Toast.makeText(requireContext(), "No video to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(capturedVideoUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] videoData = baos.toByteArray();

            Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
            params.put("file", new VolleyMultipartRequest.DataPart("video.mp4", videoData, "video/mp4"));

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                    Request.Method.POST, SERVER_URL,
                    response -> {
                        try {
                            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject jsonResponse = new JSONObject(jsonString);
                            String result = jsonResponse.getString("result");
                            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        String errorMessage = "Video Upload failed: " + error.getMessage();
                        Log.e("UPLOAD_ERROR", errorMessage);
                        Toast.makeText(requireContext(), "Video Upload failed", Toast.LENGTH_SHORT).show();
                    }, params);

            requestQueue.add(multipartRequest);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error uploading video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





