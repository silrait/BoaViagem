package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        finish();
        return true;
    }

    public void selecionarOpcao(View v){
        switch (v.getId()){
            case R.id.nova_viagem:
                startActivity( new Intent(this, ViagemActivity.class));
                break;

            case R.id.novo_gasto:
                startActivity( new Intent(this, GastoActivity.class));
                break;

            case R.id.minhas_viagens:
                startActivity( new Intent(this, ViagemListActivity.class));
                break;

            case R.id.configuracoes:
                startActivity( new Intent(this, ConfiguracoesActivity.class));
                break;
        }
    }
}
