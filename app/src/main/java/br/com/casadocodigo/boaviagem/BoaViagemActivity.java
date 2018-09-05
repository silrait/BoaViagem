package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends Activity{
    private EditText usuario;
    private EditText senha;

    public void entrarOnClick(View v){
        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();

        if( "leitor".equals(usuarioInformado) && "123".equals(senhaInformada)){
            startActivity(new Intent(this, DashboardActivity.class));
        }else{
            String mensagemErro = getString(R.string.erro_autenticacao);
            Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.login);

        usuario = findViewById(R.id.usuario);
        senha = findViewById(R.id.senha);
    }
}
