package com.example.automessaging;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class PopupClientDetails extends AppCompatDialogFragment {

    TextView serie;
    TextView nrInmatriculare;
    TextView nume;
    TextView nrTelefon;
    TextView valabilitate;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_client_details, null);

        //region UI

        serie = view.findViewById(R.id.serie_popup);
        nrInmatriculare = view.findViewById(R.id.nrMatricol_popup);
        nume = view.findViewById(R.id.nume_popup);
        nrTelefon = view.findViewById(R.id.nrTel_popup);
        valabilitate = view.findViewById(R.id.valabilitate_popup);

        //endregion

        serie.setText(getArguments().getString("serie"));
        nrInmatriculare.setText(getArguments().getString("nrInmatriculare"));
        nume.setText(getArguments().getString("nume"));
        nrTelefon.setText(getArguments().getString("nrTelefon"));
        valabilitate.setText(getArguments().getString("valabilitate"));


        builder.setView(view).setTitle("Detalii").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
