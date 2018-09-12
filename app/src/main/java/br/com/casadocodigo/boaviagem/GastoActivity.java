package br.com.casadocodigo.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

public class GastoActivity extends Activity {
    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            ano = year;
            mes = month;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes+1) + "/" + ano);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gasto);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categoria_gasto,
                android.R.layout.simple_spinner_dropdown_item
        );

        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataGasto = (Button) findViewById(R.id.data);
        dataGasto.setText( dia + "/" + (mes+1) + "/" + ano);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.data == id){
            return new DatePickerDialog(this, listener, ano, mes, dia);
        }
        return null;
    }

    public void selecionarData(View view){
        showDialog(view.getId());
    }
}
