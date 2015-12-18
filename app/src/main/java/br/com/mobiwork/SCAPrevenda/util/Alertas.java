package br.com.mobiwork.SCAPrevenda.util;

/**
 * Created by LuisGustavo on 08/08/14.
 */
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste;
import br.com.mobiwork.SCAPrevenda.sinc.SincDown;

/**
 * Created by LuisGustavo on 10/06/14.
 */
public class Alertas {

    private Context ctx;
    protected static final String ZXING_MARKET = "market://search?q=pname:cn.menue.barcodescanner";
    protected static final String ZXING_DIRECT = "https://zxing.googlecode.com/files/BarcodeScanner3.1.apk";

    public Alertas(Context context){
        ctx=context;
    }

    public void Alerta( String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }
    public double[] AlertaYN(String mensagem, String texto){
        final double[] resp = new double[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(texto);
        builder.setTitle(mensagem);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resp[0] = 1;
                    }
                });

        builder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resp[0] =0;
                    }
                });

        AlertDialog d = builder.create();
        d.show();


        return resp;
    }
    public void AlertaSinc(String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }
    public void msg(String titulo,String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setTitle(titulo);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }

    public int AlertaEsc(String [] titulo,String mensagem,String param){
        final int[] arrayOfInt = { 0 };
        String[] arrayOfString = new String[15];

        for(int i=0;i<titulo.length;i++){
            arrayOfString[i]=titulo[i];
        }
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(ctx);
        localBuilder.setTitle(mensagem).setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
                arrayOfInt[0] = paramAnonymous2Int;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
                paramAnonymous2DialogInterface.cancel();


            }
        });
        localBuilder.create().show();
        return arrayOfInt[0];
    }
    public void baixarBarcode() {
        new AlertDialog.Builder(ctx)
                .setTitle("Instalar o scanner codigo de barras?")
                .setMessage("Deseja baixar o aplicativo para o escaneamento?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Instalar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_MARKET));
                                try {
                                    ctx.startActivity(intent);
                                } catch (ActivityNotFoundException e) { // Se não tiver o Play Store
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_DIRECT));
                                    ctx.startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton("Cancelar", null).show();

    }

    public int alertaExcluirPedido(String msg) {
        final int[] resp = {0};
        new AlertDialog.Builder(ctx)
                .setTitle(msg)
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            resp[0] = i;

                        }
                    }
                }).show();
        return resp[0];
    }


    public void CadSmart( final Context  ctx){
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.id_smart, null);
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(ctx);
        final Alertas a = new Alertas(ctx) ;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ctx);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText edtxsmart = (EditText) promptsView
                .findViewById(R.id.edtxsmart);
        edtxsmart.setText(String.valueOf(sdu.getIdSmart()));
        final EditText edtxsenha=(EditText)promptsView.findViewById(R.id.edtxsenha);

        final Button altidsmartc=(Button)promptsView.findViewById(R.id.altidsmartc);

        altidsmartc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date data = new Date();
                SimpleDateFormat fmtdata= new SimpleDateFormat("ddMMyyyy");
                String senhdata=fmtdata.format(data);
                if(!edtxsenha.getText().toString().equalsIgnoreCase("")){
                   if(edtxsenha.getText().toString().equalsIgnoreCase(senhdata)){
                       edtxsmart.setEnabled(true);
                   }else{
                       AlertaSinc("Senha incorreta");
                   }
                }else{
                    AlertaSinc("Preencha o campo de se senha !");
                }

            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                try {
                                    SharedPreferencesUtil sdu = new SharedPreferencesUtil(ctx);
                                    sdu.setIdSmart(Integer.parseInt(edtxsmart.getText().toString()));
                                } catch (Exception e) {
                                    a.AlertaSinc("Caminho invalido");
                                }
                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();
    }

}
