package br.com.casadocodigo.boaviagem.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

import br.com.casadocodigo.boaviagem.DatabaseHelper;
import br.com.casadocodigo.boaviagem.model.Gasto;

public class GastoDao {
    private DBHelper helper;

    public GastoDao(){}

    public GastoDao(Context context){
        setHelper(context);
    }

    public void setHelper(Context context){
        helper = new DBHelper(context);
    }
    public Double getGastoViagem(Long viagemId){
        Double total = 0.0;
        try {
            Dao<Gasto, Long> dao = helper.getGastoDAO();
            List<Gasto> gastos = dao.queryBuilder()
                    .selectRaw("SUM(valor)")
                    .where()
                    .eq(Gasto.VIAGEM_ID, viagemId)
                    .query();
            total = gastos.get(0).getValor();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public boolean salvar(Gasto g){
        Dao.CreateOrUpdateStatus resultado = null;
        try {
            Dao<Gasto,Long> dao = helper.getGastoDAO();
            resultado = dao.createOrUpdate(g);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(resultado != null && resultado.getNumLinesChanged() > 0){
            return true;
        }else{
            return false;
        }
    }

    public void removerPorViagemId(Long viagemId){
        try {
            DeleteBuilder<Gasto,Long> deleteBuilder = helper.getGastoDAO().deleteBuilder();
            deleteBuilder.where().eq(DatabaseHelper.Gasto.VIAGEM_ID, viagemId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeHelper(){
        if (helper != null) {
            helper.close();
        }
    }
}
