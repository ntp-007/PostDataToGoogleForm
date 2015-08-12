package com.nip.android.postdatatogoogledrive;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//http://codesmith.in/post-data-google-drive-sheet-through-mobile-app/
public class MainActivity extends AppCompatActivity {
    //Post Data to Google Docs
    //https://docs.google.com/forms/d/10ToiR25sMKrbyto78vlRI7oU-SUMW-qeDeYMj8pPNLY/viewform
    //https://docs.google.com/forms/d/10ToiR25sMKrbyto78vlRI7oU-SUMW-qeDeYMj8pPNLY/formResponse
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String URL = "https://docs.google.com/forms/d/1_lR63RGcUzpu5qUAFJ0VTdMFMacasT8VHuCHXg8KzLg/formResponse";
    public static final String EMAIL_KEY = "entry_1382470853";
    public static final String SUBJECT_KEY = "entry_994168354";
    public static final String MESSAGE_KEY = "entry_1197258696";
    private Context mContext;
    private EditText emailEditText;
    private EditText subjectEditText;
    private EditText messageEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        emailEditText = (EditText) findViewById(R.id.edt_email);
        subjectEditText = (EditText) findViewById(R.id.edt_subject);
        messageEditText = (EditText) findViewById(R.id.edt_message);
        sendButton = (Button) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailEditText.getText().toString()) ||
                        TextUtils.isEmpty(subjectEditText.getText().toString()) ||
                        TextUtils.isEmpty(messageEditText.getText().toString())) {
                    Toast.makeText(mContext, "All fields are mandatory.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    Toast.makeText(mContext, "Please enter a valid email.", Toast.LENGTH_LONG).show();
                    return;
                }
                PostDataTask postDataTask = new PostDataTask();

                //execute asynctask
                postDataTask.execute(URL, emailEditText.getText().toString(),
                        subjectEditText.getText().toString(),
                        messageEditText.getText().toString());

            }
        });
    }

    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String email = contactData[1];
            String subject = contactData[2];
            String message = contactData[3];
            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + SUBJECT_KEY + "=" + URLEncoder.encode(subject, "UTF-8") +
                        "&" + MESSAGE_KEY + "=" + URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = false;
            }

            /*
            //If you want to use HttpRequest class from http://stackoverflow.com/a/2253280/1261816
            try {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.sendPost(url, postBody);
		}catch (Exception exception){
			result = false;
		}
            */

            try {
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            } catch (IOException exception) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Print Success or failure message accordingly
            Toast.makeText(mContext, result ? "Message successfully sent!" : "There was some error" +
                    " in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
