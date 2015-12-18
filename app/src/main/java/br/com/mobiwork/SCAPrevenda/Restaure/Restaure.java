package br.com.mobiwork.SCAPrevenda.Restaure;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.EditText;

        import br.com.mobiwork.SCAPrevenda.R;


public class Restaure extends Activity implements  View.OnClickListener {

    private EditText edTxSenha;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.infosenha);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            this.setFinishOnTouchOutside(false);
        }


        // senha
        edTxSenha = (EditText) findViewById(R.id.senha);

        View.OnTouchListener otl = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                return true;
            }
        };
        edTxSenha.setOnTouchListener(otl);
        configBt();

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public void onClick(View v) {
        String keyInfo = (String)v.getTag();

        if (keyInfo.equals("back"))
            isBack();
        else if (keyInfo.equals("limpa"))
            edTxSenha.setText("");
        else if (keyInfo.equals("cancel")){
            Intent result = new Intent(this, Restaure.class);
            setResult(RESULT_CANCELED, result);
            Restaure.this.finish();
        }
        else if (keyInfo.equals("done")){
            Intent result = new Intent(this, Restaure.class);
            result.putExtra("senhaRestaure",edTxSenha.getText().toString());
            setResult(RESULT_OK, result);
            Restaure.this.finish();
        }
        else if (!keyInfo.equals("")){
            edTxSenha.append(keyInfo);
        }

    }

    private void isBack() {
        CharSequence cc = edTxSenha.getText();
        if (cc != null && cc.length() > 0)
        {
            edTxSenha.setText("");
            edTxSenha.append(cc.subSequence(0, cc.length() - 1));
        }
    }

    public void configBt(){
        Button bt = (Button) findViewById(R.id.x1);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x2);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x3);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x4);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x5);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x6);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x7);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x8);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x9);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x0);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xLimpa);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xDone);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xCancel);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xPonto);
        bt.setOnClickListener(this);

    }

}