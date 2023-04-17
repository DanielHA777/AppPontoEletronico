package com.teste.registraponto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DAOEnfermeiro {
    private Conexao conexao;
    private SQLiteDatabase banco;

    public DAOEnfermeiro(Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public Long inserirEnf(Enfermeiro enfermeiro){
        ContentValues values = new ContentValues();
        values.put("nome", enfermeiro.getNome());
        values.put("status", enfermeiro.getStatus());
        values.put("data", enfermeiro.getData());
        values.put("hora", enfermeiro.getHora());

      return  banco.insert("enfermeiro", null, values);
    }

    public List<Enfermeiro> obterTodos(){

        List<Enfermeiro> enfermeiros = new ArrayList<>();
        Cursor cursor = banco.query("enfermeiro", new String[]{"id", "nome","status", "data", "hora"},
                null, null, null, null, null);
        while (cursor.moveToNext()){
            Enfermeiro a = new Enfermeiro();
            a.setId(cursor.getInt(0));
            a.setNome(cursor.getString(1));
            a.setStatus(cursor.getString(2));
            a.setData(cursor.getString(3));
            a.setHora(cursor.getString(4));
            enfermeiros.add(a);
        }
        return enfermeiros;
    }

    public void excluir(Enfermeiro enfermeiro){
        banco.delete("enfermeiro", "id = ?", new String[]{enfermeiro.getId()+""} );
    }

    public void atualizar(Enfermeiro enfermeiro){
        ContentValues values = new ContentValues();
        values.put("nome", enfermeiro.getNome());
        values.put("status", enfermeiro.getStatus());
        values.put("data", enfermeiro.getData());
        values.put("hora", enfermeiro.getHora());
        banco.update("enfermeiro", values, "id = ?", new String[]{enfermeiro.getId()+""});
    }
}
