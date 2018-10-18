package com.uniquext.android.imageeditordemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.widget.Toast;

import com.uniquext.android.imageeditor.helper.DrawableManager;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.picture);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.picture);
        DrawableManager.getInstance().init(bitmap);
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("scheme://com.uniquext.image-editor/main")), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DrawableManager.SAVE) {
            Bitmap bitmap = DrawableManager.getInstance().getDrawableBitmap();
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "没有保存", Toast.LENGTH_SHORT).show();
        }
    }
}
