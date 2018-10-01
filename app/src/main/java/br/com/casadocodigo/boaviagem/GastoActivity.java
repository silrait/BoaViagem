package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import br.com.casadocodigo.boaviagem.dao.GastoDao;
import br.com.casadocodigo.boaviagem.dao.ViagemDao;
import br.com.casadocodigo.boaviagem.model.Gasto;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class GastoActivity extends Activity {
    private Button dataGastoButton;
    private Date dataGasto;
    private Spinner categoria;
    private GastoDao gastoDao;
    private EditText valor, descricao, local;
    private Viagem viagem;
    private ViagemDao viagemDao;

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("ano: " + year);
            dataGasto = new GregorianCalendar(year,month,dayOfMonth).getTime();
            dataGastoButton.setText(df.format(dataGasto));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gastoDao = new GastoDao(this);

        setContentView(R.layout.gasto);

        valor = (EditText) findViewById(R.id.valor);
        descricao = (EditText) findViewById(R.id.descricao);
        local = (EditText) findViewById(R.id.local);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categoria_gasto,
                android.R.layout.simple_spinner_dropdown_item
        );
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        dataGasto = Calendar.getInstance().getTime();
        dataGastoButton = (Button) findViewById(R.id.data);
        dataGastoButton.setText( new SimpleDateFormat("dd/MM/yyyy").format(dataGasto) );

        viagemDao = new ViagemDao(this);

        Long viagemId = Long.getLong( (String) getIntent().getStringExtra(Constantes.VIAGEM_ID) );
        if( viagemId != null ){
            viagem = viagemDao.buscaPorId(viagemId);
        }
    }

    @Override
    protected void onDestroy(){
        gastoDao.closeHelper();
        viagemDao.closeHelper();
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.data == id){
            return new DatePickerDialog(this, listener,
                    dataGasto.getYear(),
                    dataGasto.getMonth(),
                    dataGasto.getDay());
        }
        return null;
    }

    public void registrarGasto(View view){
        Gasto g = new Gasto();
        g.setCategoria( categoria.getSelectedItem().toString() );
        g.setData( dataGasto );
        g.setDescricao( descricao.getText().toString() );
        g.setLocal( local.getText().toString() );
        g.setValor( new Double( valor.getText().toString()) );

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean modoViagem = preferencias.getBoolean("modo_viagem", false);
        if(modoViagem) {
            System.out.println("viagem_ativa: " + preferencias.getString("viagem_ativa", "-1"));

            Long viagemId = Long.getLong( preferencias.getString("viagem_ativa", "-1"));
            if( viagemId != null && viagemId >= 0){
                viagem = viagemDao.buscaPorId(viagemId);
            }
        }

        if(viagem != null){
            g.setViagem(viagem);
            if( gastoDao.salvar(g) ){
                Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
            }
        }else{
            //TODO: criar a possibilidade de escolher a viagem para o gasto.
            Toast.makeText(this, getString(R.string.modo_viagem), Toast.LENGTH_SHORT).show();
        }
    }

    public void selecionarData(View view){
        showDialog(view.getId());
    }
}
