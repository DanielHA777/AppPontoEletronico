package com.teste.registraponto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText txtEnf;
    private Button btnSalvar;
    private DAOEnfermeiro dao;
    private Switch SWstatus;
    private Enfermeiro enfermeiro = null;
    private TextView txtData;
    private TextView txtHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEnf = findViewById(R.id.txtEnf);
        btnSalvar = findViewById(R.id.btnSalvar);
        SWstatus = findViewById(R.id.SWstatus);
        dao =  new DAOEnfermeiro(this);
        txtData = findViewById(R.id.txtData);
        txtHora = findViewById(R.id.txtHora);
        txtData.setText(dataAtual());
        txtHora.setText(horaAtual());

        dataAtual();
        dataPdf();
        horaAtual();

        Intent it = getIntent();
        if (it.hasExtra("enfermeiro")){
            enfermeiro = (Enfermeiro) it.getSerializableExtra("enfermeiro");
            txtEnf.setText(enfermeiro.getNome());
            SWstatus.setText(enfermeiro.getStatus());
           txtData.setText(enfermeiro.getData());
           txtHora.setText(enfermeiro.getHora());
        }

        final LinearLayout mContent = (LinearLayout) findViewById(R.id.linearLayout);
        final CaptureSignatureView mSig = new CaptureSignatureView(this, null);
        mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEnf.getText().length() < 2 || mSig.getBytes().length <= 3000) {
                    Toast.makeText(getApplicationContext(), "Os Campos Nome e Assinatura são obrigatórios", Toast.LENGTH_SHORT).show();
                } else {
                    Enfermeiro enf = new Enfermeiro();
                    enf.setNome(txtEnf.getText().toString());
                    if (SWstatus.isChecked()) {
                        enf.setStatus("Entrada");
                    } else {
                        enf.setStatus("Saída");
                    }
                    String date = dataAtual();
                    enf.setData(date.toString());
                    String hora = horaAtual();
                    enf.setHora(hora.toString());
                    Long id = dao.inserirEnf(enf);
                    Toast.makeText(getApplicationContext(), "Salvo com sucesso " + id, Toast.LENGTH_SHORT).show();


                    String data = dataPdf().toString();

                    PdfDocument documentoPDF = new PdfDocument();
                    PdfDocument.PageInfo detalhesDaPagina = new PdfDocument.PageInfo.Builder(600, 400, 1).create();
                    PdfDocument.Page novaPagina = documentoPDF.startPage(detalhesDaPagina);
                    Canvas canvas = novaPagina.getCanvas();

                    Paint corDoTexto = new Paint();
                    corDoTexto.setColor(Color.BLACK);
                    Paint corTitulo = new Paint();
                    corTitulo.setColor(Color.RED);

                    canvas.save();

                    canvas.drawText("CONTROLE DE ENTRADA ENFERMAGEM", 100, 80, corTitulo);
                    canvas.drawText("Nome: " + txtEnf.getText().toString() + "          Data: " + txtData.getText(), 50, 120, corDoTexto);

                    if (SWstatus.isChecked()) {
                        canvas.drawText("Entrada.", 50, 140, corTitulo);
                    }else {
                        canvas.drawText("Saída.", 50, 140, corTitulo);
                    }

                    if (data.equals(data)) {
                        data = data.replaceAll("/", ".");

                        byte[] signature = mSig.getBytes();

                        try {
                            FileOutputStream fos = new FileOutputStream("/sdcard/Documents/PONTOENFERMAGEM/Assi" + txtEnf.getText() + ".jpg");
                            fos.write(signature);
                            FileDescriptor fd = fos.getFD(); //Retorna o descritor de arquivo associado a este fluxo.
                            fos.flush();
                            fd.sync();
                            fos.close();

                            Bitmap bpm, scaledBtmap;
                            Paint cor = new Paint();

                            bpm = BitmapFactory.decodeFile("/sdcard/Documents/PONTOENFERMAGEM/Assi" + txtEnf.getText() + ".jpg");
                            scaledBtmap = Bitmap.createScaledBitmap(bpm, 270, 200, false);
                            canvas.drawBitmap(scaledBtmap, 50, 170, cor);

                        } catch (Exception e) {
                            String erro = e.toString();
                        }

                        documentoPDF.finishPage(novaPagina);

                        String targetPdf = "/sdcard/Documents/PONTOENFERMAGEM/AT." + txtEnf.getText() + data + ".pdf";
                        File filePath = new File(targetPdf);

                        try {
                            documentoPDF.writeTo(new FileOutputStream(filePath));

                            Toast.makeText(getApplicationContext(), "PDF Gerado com sucesso", Toast.LENGTH_SHORT).show();

                            txtEnf.setText(null);
                            mSig.ClearCanvas();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Falha ao gerar PDF ...", Toast.LENGTH_SHORT).show();
                        }

                        documentoPDF.close();

                    }

                }

            }

        });
    }

    public void salvar(View view) {
        Enfermeiro enf = new Enfermeiro();
        enf.setNome(txtEnf.getText().toString());
        if (SWstatus.isChecked()) {
            enf.setStatus("Entrada");
        } else {
            enf.setStatus("Saída");
        }
        String date = dataAtual();
        enf.setData(date.toString());
        String hora = horaAtual();
        enf.setHora(hora.toString());
        Long id = dao.inserirEnf(enf);
        Toast.makeText(this, "Salvo com sucesso " + id, Toast.LENGTH_SHORT).show();

    }
    public static String dataAtual() {

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }
    public static String dataPdf() {
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String horaAtual() {

        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
