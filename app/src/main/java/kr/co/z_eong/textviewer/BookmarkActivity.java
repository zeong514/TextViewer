package kr.co.z_eong.textviewer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.z_eong.textviewer.adapter.BookmarkAdapter;
import kr.co.z_eong.textviewer.db.DBmanager;
import kr.co.z_eong.textviewer.sub.Bookmark;

public class BookmarkActivity extends AppCompatActivity {
    public static int WHITE = 0;
    public static int BLACK = 1;
    public static int NOW = 2;
    public static int ALL = 3;
    @BindView(R.id.bookmark_main_back)LinearLayout bookmark_main_back;
    @BindView(R.id.nowbookmark)CheckBox nowbookmark;
    @BindView(R.id.allbookmark)CheckBox allbookmark;
    @BindView(R.id.bookmark_list)ListView bookmark_list;
    @BindView(R.id.add_bookmark)Button add_bookmark;
    @BindView(R.id.esc_bookmark)Button esc_bookmark;
    @BindView(R.id.bookmark_main)LinearLayout bookmark_main;
    @BindView(R.id.bookmark_title_edit)EditText bookmark_title_edit;
    @BindView(R.id.bookmark_add)Button bookmark_add;
    @BindView(R.id.bookmark_cancel)Button bookmark_cancel;
    @BindView(R.id.addbookmark)TextView addbookmark;

    int backgroundColor = WHITE;

    ArrayList<Bookmark> bookmarks = new ArrayList<>();
    BookmarkAdapter bookmarkAdapter;
    DBmanager dBmanager;
    int choice = NOW;
    String root = "";
    Integer page = 0;
    String title = "";
    String name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        ButterKnife.bind(this);
        dBmanager = new DBmanager(BookmarkActivity.this,"background.db",null,1);
        backgroundColor = dBmanager.seleteBackground();
        colorSelect(backgroundColor);

        Intent intent1 = getIntent();
        root = intent1.getStringExtra("root");
        page = intent1.getIntExtra("page",0);
        int test = intent1.getIntExtra("page",0);
        title = intent1.getStringExtra("title");
        name = intent1.getStringExtra("name");
        bookmark_title_edit.setText(title);


        bookmarks = dBmanager.seleteBookmarkRoot(name);
        bookmarkAdapter = new BookmarkAdapter(bookmarks,dBmanager);
        bookmark_list.setAdapter(bookmarkAdapter);

        nowbookmark.setChecked(true);


        nowbookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allbookmark.setChecked(false);
                nowbookmark.setChecked(true);
                bookmarks = dBmanager.seleteBookmarkRoot(name);
                bookmarkAdapter = new BookmarkAdapter(bookmarks,dBmanager);
                bookmark_list.setAdapter(bookmarkAdapter);
                choice = NOW;
            }
        });

        allbookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowbookmark.setChecked(false);
                allbookmark.setChecked(true);
                bookmarks = dBmanager.seleteBookmark();
                bookmarkAdapter = new BookmarkAdapter(bookmarks, dBmanager);
                bookmark_list.setAdapter(bookmarkAdapter);
                choice = ALL;
            }
        });




        add_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmark_main.getVisibility() == View.GONE){
                    bookmark_main.setVisibility(View.VISIBLE);
                }
            }
        });

        bookmark_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tit = bookmark_title_edit.getText().toString();
                dBmanager.insertBookmark(root,page,tit,name);
                if(choice == NOW){
                    bookmarks = dBmanager.seleteBookmarkRoot(name);
                    bookmarkAdapter = new BookmarkAdapter(bookmarks,dBmanager);
                    bookmark_list.setAdapter(bookmarkAdapter);
                }else if(choice == ALL) {
                    bookmarks = dBmanager.seleteBookmark();
                    bookmarkAdapter = new BookmarkAdapter(bookmarks, dBmanager);
                    bookmark_list.setAdapter(bookmarkAdapter);
                }
                bookmark_main.setVisibility(View.GONE);
            }
        });

        bookmark_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmark_main.setVisibility(View.GONE);
            }
        });

        esc_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bookmark_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bookmark item = bookmarks.get(position);
                Intent intent = getIntent();
                intent.putExtra("root",item.getRoot());
                intent.putExtra("page",item.getPage());
                intent.putExtra("name",item.getName());
                intent.putExtra("id",item.getId());
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    public void colorSelect(int color){
        if(color == WHITE){
            bookmark_main_back.setBackgroundResource(R.color.white);
            nowbookmark.setTextColor(getColorStateList(R.color.black));
            allbookmark.setTextColor(getColorStateList(R.color.black));
            add_bookmark.setBackgroundResource(R.color.white);
            add_bookmark.setTextColor(getColorStateList(R.color.black));
            esc_bookmark.setBackgroundResource(R.color.white);
            esc_bookmark.setTextColor(getColorStateList(R.color.black));
            bookmark_main.setBackgroundResource(R.color.tranWhite);
            addbookmark.setTextColor(getColorStateList(R.color.black));
            bookmark_title_edit.setTextColor(getColorStateList(R.color.black));
            bookmark_add.setBackgroundResource(R.color.tranWhite);
            bookmark_add.setTextColor(getColorStateList(R.color.black));
            bookmark_cancel.setBackgroundResource(R.color.tranWhite);
            bookmark_cancel.setTextColor(getColorStateList(R.color.black));

        }else if(color ==BLACK){
            bookmark_main_back.setBackgroundResource(R.color.black);
            nowbookmark.setTextColor(getColorStateList(R.color.white));
            allbookmark.setTextColor(getColorStateList(R.color.white));
            add_bookmark.setBackgroundResource(R.color.black);
            add_bookmark.setTextColor(getColorStateList(R.color.white));
            esc_bookmark.setBackgroundResource(R.color.black);
            esc_bookmark.setTextColor(getColorStateList(R.color.white));
            bookmark_main.setBackgroundResource(R.color.tranBlack);
            addbookmark.setTextColor(getColorStateList(R.color.white));
            bookmark_title_edit.setTextColor(getColorStateList(R.color.white));
            bookmark_add.setBackgroundResource(R.color.tranBlack);
            bookmark_add.setTextColor(getColorStateList(R.color.white));
            bookmark_cancel.setBackgroundResource(R.color.tranBlack);
            bookmark_cancel.setTextColor(getColorStateList(R.color.white));

        }
    }
}
