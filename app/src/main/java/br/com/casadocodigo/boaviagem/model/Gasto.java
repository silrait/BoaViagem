package br.com.casadocodigo.boaviagem.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "gasto")
public class Gasto {
    public static final String TABELA = "gasto";
    public static final String _ID = "_id";
    public static final String VIAGEM_ID = "viagem_id";
    public static final String CATEGORIA = "categoria";
    public static final String DATA = "data";
    public static final String DESCRICAO = "descricao";
    public static final String VALOR = "valor";
    public static final String LOCAL = "local";

    public static final String[] COLUNAS = new String[]{
            _ID, VIAGEM_ID, CATEGORIA, DATA, DESCRICAO, VALOR, LOCAL
    };

    @DatabaseField(columnName = "_id", generatedId = true)
    private Long id;

    @DatabaseField(columnName = "data")
    private Date data;

    @DatabaseField(columnName = "categoria")
    private String categoria;

    @DatabaseField(columnName = "descricao")
    private String descricao;

    @DatabaseField(columnName = "valor")
    private Double valor;

    @DatabaseField(columnName = "local")
    private String local;

    @DatabaseField(columnName = "viagem_id", foreign = true, foreignAutoRefresh = true)
    private Viagem viagem;

    public Gasto() {}

    public Gasto(Long id, Date data, String categoria, String descricao, Double valor, String local, Viagem viagem) {
        this.id = id;
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.local = local;
        this.viagem = viagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }
}
