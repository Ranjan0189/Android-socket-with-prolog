package com.example.root.myapplication90;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements TCPListener {

    private TCPCommunicator tcpClient;
    private ProgressDialog dialog;
    public static String currentUserName;
    private Handler UIHandler = new Handler();
    private boolean isFirstLoad=true;
    private String obj1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectToServer();
    }

    private void ConnectToServer() {
        setupDialog();
        tcpClient = TCPCommunicator.getInstance();
        TCPCommunicator.addListener(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        tcpClient.init(settings.getString(EnumsAndStatics.SERVER_IP_PREF, "192.168.43.5"),
                Integer.parseInt(settings.getString(EnumsAndStatics.SERVER_PORT_PREF, "3050")));
    }

    private void setupDialog() {
        dialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setContentView(R.layout.activity_main);
        if(!isFirstLoad)
        {
            TCPCommunicator.closeStreams();
            ConnectToServer();
        }
        else
            isFirstLoad=false;
    }
    public void btnSendClick(View view)
    {
        EditText txtName= (EditText)findViewById(R.id.txtUserName);
        if(txtName.getText().toString().length()==0)
        {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        try
        {
           // obj.put(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON, EnumsAndStatics.MessageTypes.MessageFromClient);
            obj1 = txtName.getText().toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        TCPCommunicator.writeToSocket(obj1,UIHandler,this);
        //dialog.show();

    }

    @Override
    public void onTCPMessageRecieved(String message) {
        final String theMessage=message;


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            EditText editTextFromServer =(EditText)findViewById(R.id.editTextFromServer);
                            editTextFromServer.setText(theMessage);
                        }
                    });


    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
        if(isConnectedNow)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    dialog.hide();
                    Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void btnSettingsClicked(View view)
    {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}
