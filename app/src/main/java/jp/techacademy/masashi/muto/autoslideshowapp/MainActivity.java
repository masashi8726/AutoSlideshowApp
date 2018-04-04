package jp.techacademy.masashi.muto.autoslideshowapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.os.Build;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer mTimer;
    double mTimerSec = 0.0;

    Handler mHandler = new Handler();

    Button mNextButton;
    Button mStartPauseButton;
    Button mBackButton;

    Cursor cursor;

    private void getContentsInfo() {
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    mStartPauseButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button) findViewById(R.id.nextButton);
        mStartPauseButton = (Button) findViewById(R.id.startPauseButton);
        mBackButton = (Button) findViewById(R.id.backButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        mStartPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mNextButton.setEnabled(false);
                        mBackButton.setEnabled(false);
                        mStartPauseButton.setText("停止");
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (cursor.moveToNext()) {
                                                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                                    Long id = cursor.getLong(fieldIndex);
                                                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                                    imageView.setImageURI(imageUri);
                                                } else {
                                                    cursor.moveToFirst();
                                                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                                    Long id = cursor.getLong(fieldIndex);
                                                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                                    imageView.setImageURI(imageUri);
                                                }
                                            } catch (NullPointerException e) {
                                                Toast toast = Toast.makeText(MainActivity.this, "許可しなければ表示されません", Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        }
                                    });
                                } catch (NullPointerException e) {
                                    Toast toast = Toast.makeText(MainActivity.this, "許可しなければ表示されません", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }, 2000, 2000);
                    } else {
                        mTimer.cancel();
                        mTimer = null;
                        mNextButton.setEnabled(true);
                        mBackButton.setEnabled(true);
                        mStartPauseButton.setText("再生");
                    }
                } catch (NullPointerException e) {
                    Toast toast = Toast.makeText(MainActivity.this, "許可しなければ表示されません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (cursor.moveToNext()) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    } else {
                        cursor.moveToFirst();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    }
                } catch (NullPointerException e) {
                    Toast toast = Toast.makeText(MainActivity.this, "許可しなければ表示されません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (cursor.moveToPrevious()) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    } else {
                        cursor.moveToLast();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    }
                } catch (NullPointerException e) {
                    Toast toast = Toast.makeText(MainActivity.this, "許可しなければ表示されません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            cursor.close();
        } catch (NullPointerException e) {

        }
    }
}
