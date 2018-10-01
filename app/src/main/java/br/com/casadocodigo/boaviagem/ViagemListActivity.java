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
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.casadocodigo.boaviagem.dao.DBHelper;
import br.com.casadocodigo.boaviagem.dao.GastoDao;
import br.com.casadocodigo.boaviagem.dao.ViagemDao;
import br.com.casadocodigo.boaviagem.model.Gasto;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class ViagemListActivity extends ListActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    public static final Map<Integer,Class<?>> viagemActions;
    static{
        Map<Integer,Class<?>> map = new HashMap<Integer, Class<?>>();
        //editar viagem
        map.put(0, ViagemActivity.class);
        //novo gasto
        map.put(1, GastoActivity.class);
        //lista de gastos realizados
        map.put(2, GastoListActivity.class);

        viagemActions = Collections.unmodifiableMap(map);
    }


    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;

    private ViagemDao viagemDao;
    private GastoDao gastoDao;
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

        //TODO: bom candidato A factory
        viagemDao = new ViagemDao(this);
        gastoDao = new GastoDao(this);

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
        viagemDao.closeHelper();
        gastoDao.closeHelper();
        super.onDestroy();
    }

    protected double calcularTotalGasto(Long id){
        return gastoDao.getGastoViagem(id);
    }

    protected List<Map<String, Object>>  listarViagens(){

        List<Viagem> viagemList = viagemDao.buscaTodos();

        if(viagemList == null)
            return null;

        viagens = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < viagemList.size(); i++){
            Viagem v = viagemList.get(i);
            Map<String,Object> item = new HashMap<String, Object>();

            item.put("id", v.getId() );

            if(v.getTipoViagem() == Constantes.VIAGEM_LAZER){
                item.put("imagem", R.drawable.lazer);
            }else{
                item.put("imagem", R.drawable.negocios);
            }
            item.put("destino", v.getDestino());

            String periodo = dateFormat.format(v.getDataChegada()) + " a " +
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
        String id = (String) viagens.get(viagemSelecionada).get("id").toString();
        Integer acao = Integer.valueOf(item);

        Class<?> cls = viagemActions.get(acao);
        if (cls != null) {
            intent = new Intent(this, cls);
            intent.putExtra(Constantes.VIAGEM_ID, id );
            startActivity(intent);
        }else{
            if(acao == 3){
                viagens.remove(this.viagemSelecionada);
                removerViagem(Long.getLong(id));
            }
        }
    }

    private void removerViagem(Long id){
        gastoDao.removerPorViagemId(id);
        viagemDao.removerPorId(id);

        Toast.makeText(this, getString(R.string.viagem_removida), Toast.LENGTH_SHORT).show();
        getListView().invalidateViews();
    }
}
