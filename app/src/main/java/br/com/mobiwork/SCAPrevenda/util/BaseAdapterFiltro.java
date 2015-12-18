package br.com.mobiwork.SCAPrevenda.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;

/**
 * Created by LuisGustavo on 04/06/14.
 */
public class BaseAdapterFiltro extends BaseAdapter {

    private Context context;
    private Cursor c,c2;
    private SQLiteDatabase db;
    DateFormat formatter;
    Date d ;
    private int _id;
    private String _id2;
    ArrayList<Integer> p= new ArrayList<Integer>();
    ArrayList<String> p2= new ArrayList<String>();
    String sit,filtro;
    ListView le ;
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

    public BaseAdapterFiltro(Context context,  List<HashMap<String, String>> fillMaps2,ListView l,String filtror){
        this.context = context;
        //this.c = rotas;
        this.fillMaps=fillMaps2;
        DaoCreateDB dao = new DaoCreateDB(context);
        db =  dao.getWritableDatabase();
        formatter= new SimpleDateFormat("dd/MM/yyyy");
        d = new Date();
        le=l;
        this.filtro=filtror;

    }




    @Override
    public int getCount() {
        return fillMaps.size();
    }

    @Override
    public Object getItem(int position) {
        if(filtro.equalsIgnoreCase("restaure")){
            return position;
        }else{
            return this.p.get(position);

        }
    }


    @Override
    public long getItemId(int position) {
        if(filtro.equalsIgnoreCase("restaure")){
            return position;
        }else{
            return this.p.get(position);

        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        View layout;
        Activity aa = (Activity)context;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        else{
            convertView = convertView;
        }

        boolean r=false;

        if(filtro.equalsIgnoreCase("restaure")){
            if(p.isEmpty()){
                this._id2=fillMaps.get(position).get("_id");
                this.p2.add(position,_id2);

            }else{
                if(position==p.size()){
                    this._id2=fillMaps.get(position).get("_id");
                    this.p2.add(position,_id2);
                    ;
                }
            }

        }
        TextView modelo = (TextView) convertView.findViewById(R.id.firstName);
        modelo.setText(fillMaps.get(position).get("_id"));
        TextView end = (TextView) convertView.findViewById(R.id.title);
        end.setText(fillMaps.get(position).get("hora"));


        return convertView;
    }
}
