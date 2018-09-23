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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.casadocodigo.boaviagem.dao.DBHelper;
import br.com.casadocodigo.boaviagem.model.Gasto;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class ViagemListActivity extends ListActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;

    private DBHelper helper;
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

        helper = new DBHelper(this);
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

    @Override
    public void onDestroy(){
        helper.close();
        super.onDestroy();
    }

    protected double calcularTotalGasto(Long id){
        double total = 0.0;
        try {
            Dao<Gasto, Long> dao = helper.getGastoDAO();
            List<Gasto> gastos = dao.queryBuilder()
                    .selectRaw("SUM(valor)")
                    .where()
                    .eq(Gasto.VIAGEM_ID, id)
                    .query();
            total = gastos.get(0).getValor();
        } catch (SQLException e) {
            e.printStackTrace();
        }

       /* Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM gasto WHERE viagem_id = ?", new String[]{id});
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();*/

        return total;
    }

    protected List<Map<String, Object>>  listarViagens(){
        /*SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, tipo_viagem, destino," +
                "data_chegada, data_saida, orcamento FROM viagem", null);

        cursor.moveToFirst();*/

        List<Viagem> viagemList = null;

        try {
            viagemList = helper.getViagemDAO().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(viagemList == null)
            return null;

        viagens = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < viagemList.size(); i++){
            Viagem v = viagemList.get(i);
            Map<String,Object> item = new HashMap<String, Object>();

            /*String id = cursor.getString(0);
            int tipoViagem = cursor.getInt(1);
            String destino = cursor.getString(2);
            long dataChegada = cursor.getLong(3);
            long dataSaida = cursor.getLong(4);
            double orcamento = cursor.getDouble(5);*/

            item.put("id", v.getId());

            if(v.getTipoViagem() == Constantes.VIAGEM_LAZER){
                item.put("imagem", R.drawable.lazer);
            }else{
                item.put("imagem", R.drawable.negocios);
            }
            item.put("destino", v.getDestino());

            String periodo = dateFormat.format(v.getDataChegada()) + "a" +
                    dateFormat.format(v.getDataSaida());
            item.put("data", periodo);

            double totalGasto = calcularTotalGasto(v.getId());
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = v.getOrcamento() * valorLimite / 100;
            Double[] valores = new Double[]{v.getOrcamento(), alerta, totalGasto};
            item.put("barraProgresso", valores);
            viagens.add(item);
        }

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
            case 0: //editar viagem
                intent = new Intent(this, ViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id );
                startActivity(intent);
                break;

            case 1: //novo gasto
                startActivity( new Intent(this, GastoActivity.class));
                break;

            case 2: //lista de gastos realizados
                startActivity( new Intent(this, GastoListActivity.class));
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(this.viagemSelecionada);
                removerViagem(id);
                getListView().invalidateViews();
                break;
        }
    }

    private void removerViagem(String idS){
        Long id = new Long(idS);

        try {
            DeleteBuilder<Gasto,Long> deleteBuilder = helper.getGastoDAO().deleteBuilder();
            deleteBuilder.where().eq(DatabaseHelper.Gasto.VIAGEM_ID, id);
            deleteBuilder.delete();

            helper.getGastoDAO().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*SQLiteDatabase db = helper.getWritableDatabase();
        String[] where = new String[]{ id };
        db.delete("gasto", "viagem_id = ?", where );
        db.delete("viagem", "_id = ?", where );*/
    }
}
