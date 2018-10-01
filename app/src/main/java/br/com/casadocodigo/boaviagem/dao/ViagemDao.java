package br.com.casadocodigo.boaviagem.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import br.com.casadocodigo.boaviagem.model.Viagem;

public class ViagemDao {
    private DBHelper helper;

    public ViagemDao(){}

    public ViagemDao(Context context){
        setHelper(context);
    }

    public void setHelper(Context context){
        helper = new DBHelper(context);
    }

    public boolean remover(Viagem v){
        boolean retorno = false;

        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();
            if ( dao.delete(v) == 1 )
                retorno = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public Viagem buscaPorId(Long id){
        Viagem v = null;
        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();
            v = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

    public Boolean tabelaEstaVazia(){
        Boolean retorno = true;
        try {
            if( helper.getViagemDAO().queryBuilder().limit(1l).queryForFirst() != null){
                retorno = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retorno;
    }


    public List<Viagem> buscaTodos(){
        List<Viagem> lista = null;
        try {
            lista = helper.getViagemDAO().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void removerPorId(Long id){
        try {
            helper.getViagemDAO().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean salvar(Viagem v){
        Dao.CreateOrUpdateStatus resultado = null;
        try {
            Dao<Viagem,Long> dao = helper.getViagemDAO();
            resultado = dao.createOrUpdate(v);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(resultado != null && resultado.getNumLinesChanged() > 0){
            return true;
        }else{
            return false;
        }
    }

    public void closeHelper(){
        if (helper != null) {
            helper.close();
        }
    }
}
