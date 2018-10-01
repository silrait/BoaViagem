package br.com.casadocodigo.boaviagem;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.casadocodigo.boaviagem.dao.ViagemDao;
import br.com.casadocodigo.boaviagem.model.Viagem;

public class ConfiguracoesActivity extends PreferenceActivity {
    private ListPreference listViagem;
    private CheckBoxPreference modoViagemCheckbox;
    private ViagemDao viagemDao;
    private Context self = null;
    private Boolean modoViagem = false;
    private List<Viagem> viagens = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = this;

        viagemDao = new ViagemDao(this);

        addPreferencesFromResource(R.layout.preferencias);

        modoViagemCheckbox = (CheckBoxPreference) findPreference("modo_viagem");
        modoViagemCheckbox.setChecked(false);
        modoViagem = false;

        modoViagemCheckbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(((CheckBoxPreference) preference).isChecked()){
                        if( viagemDao.tabelaEstaVazia() == true){
                            Toast.makeText(self, getString(R.string.sem_viagem), Toast.LENGTH_SHORT).show();
                            ((CheckBoxPreference) preference).setChecked(false);
                        }else{
                            ((ConfiguracoesActivity) self).recuperaViagens();
                        }
                    }
                    return true;
                }
            });
    }

    public void recuperaViagens(){
        ViagemDao viagemDao = new ViagemDao(this);
        ArrayList<String> entries = new ArrayList<String>();
        ArrayList<String> entryValues = new ArrayList<String>();

        List<Viagem> viagens = viagemDao.buscaTodos();
        if( viagens != null){
            for (Viagem v : viagens){
                entries.add( v.getDestino() );
                entryValues.add( v.getId().toString() );
            }
        }

        if( !entries.isEmpty() ) {
            listViagem.setEntries( entries.toArray( new String[0]));
            listViagem.setEntryValues( entryValues.toArray(new String[0]) );
        }

        viagemDao.closeHelper();
    }
}
