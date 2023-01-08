package com.example.qrscanerti21c4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //view Object
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewId;

    //qrcode scaner objek
    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

        buttonScanning = (Button) findViewById(R.id.buttonscan);
        textViewName = (TextView) findViewById(R.id.textViewnama);
        textViewClass = (TextView) findViewById(R.id.textViewkelas);
        textViewId = (TextView) findViewById(R.id.textViewnim);

        qrScan = new IntentIntegrator( this);

        //inplementasi onClik listener
        buttonScanning.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            //jika hasil scaner tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "hasil scaner tidak ada", Toast.LENGTH_LONG).show();
            }else if (result.getContents().startsWith("geo:")) {
                String map = String.valueOf(result.getContents());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                //webview
                else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                    Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                    startActivity(visitUrl);
                }
                // geolokasi
                else if (result.getContents().startsWith("geo:")) {
                    String geoUri = result.getContents();
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(geoIntent);
                }
                }else{
                //jika qrcode ada isinya
                try {
                    //conversi data ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //diset nilai datanya ke textview
                    textViewName.setText(obj.getString("nama"));
                    textViewClass.setText(obj.getString("kelas"));
                    textViewId.setText(obj.getString("nim"));
                }catch (JSONException e){
                    e.printStackTrace();
                        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
                // DIAL UP, NOMOR TELEPON
                try {
                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                    startActivity(intent2);
                } catch (Exception e){
                    Toast.makeText(this, "Not Scanned", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}