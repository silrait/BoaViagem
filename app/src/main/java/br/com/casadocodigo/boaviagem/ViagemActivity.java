package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class ViagemActivity extends Activity{

    private DatabaseHelper helper;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private int ano, mes, dia;
    private Date dataChegada, dataSaida;
    private Button dataChegadaButton, dataSaidaButton;

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Button pickedButton = null;
            Date fullDate = new Date(year, month, dayOfMonth);
            int id = view.getId();

            if(R.id.dataSaida == id ){
                pickedButton = dataSaidaButton;
                dataSaida = fullDate;
            } else if(R.id.dataChegada == id){
                pickedButton = dataChegadaButton;
                dataSaida = fullDate;
            }

            if( pickedButton != null ) pickedButton.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }
    };

    @Override
    public void onDestroy(){
        helper.close();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.cadastro_viagem);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegadaButton = (Button) findViewById(R.id.dataChegada);
        dataSaidaButton = (Button) findViewById(R.id.dataSaida);
        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas = (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);
        helper = new DatabaseHelper(this);

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

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.dataChegada == id || R.id.dataSaida == id){
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }

    public void selecionarData(View view){
        showDialog(view.getId());
    }

    public void salvarViagem(View view){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destino", destino.getText().toString());
        values.put("dataChegada", dataChegada.getTime());
        values.put("dataSaida", dataSaida.getTime());
        values.put("orcamento", orcamento.getText().toString());
        values.put("quantidade_pessoas", quantidadePessoas.getText().toString());

        int tipo = radioGroup.getCheckedRadioButtonId();
        if(tipo == R.id.lazer){
            values.put("tipo_viagem", Constantes.VIAGEM_LAZER);
        }else {
            values.put("tipo_viagem", Constantes.VIAGEM_NEGOCIO);
        }

        long resultado = db.insert("viagem", null, values);

        if(resultado != -1){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }
}
