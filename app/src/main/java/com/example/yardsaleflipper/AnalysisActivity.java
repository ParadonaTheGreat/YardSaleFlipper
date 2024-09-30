package com.example.yardsaleflipper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class AnalysisActivity extends AppCompatActivity{

    //region API KEYS
    private final String API_TOKEN = "hf_XXXXXXXXXXXXXXXXXXXXXXXX";
    private final String APP_ID = "XXXXXXXXXXXXXXXXXXXXXX";
    //endregion

    String filePath;
    ImageView photoTaken;
    TextView imageContent;
    TextView priceList, titleList;
    String tempPriceList = "Price";
    String tempTitleList = "Item Name";
    String query;
    EditText enteredText;
    ListView listView;
    ArrayList<String> itemList;
    ArrayAdapter<String> arr;
    ArrayList<ArrayList<String>> detailedItemList;
    LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analysis);

        photoTaken = findViewById(R.id.pictureTaken);
        imageContent = findViewById(R.id.imageContent);
        //priceList = findViewById(R.id.priceList);
        //titleList = findViewById(R.id.titleList);
        enteredText = findViewById(R.id.enterInstead);
        listView = findViewById(R.id.itemList);
        itemList = new ArrayList<>();
        detailedItemList = new ArrayList<>();
        layoutInflater = getLayoutInflater();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getIntExtra("reqCode",0) == 0) {
                filePath = intent.getStringExtra("photoFilePath");
                File imgFile = new File(filePath);
                if (imgFile.exists()) {
                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photoTaken.setImageBitmap(imgBitmap);
                }
                processImage(filePath);
                query = imageContent.getText().toString();
            }
            else{
                filePath = intent.getStringExtra("photoFilePath");
                query = intent.getStringExtra("query");
                File imgFile = new File(filePath);
                if (imgFile.exists()){
                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photoTaken.setImageBitmap(imgBitmap);
                }
                imageContent.setText(intent.getStringExtra("imageContent"));
                enteredText.setText(query);
            }
        }

        findEbayPricing();

    }

    private void findEbayPricing() {
        //priceList.setText("");
        //titleList.setText("");
        tempPriceList = "Price";
        tempTitleList = "Item Name";
        itemList.clear();
        detailedItemList.clear();

        Thread r = new Thread() {
            @Override
            public void run() {
                try {
                    if (query.contains(",")){
                        query = query.substring(0,query.indexOf(","));
                    }
                    String query2 = URLEncoder.encode(query, "UTF-8");
                    String endpoint = "https://svcs.sandbox.ebay.com/services/search/FindingService/v1";
                    String params = "OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME="+ APP_ID + "&RESPONSE-DATA-FORMAT=JSON&keywords=" + query2;
                    URL url = new URL(endpoint + "?" + params);
                    System.out.println(url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray itemsArray = jsonResponse.getJSONArray("findItemsByKeywordsResponse").getJSONObject(0).getJSONArray("searchResult").getJSONObject(0).getJSONArray("item");

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        //System.out.println(item.toString());
                        String title = item.getJSONArray("title").getString(0);
                        String price = item.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");

                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(title);

                        if (title.length()>27){
                            title = title.substring(0,24) + "...";
                        }
                        double price2 = Double.valueOf(price);
                        price = String.format("$%.2f", price2);

                        tempTitleList += "\n" + title;
                        tempPriceList += "\n$" + price;
                        String tempListAddition = String.format("%-27s %8s", title, price);
                        //tempListAddition += String.format("%-1s", price);

                        arrayList.add(price);
                        arrayList.add(item.getJSONArray("galleryURL").getString(0));
                        detailedItemList.add(arrayList);
                        itemList.add(tempListAddition);
                    }
                    //runOnUiThread(() -> titleList.setText(tempTitleList));
                    //runOnUiThread(() -> priceList.setText(tempPriceList));
                    arr = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_item, R.id.textView, itemList);
                    runOnUiThread(() -> listView.setAdapter(arr));


                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (itemList.get(position) == "No results found."){
                                System.out.println("None");
                            }
                            else{
                                Intent intent = new Intent(AnalysisActivity.this, EbayItemDescriptionActivity.class);
                                intent.putExtra("details", detailedItemList.get(position));
                                intent.putExtra("photoFilePath",filePath);
                                intent.putExtra("query",query);
                                intent.putExtra("imageContent", imageContent.getText().toString());
                                startActivity(intent);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    itemList.clear();
                    itemList.add("No results found.");
                    arr = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_item, R.id.textView, itemList);
                    runOnUiThread(() -> listView.setAdapter(arr));
                    //runOnUiThread(() -> titleList.setText("Error: " + e.getMessage()));
                }

            }
        };
        r.start();
    }




    private void processImage(String filePath) {
        try {
            System.out.println("process started");
            String[] command = {
                    "curl",
                    "https://api-inference.huggingface.co/models/google/vit-base-patch16-224",
                    "-X", "POST",
                    "--data-binary", "@" + filePath,
                    "-H", "Authorization: Bearer " + API_TOKEN };
            Process execute = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(execute.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = stdInput.readLine()) != null) {
                output.append(line);
            }
            BufferedReader stdError = new BufferedReader(new InputStreamReader(execute.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();

            while ((line = stdError.readLine()) != null) {
                errorOutput.append(line);
            }

            // Log the full response and error
            Log.d("API Response", output.toString());
            Log.e("API Error", errorOutput.toString());

            String result = output.toString();
            String formattedResult = result.substring(11);
            formattedResult = formattedResult.substring(0,formattedResult.indexOf("\""));

            imageContent.setText(formattedResult);
            Log.d("TAG", result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void retakePressed(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, 1, null);
    }


    public void enterPressed(View view) {
        query = enteredText.getText().toString();
        findEbayPricing();
    }

}
