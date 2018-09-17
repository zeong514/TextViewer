package kr.co.z_eong.textviewer.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import kr.co.z_eong.textviewer.R;
import kr.co.z_eong.textviewer.db.DBmanager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TextPrintAdapter extends BaseAdapter{
    ArrayList<String> tx;
    DBmanager dBmanager;

    @Override
    public int getCount() {
        return tx.size();
    }

    @Override
    public Object getItem(int position) {
        return tx.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.txt_mold, parent, false);
            holder.txt_mold_text = convertView.findViewById(R.id.txt_mold_text);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }

        String item = (String)getItem(position);

        int mainColor = dBmanager.seleteBackground();
        holder.txt_mold_text.setTextColor(Color.parseColor(textColor(mainColor)));
        holder.txt_mold_text.setTextSize(dBmanager.seleteTextsize());
        holder.txt_mold_text.setText(item);

        return convertView;
    }

    public class Holder{
        TextView txt_mold_text;
    }

    public String textColor(int color){
        if(color == 0){
            return "#000000";
        }else{
            return "#ffffff";
        }
    }
}
