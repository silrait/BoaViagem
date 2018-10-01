package br.com.casadocodigo.boaviagem.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.com.casadocodigo.boaviagem.model.Gasto;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "BoaViagem.db";
    private static final int VERSION = 2;

    private static DBHelper helper = null;
    private Dao<Viagem, Long> viagemDAO = null;
    private Dao<Gasto, Long> gastoDAO = null;

    public DBHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Viagem.class);
            TableUtils.createTable(connectionSource, Gasto.class);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Gasto.class, true);
            TableUtils.dropTable(connectionSource, Viagem.class, true);
            onCreate(database, connectionSource);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close(){
        viagemDAO = null;
        gastoDAO = null;

        super.close();
    }

    public Dao<Viagem, Long> getViagemDAO() throws SQLException{
        if (viagemDAO == null) {
            viagemDAO = getDao(Viagem.class);
        }
        return viagemDAO;
    }

    public Dao<Gasto, Long> getGastoDAO() throws SQLException{
        if (gastoDAO == null) {
            gastoDAO = getDao(Gasto.class);
        }
        return gastoDAO;
    }
}
