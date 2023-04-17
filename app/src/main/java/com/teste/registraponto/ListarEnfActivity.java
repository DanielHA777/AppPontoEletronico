package com.teste.registraponto;

import  androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.teste.registraponto.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListarEnfActivity extends AppCompatActivity {

    private ListView listView;
    private DAOEnfermeiro enfermeiroDAO;
    private List<Enfermeiro> enfermeiros;
    private List<Enfermeiro> enfermeirosFiltrados = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_enf);

        listView = findViewById(R.id.lista_enfermeiros);
        enfermeiroDAO = new DAOEnfermeiro(this);
        enfermeiros = enfermeiroDAO.obterTodos();
        enfermeirosFiltrados.addAll(enfermeiros);
        //ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunosFiltrados);
        EnfermeiroAdapter adaptador = new EnfermeiroAdapter(this, enfermeirosFiltrados);
        listView.setAdapter(adaptador);
        registerForContextMenu(listView);

       // SqlitePraExcel(enfermeiros);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_principal, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                procuraAluno(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }

    public void procuraAluno(String nome){
        enfermeirosFiltrados.clear();
        for (Enfermeiro a : enfermeiros) {
            if(a.getNome().toLowerCase().contains(nome.toLowerCase())){
                enfermeirosFiltrados.add(a);
            }
            listView.invalidateViews();
        }
    }



    public void cadastrar(MenuItem item){
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    public void excluir(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Enfermeiro enfermeiroExcluir = enfermeirosFiltrados.get(menuInfo.position);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Excluir o registro?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enfermeirosFiltrados.remove(enfermeiroExcluir);
                        enfermeiros.remove(enfermeiroExcluir);
                        enfermeiroDAO.excluir((enfermeiroExcluir));
                        listView.invalidateViews();
                    }
                }).create();
        dialog.show();
    }

    public void atualizar(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Enfermeiro enfermeiroAtualizar = enfermeirosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("enfermeiro", enfermeiroAtualizar);
        startActivity(it);
    }


    @Override
    public void onResume(){
        super.onResume();
        enfermeiros = enfermeiroDAO.obterTodos();
        enfermeirosFiltrados.clear();
        enfermeirosFiltrados.addAll(enfermeiros);
        listView.invalidateViews();
    }

    public void geraPdf(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

       PdfDocument documentoPDF = new PdfDocument();
        PdfDocument.PageInfo detalhesDaPagina = new PdfDocument.PageInfo.Builder(800, 800, 1).create();
        PdfDocument.Page novaPagina = documentoPDF.startPage(detalhesDaPagina);
        Canvas canvas = novaPagina.getCanvas();

        Paint corDoTexto = new Paint();
        corDoTexto.setColor(Color.BLACK);
        Paint corTitulo = new Paint();
        corTitulo.setColor(Color.RED);

        canvas.save();

        canvas.drawText("CONTROLE DE ENFERMAGEM", 100,80, corTitulo);
        canvas.drawText(Arrays.toString(enfermeiros.toArray() ) +"\n", 50, 120, corDoTexto);


        documentoPDF.finishPage(novaPagina);

        String targetPdf = "/sdcard/Documents/PONTOENFERMAGEM/AT" + ".pdf";
        File filePath = new File(targetPdf);

        try {
            documentoPDF.writeTo(new FileOutputStream(filePath));

            Toast.makeText(getApplicationContext(), "PDF Gerado com sucesso", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Falha ao gerar PDF ...", Toast.LENGTH_SHORT).show();
        }

        documentoPDF.close();
      //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      //  startActivity(intent);

        //SQLiteToExcel sqliteToExcel = new SQLiteToExcel(this, "banco.db");
        //sqliteToExcel.exportAllTables();
      //  String targetPdf = "/sdcard/PONTOENFERMAGEM/AT" + ".pdf";
       // File filePath = new File(targetPdf);
       // SQLiteToExcel sqliteToExcel = new SQLiteToExcel(this, "helloworld.db", filePath.toString() );
    }
    public void SqlitePraExcel(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        String colunas = "\"NOME\t\" \t\"STATUS\t\" \t\"DATA\t\" \t\"HORA\t\"";

       // File root = Environment.getExternalStorageDirectory();
      //  File pasta = new File(root.getAbsolutePath());
      //  pasta.mkdirs();
        String pasta = "/sdcard/Documents/PONTOENFERMAGEM/";
        File arquivoXls = new File(pasta, "Lista3.xls");
        FileOutputStream streamDeSaida = null;

        try {
            streamDeSaida = new FileOutputStream(arquivoXls);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            streamDeSaida.write(colunas.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Enfermeiro enf: enfermeiros){

            String dadosEnfermeiro = "\n" + "\"" + enf.getNome() +"\t\"" + "\t\"" + enf.getStatus() + "\t\"" + "\t\"" + enf.getData() +"\t\"" + "\t\"" + enf.getHora() + "\t\"";

            try {
                streamDeSaida.write(dadosEnfermeiro.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            streamDeSaida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Planilha baixada com sucesso", Toast.LENGTH_SHORT).show();
    }
}
