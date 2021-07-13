package com.example.automessaging;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PopupVerifyMessageSending extends AppCompatDialogFragment {

    TextView numeClient;
    TextView nrTelClient;
    Button sendMessage;
    TextView displayMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.popup_verify_message_sending, null);

        //region UI

        numeClient = view.findViewById(R.id.nume_client);
        nrTelClient= view.findViewById(R.id.nrTelefon_client);
        sendMessage = view.findViewById(R.id.verify_send_btn);
        displayMessage = view.findViewById(R.id.display_message);

        //endregion

        numeClient.setText(getArguments().getString("nume"));
        nrTelClient.setText(getArguments().getString("nrTelefon"));


         final String nrTel = nrTelClient.getText().toString();
         final String mesaj = getArguments().getString("mesaj");

        displayMessage.setText(mesaj);


        builder.setView(view).setTitle("Are you sure you want to send the message?").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                sendSMS(nrTel , mesaj);
                dismiss();

            }
        });

        return builder.create();
    }


    public void sendSMS(String nrTelefon, String mesaj)
    {
        try{
            SmsManager smsManager = SmsManager.getDefault();

            int mesajLength = mesaj.length();

            if(mesajLength > 70)
            {
                ArrayList<String> messagelist = smsManager.divideMessage(mesaj);
                smsManager.sendMultipartTextMessage(nrTelefon, null , messagelist , null , null);
                Toast.makeText(getContext(), "Sent! (multiple)", Toast.LENGTH_SHORT).show();
            }
            else
            {
                smsManager.sendTextMessage("0756917157", null , mesaj , null, null);
                Toast.makeText(getContext(), "Sent! (single)", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed" , Toast.LENGTH_SHORT).show();
        }
    }
    }




