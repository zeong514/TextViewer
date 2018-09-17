package kr.co.z_eong.textviewer.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.z_eong.textviewer.R;
import kr.co.z_eong.textviewer.db.DBmanager;
import kr.co.z_eong.textviewer.sub.Bookmark;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookmarkAdapter extends BaseAdapter{
    ArrayList<Bookmark> bookmarks;
    DBmanager dBmanager;
    @Override
    public int getCount() {
        return bookmarks.size();
    }

    @Override
    public Object getItem(int position) {
        return bookmarks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_mold, parent, false);
            holder.book_mold_title = convertView.findViewById(R.id.book_mold_title);
            holder.book_mold_delete = convertView.findViewById(R.id.book_mold_delete);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }

        final Bookmark item = (Bookmark)getItem(position);

        int mainColor = dBmanager.seleteBackground();
        holder.book_mold_title.setTextColor(Color.parseColor(textColor(mainColor)));

        holder.book_mold_title.setText(item.getTitle());
        holder.book_mold_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dBmanager.deleteBookmark(item.getId());
                bookmarks.remove(item);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public class Holder{
        TextView book_mold_title;
        ImageView book_mold_delete;

    }

    public String textColor(int color){
        if(color == 0){
            return "#000000";
        }else{
            return "#ffffff";
        }
    }
}
