package com.example.yardsaleflipper;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class EbayItemDescriptionActivity extends AppCompatActivity {

    String imageURL;
    String name;
    String price;
    ImageView itemImage;
    TextView itemName, itemPrice;
    String query;
    String filePath;
    String imageContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ebay_item_description);

        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.price);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> detailedItemList = intent.getStringArrayListExtra("details");
            name = detailedItemList.get(0);
            price = detailedItemList.get(1);
            imageURL = detailedItemList.get(2);
            filePath = intent.getStringExtra("photoFilePath");
            query = intent.getStringExtra("query");
            imageContent = intent.getStringExtra("imageContent");
        }
        try {
            Picasso.get().load(imageURL).into(itemImage);
        } catch (Exception e) {
            itemImage.setImageResource(R.drawable.noimagefound);
        }
        itemName.setText(name);
        itemPrice.setText(price);

    }

    public void onBackPressed(View view) {
        Intent intent = new Intent(EbayItemDescriptionActivity.this, AnalysisActivity.class);
        intent.putExtra("reqCode",1);
        intent.putExtra("photoFilePath", filePath);
        intent.putExtra("query",query);
        intent.putExtra("imageContent",imageContent);

        startActivity(intent);
    }
}