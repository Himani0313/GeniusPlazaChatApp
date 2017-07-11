package com.example.geniusplaza.geniusplazachatapp.Tutor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.geniusplaza.geniusplazachatapp.R;
import com.example.geniusplaza.geniusplazachatapp.Tutor.Pojo.Pod;
import com.example.geniusplaza.geniusplazachatapp.Tutor.Pojo.Query;
import com.example.geniusplaza.geniusplazachatapp.Tutor.Retrofit.RestClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivityTutor extends AppCompatActivity {

    TextView results,definition,headerResult,definitionHeader;
    public EditText queryWord;
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_tutor);

            results = (TextView) findViewById(R.id.queryResults);
            definition = (TextView) findViewById(R.id.queryDefinitions);
            queryWord = (EditText) findViewById(R.id.queryWord);
            headerResult = (TextView) findViewById(R.id.resultText);
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            definitionHeader = (TextView) findViewById(R.id.definitionText);

        }

    public void submitQueryWord (View v){
        results.setVisibility(View.GONE);
        definition.setVisibility(View.GONE);
        headerResult.setVisibility(View.GONE);
        definitionHeader.setVisibility(View.GONE);
        String searchWords = queryWord.getText().toString().replace("+", "plus");
        mProgressBar.setVisibility(View.VISIBLE);
        RestClient.getExampleApi().postGetQueryResult(searchWords)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.Observer<Query>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Query value) {
                Log.d("Pod Value", value.toString());
                mProgressBar.setVisibility(View.GONE);
                for (Pod i:value.getQueryresult().getPods()){
                    Log.d("Pre if Result Value: " , i.toString());
                    if(i.getTitle().equals("Result")||i.getTitle().equals("Results")){
                        Log.d("Result Value: " , i.toString());
                        headerResult.setVisibility(View.VISIBLE);
                        results.setVisibility(View.VISIBLE);
                        results.setText(i.getSubpods().get(0).getPlaintext().toString());
                    }
                    if(i.getTitle().equals("Definitions") || i.getTitle().equals("Definition")){
                        Log.d("Result Value: " , i.toString());
//                        results.setText(i.getSubpods().get(0).getPlaintext().toString());
                        definitionHeader.setVisibility(View.VISIBLE);
                        definition.setVisibility(View.VISIBLE);
                        definition.setText(i.getSubpods().get(0).getPlaintext().toString());
                    }


                    }
                if(results.getText().equals("Results")&&definition.getText().equals("Definitions")){
                    Toast.makeText(getApplicationContext(),"No results, Please try rewording the input",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivityTutor.this, "No results, Please try rewording the input", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
