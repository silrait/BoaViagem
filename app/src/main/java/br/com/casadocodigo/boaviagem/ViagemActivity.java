package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.casadocodigo.boaviagem.dao.DBHelper;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class ViagemActivity extends Activity{
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private int ano, mes, dia;
    private Date dataChegada, dataSaida;
    private Button dataChegadaButton, dataSaidaButton;
    private Long id;
    private DBHelper helper;
    private Viagem viagem;

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Date fullDate = new Date(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

            dataSaida = fullDate;
            dataSaidaButton.setText(dateFormat.format(dataSaida));
        }
    };

    private DatePickerDialog.OnDateSetListener dataChegadaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Date fullDate = new Date(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

            dataChegada = fullDate;
            dataChegadaButton.setText(dateFormat.format(dataChegada));
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

        this.viagem = new Viagem();

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
        helper = new DBHelper(this);

        String retorno = getIntent().getStringExtra(Constantes.VIAGEM_ID);
        if(retorno !=null){
            id = new Long(retorno);
            prepararEdicao();
        }

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
                removerViagem(viagem);
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.dataChegada == id){
            return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);
        }else if( R.id.dataSaida == id){
            return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);
        }
        return null;
    }

    public void removerViagem(Viagem v){
        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();
            dao.delete(v);

            Toast.makeText(this, getString(R.string.viagem_removida), Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selecionarData(View view){
        showDialog(view.getId());
    }

    protected void prepararEdicao(){
        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();
            Viagem v = dao.queryForId(id);
            if (v != null) {
                this.viagem = v;

                if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
                    radioGroup.check(R.id.lazer);
                }else{
                    radioGroup.check(R.id.negocios);
                }

                destino.setText( viagem.getDestino() );

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                dataChegada = viagem.getDataChegada();
                dataChegadaButton.setText( df.format(dataChegada));

                dataSaida = viagem.getDataSaida();
                dataSaidaButton.setText( df.format(dataSaida));

                quantidadePessoas.setText( viagem.getQuantidadePessoas() );
                orcamento.setText( viagem.getOrcamento().toString() );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT tipo_viagem, destino, data_chegada, " +
                "data_saida, quantidade_pessoas, orcamento" +
                "FROM viagem WHERE _id = ?", new String[]{ id });

        cursor.moveToFirst();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(cursor.getInt(0) == Constantes.VIAGEM_LAZER ){
            radioGroup.check(R.id.lazer);
        }else{
            radioGroup.check(R.id.negocios);
        }

        destino.setText(cursor.getString(1));
        dataChegada = new Date( cursor.getLong(2));
        dataSaida = new Date( cursor.getLong(3));
        dataChegadaButton.setText( dateFormat.format(dataChegada));
        dataSaidaButton.setText( dateFormat.format(dataSaida));
        quantidadePessoas.setText( cursor.getString(4));
        orcamento.setText(cursor.getString(5));
        cursor.close();*/
    }


    public void salvarViagem(View view){
        Dao.CreateOrUpdateStatus resultado = null;
        DBHelper helper = new DBHelper(this);

        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();

            viagem.setDestino( destino.getText().toString());
            viagem.setDataChegada( new Date(dataChegada.getTime()) );
            viagem.setDataSaida( new Date(dataSaida.getTime()));
            viagem.setOrcamento( new Double(orcamento.getText().toString()) );
            viagem.setQuantidadePessoas( new Integer(quantidadePessoas.getText().toString()) );
            if( radioGroup.getCheckedRadioButtonId() == R.id.lazer ){
                viagem.setTipoViagem( Constantes.VIAGEM_LAZER );
            }else{
                viagem.setTipoViagem( Constantes.VIAGEM_NEGOCIO);
            }

            resultado = dao.createOrUpdate( viagem);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(resultado != null && resultado.getNumLinesChanged() > 0){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }


        /*SQLiteDatabase db = helper.getWritableDatabase();

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

        long resultado;

        if( id == null){
            resultado = db.insert("viagem", null, values);
        }else{
            resultado = db.update("viagem", values, "_id = ?", new String[]{ id });
        }*/
    }
}
