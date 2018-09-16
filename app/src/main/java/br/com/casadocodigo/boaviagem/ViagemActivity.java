package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ViagemActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);

        setContentView(R.layout.cadastro_viagem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        switch (item.getItemId()){
            case R.id.novo_gasto:
                startActivity(new Intent(this, GastoActivity.class));
                return true;

            case R.id.remover:
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
}
