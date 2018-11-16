package com.fivesys.alphamanufacturas.fivesys.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fivesys.alphamanufacturas.fivesys.R;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.view.ContextThemeWrapper;


public class Dialog {

    private static AlertDialog dialog;

    public static void MensajeOk(Context context, String titulo, String m) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        final TextView textViewMessage = v.findViewById(R.id.textViewMessage);
        final TextView textViewTitle = v.findViewById(R.id.textViewTitle);
        MaterialButton buttonCancelar = v.findViewById(R.id.buttonCancelar);
        buttonCancelar.setVisibility(View.GONE);
        MaterialButton buttonAceptar = v.findViewById(R.id.buttonAceptar);
        textViewTitle.setText(titulo);
        textViewMessage.setTextSize(18);
        textViewMessage.setText(m);
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


}
