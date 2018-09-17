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
import kr.co.z_eong.textviewer.sub.Folder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileListAdapter extends BaseAdapter{
    ArrayList<Folder> filesNameList;
    DBmanager dBmanager;

    @Override
    public int getCount() {
        return filesNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return filesNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_mold, parent, false);
            holder.mold_text = convertView.findViewById(R.id.mold_text);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }

        Folder item = (Folder)getItem(position);

        int mainColor = dBmanager.seleteBackground();
        holder.mold_text.setTextColor(Color.parseColor(textColor(mainColor)));

        holder.mold_text.setText(item.getName());

        return convertView;
    }

    public class Holder{
        TextView mold_text;
    }

    public String textColor(int color){
        if(color == 0){
            return "#000000";
        }else{
            return "#ffffff";
        }
    }
}
