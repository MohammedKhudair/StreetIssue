package com.barmej.streetissues.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.barmej.streetissues.R;
import com.barmej.streetissues.data.StreetIssueItem;
import com.barmej.streetissues.databinding.ActivityAddNewIssueBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewIssueActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityAddNewIssueBinding binding;

    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private static final int REQUEST_GET_PHOTO = 3;

    private boolean mLocationPermissionGranted;
    private boolean mReadStoragePermissionGranted;
    private FusedLocationProviderClient mLocationProviderClient;
    private LatLng mSelectedLatLng;
    private GoogleMap mGoogleMap;
    private Uri mPhotoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mReadStoragePermissionGranted) {
                    requestExternalStoragePermission();
                } else {
                    launchGalleryIntent();
                }
            }
        });

        binding.reportAnIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.editTextTitle.getText().toString();
                String description = binding.editTextDescription.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(AddNewIssueActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                } else if (mPhotoUri == null) {
                    Toast.makeText(AddNewIssueActivity.this, "Choose photo", Toast.LENGTH_SHORT).show();

                } else
                    addStreetIssue(title, description, mPhotoUri);
            }
        });


        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();
    }


    private void requestLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_LOCATION);
        }

    }

    private void requestExternalStoragePermission() {
        mReadStoragePermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mReadStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    requestDeviceCurrentLocation();
                } else
                    Toast.makeText(this, R.string.need_permission, Toast.LENGTH_LONG).show();
                break;

            case PERMISSION_REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mReadStoragePermissionGranted = true;
                    launchGalleryIntent();
                } else
                    Toast.makeText(this, R.string.need_permission_photo, Toast.LENGTH_LONG).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    private void requestDeviceCurrentLocation() {
        Task<Location> locationResult = mLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mSelectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLng, 15));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(mSelectedLatLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    markerOptions.title("Your location");
                    mGoogleMap.addMarker(markerOptions);
                } else
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.3760641, 47.9643571), 15));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (mLocationPermissionGranted) {
            requestDeviceCurrentLocation();
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mGoogleMap.clear();
                mSelectedLatLng = latLng;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mSelectedLatLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mGoogleMap.addMarker(markerOptions);
            }
        });

    }

    public void currentLocation(View view) {
        mGoogleMap.clear();
        requestDeviceCurrentLocation();
    }


    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose photo"), REQUEST_GET_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    mPhotoUri = data.getData();
                    binding.imageView.setImageURI(mPhotoUri);
                } catch (Exception e) {
                    Snackbar.make(binding.cardView, "Photo selection error", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void addStreetIssue(String title, String description, Uri photoUri) {
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setTitle(R.string.app_name);
        mDialog.setMessage(getString(R.string.uploading));
        mDialog.show();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference photoStorageReference = storageReference.child(UUID.randomUUID().toString());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        photoStorageReference.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    photoStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                StreetIssueItem streetIssueItem = new StreetIssueItem();
                                streetIssueItem.setTitle(title);
                                streetIssueItem.setDescription(description);
                                streetIssueItem.setPhoto(task.getResult().toString());
                                streetIssueItem.setLocation(new GeoPoint(mSelectedLatLng.latitude, mSelectedLatLng.longitude));

                                firebaseFirestore.collection("StreetsIssues").add(streetIssueItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(binding.cardView, R.string.uploading_success, Snackbar.LENGTH_LONG).show();
                                            mDialog.dismiss();
                                            finish();
                                        } else {
                                            mDialog.dismiss();
                                            Snackbar.make(binding.cardView, R.string.uploading_failed, Snackbar.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } else {
                                mDialog.dismiss();
                                Snackbar.make(binding.cardView, R.string.uploading_photo_failed, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Snackbar.make(binding.cardView, R.string.uploading_photo_failed, Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

}