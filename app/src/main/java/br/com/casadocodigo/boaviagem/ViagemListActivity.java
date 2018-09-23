package br.com.casadocodigo.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;

    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;

    private class ViagemViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation){
            if(view.getId() == R.id.barraProgresso){
                Double[] valores = (Double[]) data;
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setMax(valores[0].intValue());
                progressBar.setSecondaryProgress(valores[1].intValue());
                progressBar.setProgress(valores[2].intValue());
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);

        helper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        valorLimite = Double.valueOf( preferencias.getString("valor_limite", "-1"));

        String[] de = {"imagem", "destino", "data", "total", "barraProgresso"};
        int[] para = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso};
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                listarViagens(),
                R.layout.lista_viagem,
                de,
                para);
        simpleAdapter.setViewBinder(new ViagemViewBinder());
        setListAdapter(simpleAdapter);

        getListView().setOnItemClickListener(this);
        this.alertDialog = criaAlertDialog();
    }

    protected double calcularTotalGasto(SQLiteDatabase db, String id){
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM gasto WHERE viagem_id = ?", new String[]{id});
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();

        return total;
    }

    protected List<Map<String, Object>>  listarViagens(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, tipo_viagem, destino," +
                "data_chegada, data_saida, orcamento FROM viagem", null);

        cursor.moveToFirst();

        viagens = new ArrayList<Map<String, Object>>();
        for(int i =0; i < cursor.getCount(); i++){
            Map<String,Object> item = new HashMap<String, Object>();

            String id = cursor.getString(0);
            int tipoViagem = cursor.getInt(1);
            String destino = cursor.getString(2);
            long dataChegada = cursor.getLong(3);
            long dataSaida = cursor.getLong(4);
            double orcamento = cursor.getDouble(5);

            item.put("id", id);

            if(tipoViagem == Constantes.VIAGEM_LAZER){
                item.put("imagem", R.drawable.lazer);
            }else{
                item.put("imagem", R.drawable.negocios);
            }
            item.put("destino", destino);

            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);
            String periodo = dateFormat.format(dataChegadaDate) + "a" +
                    dateFormat.format(dataSaidaDate);
            item.put("data", periodo);

            double totalGasto = calcularTotalGasto(db, id);
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = orcamento * valorLimite / 100;
            Double[] valores = new Double[]{orcamento, alerta, totalGasto};
            item.put("barraProgresso", valores);
            viagens.add(item);

            cursor.moveToNext();
        }
        cursor.close();

        return viagens;
    }

    private AlertDialog criaAlertDialog() {
        final CharSequence[] items = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover) };

        return new AlertDialog.Builder(this)
                .setTitle(R.string.opcoes)
                .setItems(items, this)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        this.viagemSelecionada = position;
        alertDialog.show();

    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        String id = (String) viagens.get(viagemSelecionada).get("id");

        switch (item){
            case 0:
                intent = new Intent(this, ViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;

            case 1:
                startActivity( new Intent(this, GastoActivity.class));
                break;

            case 2:
                startActivity( new Intent(this, GastoListActivity.class));
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(this.viagemSelecionada);
                removerViagem(id);
                getListView().invalidateViews();
                break;
        }
    }

    private void removerViagem(String id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] where = new String[]{ id };
        db.delete("gasto", "viagem_id = ?", where );
        db.delete("viagem", "_id = ?", where );
    }
}
