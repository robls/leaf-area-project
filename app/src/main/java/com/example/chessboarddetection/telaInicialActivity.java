package com.example.chessboarddetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.opencv.core.Mat;

import SQLHelper.Helper;

public class telaInicialActivity extends AppCompatActivity {

    private Button btProsseguir;
    private EditText etComprimento;
    private EditText etLargura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        btProsseguir = findViewById(R.id.btProsseguir);
        etComprimento = findViewById(R.id.etComprimento);
        etLargura = findViewById(R.id.etLargura);

        btProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etComprimento.getText().equals("")||etLargura.getText().equals("")){
                    Toast.makeText(getApplicationContext(), "Inserir os valores", Toast.LENGTH_LONG).show();
                }else{
                    String aux = etComprimento.getText().toString().replaceAll(",", ".");
                    double comprimento = Double.parseDouble(aux);
                    aux = etLargura.getText().toString().replaceAll(",", ".");
                    double largura = Double.parseDouble(aux);
                    Intent myIntent = new Intent(telaInicialActivity.this, MainActivity.class);
                    myIntent.putExtra("comprimento", comprimento);
                    myIntent.putExtra("largura", largura);
                    startActivity(myIntent);
                }
            }
        });


    }
}
