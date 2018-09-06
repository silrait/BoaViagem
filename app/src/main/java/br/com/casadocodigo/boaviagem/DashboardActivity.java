package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard);
    }

    public void selecionarOpcao(View v){
        switch (v.getId()){
            case R.id.nova_viagem:
                startActivity( new Intent(this, ViagemActivity.class));
                break;

                default:
                    String opcao = "Opção: " + ((TextView) v).getText().toString();
                    Toast.makeText(this, opcao, Toast.LENGTH_SHORT).show();
        }


    }
}
