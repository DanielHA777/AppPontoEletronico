package com.teste.registraponto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

// view bloco de construção básico para os componentes de interface do usuário
public class CaptureSignatureView extends View {
    // é uma classe que trabalha com imagens cujo formato representa um mapa de bits
    // um mapa de bits é uma matriz de bytes que especifica a cor de cada pixel numa matriz (tabela)
    private Bitmap _Bitmap;
    //A classe Canvas define métodos para desenhar texto, linhas, bitmaps e muitos outros elementos gráficos primitivos
    private Canvas _Canvas;
    //A classe Path encapsula caminhos geométricos compostos (contornos múltiplos) que consistem em segmentos de linha reta, curvas quadráticas e curvas cúbicas.
    private Path _Path;
    // A classe Paint contém informações de estilo e cor sobre como desenhar geometrias, texto e bitmaps.
    private Paint _BitmapPaint;
    private Paint _paint;
    //As variáveis do tipo float representam números de ponto flutuante de precisão simples e podem representar até 7 dígitos
    private float _mX;
    private float _mY;
    // tolerancia ao toque
    private float TouchTolerance = 4;
    //expessura da linha
    private float LineThickness = 4;

   // Context é a ponte entre os componentes android. É usado para fazer a comunicação entre componentes, instanciá-los e acessá-los
  //AttributSet Uma coleção de atributos, conforme encontrados associados a uma tag em um documento XML.
    public CaptureSignatureView(Context context, AttributeSet attr) {
        super(context, attr);
        _Path = new Path();
        //Pinte a bandeira que ativa o pontilhamento durante o blitting.
        _BitmapPaint = new Paint(Paint.DITHER_FLAG);
        _paint = new Paint();
        //suaviza as bordas do que está sendo desenhado, mas não tem impacto no interior da forma
        _paint.setAntiAlias(true);
        //O pontilhamento afeta como as cores com maior precisão do que o dispositivo são amostradas
        _paint.setDither(true);
        //Defina a cor
        _paint.setColor(Color.argb(255, 0, 0, 0));
        //Defina o estilo da pintura
        _paint.setStyle(Paint.Style.STROKE);
        //Defina a junção da tinta.
        _paint.setStrokeJoin(Paint.Join.ROUND);
        //Defina a tampa da tinta.
        _paint.setStrokeCap(Paint.Cap.ROUND);
        //Defina a largura para acariciar.
        _paint.setStrokeWidth(LineThickness);
    }

    @Override //Chamado quando o tamanho desta visualização é alterado.
   // w	int: Largura atual desta vista. h	int: Altura atual desta vista. oldw	int: Largura antiga desta vista. oldh	int: Antiga altura desta vista.
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Create Retorna um bitmap do subconjunto especificado do bitmap de origem. O novo bitmap pode ser o mesmo objeto da origem ou uma cópia pode ter sido feita. Ele é inicializado com a mesma densidade e espaço de cores do bitmap original.
      // config  Possíveis configurações de bitmap. Uma configuração de bitmap descreve como os pixels são armazenados. Isso afeta a qualidade (profundidade da cor), bem como a capacidade de exibir cores transparentes / translúcidas.
        _Bitmap = Bitmap.createBitmap(w, (h > 0 ? h : ((View) this.getParent()).getHeight()), Bitmap.Config.ARGB_8888);
        _Canvas = new Canvas(_Bitmap);
    }

    @Override //Chamado quando a visualização deve renderizar seu conteúdo.  onDraw() para criar sua interface do usuário (IU) personalizada
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Preenchatodo o bitmap da tela com a cor e o modo de mesclagem especificados
        canvas.drawColor(Color.WHITE);
        //Desenhe o bitmap usando a matriz especificada
        canvas.drawBitmap(_Bitmap, 0, 0, _BitmapPaint);
        //esenhe o caminho especificado usando a tinta especificada.
        canvas.drawPath(_Path, _paint);
    }

    private void TouchStart(float x, float y) { //  indicando que um novo toque na superfície tenha ocorrido
        _Path.reset(); //Limpe todas as linhas e curvas do caminho, tornando-o vazio.
        _Path.moveTo(x, y); //Defina o início do próximo contorno no ponto (x, y)
        _mX = x;
        _mY = y;
    }

    private void TouchMove(float x, float y) { //Cada vez que um ou mais dedos se movem, um evento de TouchMove é disparado
        float dx = Math.abs(x - _mX);
        float dy = Math.abs(y - _mY);

        if (dx >= TouchTolerance || dy >= TouchTolerance) {
            _Path.quadTo(_mX, _mY, (x + _mX) / 2, (y + _mY) / 2);
            _mX = x;
            _mY = y;
        }
    }

    private void TouchUp() { // retocar
        if (!_Path.isEmpty()) { // se n for vazio
            _Path.lineTo(_mX, _mY); // Adicione uma linha do último ponto ao ponto especificado (x, y).
            _Canvas.drawPath(_Path, _paint);
        } else {
            _Canvas.drawPoint(_mX, _mY, _paint); //para desenhar um único ponto.
        }

        _Path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) { //Chamado quando ocorre um evento de movimento da tela de toque.
        super.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: // ação para baixo
                TouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE: // ação mover
                TouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP: //ação para cima
                TouchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void ClearCanvas() { // limpando desenho
        _Canvas.drawColor(Color.WHITE);
        invalidate();
    }

    @SuppressLint("WrongThread") // pegando array de bytes e transformando em png
    public byte[] getBytes() {
        Bitmap b = getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public Bitmap getBitmap() { // pegando o mapa de bits para capturrae imagem
        View v = (View) this.getParent();
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        return b;
    }
}