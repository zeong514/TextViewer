package kr.co.z_eong.textviewer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.z_eong.textviewer.adapter.TextPrintAdapter;
import kr.co.z_eong.textviewer.db.DBmanager;
import kr.co.z_eong.textviewer.sub.Position;

public class TxtActivity extends AppCompatActivity {
    public static int WHITE = 0;
    public static int BLACK = 1;
    @BindView(R.id.txt_name)TextView txt_name;
    @BindView(R.id.txt_menu)Button txt_menu;
    @BindView(R.id.txt_list)ListView txt_list;
    @BindView(R.id.txt_main_menu)LinearLayout txt_main_menu;
    @BindView(R.id.open_file_btn)RelativeLayout open_file_btn;
    @BindView(R.id.back_change_btn)RelativeLayout back_change_btn;
    @BindView(R.id.text_change_btn)RelativeLayout text_change_btn;
    @BindView(R.id.bookmark_btn)RelativeLayout bookmark_btn;
    @BindView(R.id.search_btn)RelativeLayout search_btn;
    @BindView(R.id.page_move_btn)RelativeLayout page_move_btn;
    @BindView(R.id.background_image)ImageView background_image;
    @BindView(R.id.basic_background)RelativeLayout basic_background;
    @BindView(R.id.text_open_file)TextView text_open_file;
    @BindView(R.id.text_back_change)TextView text_back_change;
    @BindView(R.id.text_text_change)TextView text_text_change;
    @BindView(R.id.text_bookmark)TextView text_bookmark;
    @BindView(R.id.text_search)TextView text_search;
    @BindView(R.id.text_page_move)TextView text_page_move;
    @BindView(R.id.text_change_left)Button text_change_left;
    @BindView(R.id.text_change_num)TextView text_change_num;
    @BindView(R.id.text_change_right)Button text_change_right;
    @BindView(R.id.text_change_relative)RelativeLayout text_change_relative;
    @BindView(R.id.text_change_title)TextView text_change_title;
    @BindView(R.id.search_content)EditText search_content;
    @BindView(R.id.search_before)Button search_before;
    @BindView(R.id.search_next)Button search_next;
    @BindView(R.id.search_close)Button search_close;
    @BindView(R.id.search_main_line)LinearLayout search_main_line;
    @BindView(R.id.page_move_seek)SeekBar page_move_seek;
    @BindView(R.id.page_move_edit)EditText page_move_edit;
    @BindView(R.id.page_move_page_num)TextView page_move_page_num;
    @BindView(R.id.page_move_close)Button page_move_close;
    @BindView(R.id.page_move_line)LinearLayout page_move_line;
    @BindView(R.id.page_move_dbr)Button page_move_dbr;
    @BindView(R.id.page_move_nor)Button page_move_nor;
    @BindView(R.id.page_move_nol)Button page_move_nol;
    @BindView(R.id.page_move_dbl)Button page_move_dbl;
    @BindView(R.id.search_content_text)TextView search_content_text;
    @BindView(R.id.page_move_dshbar)TextView page_move_dshbar;
    @BindView(R.id.page_move_first)Button page_move_first;
    @BindView(R.id.txt_page_num)TextView txt_page_num;
    @BindView(R.id.txt_page_full_num)TextView txt_page_full_num;
    @BindView(R.id.text_created)TextView text_created;
    DBmanager dBmanager;

    ArrayList<String> tx = new ArrayList<>();
    TextPrintAdapter textPrintAdapter;

    ArrayList<Position> positions = new ArrayList<>();
    Integer nowPosition = 0;

    int backgroundColor = WHITE;
    Integer textSize = 0;

    String nowFolder = "";
    String name = "";

    int thePage = 10;
    Integer temporaryPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);
        ButterKnife.bind(this);

        String nowName = "";


        dBmanager = new DBmanager(TxtActivity.this,"background.db",null,1);
        backgroundColor = dBmanager.seleteBackground();

        txt_name.setText(nowName);
        String main = getResources().getString(R.string.Qs);
        tx.add(main);
        colorSelect(backgroundColor);

        textPrintAdapter = new TextPrintAdapter(tx,dBmanager);
        txt_list.setAdapter(textPrintAdapter);
        textSize = dBmanager.seleteTextsize();
        text_change_num.setText(textSize.toString());
        positions = dBmanager.seletePosition();

        Integer maxPag = tx.size()/thePage;
        page_move_seek.setMax(maxPag);
        page_move_page_num.setText(maxPag.toString());
        page_move_seek.setProgress(0);
        txt_page_full_num.setText("/0");


        txt_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                nowPosition = firstVisibleItem;
                Integer page = nowPosition/thePage;
                txt_page_num.setText(page.toString());
            }
        });

        txt_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(txt_main_menu.getVisibility() == View.VISIBLE){
                    Animation an = AnimationUtils.loadAnimation(TxtActivity.this,R.anim.menu_down_anim);

                    an.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            txt_main_menu.setVisibility(View.GONE);
                            if(text_change_relative.getVisibility() == View.VISIBLE){
                                text_change_relative.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    txt_main_menu.startAnimation(an);
                }
            }
        });


        txt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_main_menu.getVisibility() == View.GONE){
                    Animation an = AnimationUtils.loadAnimation(TxtActivity.this,R.anim.menu_up_anim);

                    an.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            txt_main_menu.setVisibility(View.VISIBLE);
                            if(search_main_line.getVisibility() == View.VISIBLE){
                                search_main_line.setVisibility(View.GONE);
                                search_content.setText("");
                            }
                            if(page_move_line.getVisibility() == View.VISIBLE){
                                page_move_line.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    txt_main_menu.startAnimation(an);
                }else {
                    Animation an = AnimationUtils.loadAnimation(TxtActivity.this,R.anim.menu_down_anim);

                    an.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            txt_main_menu.setVisibility(View.GONE);
                            if(text_change_relative.getVisibility() == View.VISIBLE){
                                text_change_relative.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    txt_main_menu.startAnimation(an);
                }
            }
        });


        open_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_main_menu.setVisibility(View.GONE);
                Intent intent = new Intent(TxtActivity.this,SearchActivity.class);
                startActivityForResult(intent,1);
            }
        });


        back_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backgroundColor == WHITE){
                    dBmanager.updateBackground(BLACK);
                }else if(backgroundColor == BLACK){
                    backgroundColor = WHITE;
                    dBmanager.updateBackground(WHITE);
                }

                backgroundColor = dBmanager.seleteBackground();
                colorSelect(backgroundColor);
                textPrintAdapter.notifyDataSetChanged();
            }
        });

        text_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_change_relative.getVisibility() == View.GONE){
                    text_change_relative.setVisibility(View.VISIBLE);
                }else{
                    text_change_relative.setVisibility(View.GONE);
                }


            }
        });

        text_change_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textSize > 10){
                    textSize--;
                    dBmanager.updateTextsize(textSize);
                    text_change_num.setText(textSize.toString());
                    int fg = nowPosition;
                    textPrintAdapter = new TextPrintAdapter(tx,dBmanager);
                    txt_list.setAdapter(textPrintAdapter);
                    txt_list.setSelection(fg);
                }
            }
        });

        text_change_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textSize < 30){
                    textSize++;
                    dBmanager.updateTextsize(textSize);
                    text_change_num.setText(textSize.toString());
                    int fg = nowPosition;
                    textPrintAdapter = new TextPrintAdapter(tx,dBmanager);
                    txt_list.setAdapter(textPrintAdapter);
                    txt_list.setSelection(fg);
                }
            }
        });

        bookmark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_main_menu.setVisibility(View.GONE);
                Intent intent1 = new Intent(TxtActivity.this,BookmarkActivity.class);
                String title = tx.get(nowPosition);
                int sg = title.length();
                String nowTitle = "";
                if(sg > 10){
                    nowTitle = title.substring(0,10)+"...";
                }else{
                    nowTitle = title.substring(0,sg)+"...";
                }
                intent1.putExtra("root",nowFolder);
                intent1.putExtra("page",nowPosition);
                intent1.putExtra("title",nowTitle);
                intent1.putExtra("name",name);
                startActivityForResult(intent1,0);
            }
        });


        search_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cont = search_content.getText().toString();
                int pos = searchBefore(nowPosition,cont);
                if (pos != -1){
                    txt_list.setSelection(pos);
                }
            }
        });

        search_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cont = search_content.getText().toString();
                int pos = searchNext(nowPosition,cont);
                if (pos != -1){
                    txt_list.setSelection(pos);
                }
            }
        });

        search_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_content.setText("");
                search_main_line.setVisibility(View.GONE);
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_main_menu.setVisibility(View.GONE);
                search_main_line.setVisibility(View.VISIBLE);
                if(text_change_relative.getVisibility() == View.VISIBLE){
                    text_change_relative.setVisibility(View.GONE);
                }
            }
        });

        page_move_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer pag = progress;
                page_move_edit.setText(pag.toString());
                txt_list.setSelection(progress*thePage);//thePage은 임시 1페이지
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pag = page_move_edit.getText().toString();
                if (pag != null && !pag.equals("")) {
                    Integer nowPag = Integer.parseInt(pag);
                    page_move_seek.setProgress(nowPag);
                    txt_list.setSelection(nowPag * thePage);
                }
            }
        };

        page_move_edit.addTextChangedListener(textWatcher);

        page_move_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_move_line.setVisibility(View.GONE);
            }
        });

        page_move_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_main_menu.setVisibility(View.GONE);
                page_move_line.setVisibility(View.VISIBLE);
                temporaryPage = nowPosition/thePage;
                int nowpos = nowPosition;
                page_move_seek.setProgress(temporaryPage);
                page_move_edit.setText(temporaryPage.toString());
                txt_list.setSelection(nowpos);
                if(text_change_relative.getVisibility() == View.VISIBLE){
                    text_change_relative.setVisibility(View.GONE);
                }
            }
        });

        page_move_dbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pag = page_move_edit.getText().toString();
                Integer nowPag = Integer.parseInt(pag);
                if(nowPag > thePage){
                    Integer movePosi = nowPag-thePage;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }else{
                    Integer movePosi = 0;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }
            }
        });

        page_move_nor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pag = page_move_edit.getText().toString();
                Integer nowPag = Integer.parseInt(pag);
                if(nowPag > 0){
                    Integer movePosi = nowPag-1;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }else{
                    Integer movePosi = 0;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }
            }
        });

        page_move_nol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pag = page_move_edit.getText().toString();
                Integer nowPag = Integer.parseInt(pag);
                Integer maxPage = tx.size()/thePage;
                if(nowPag < maxPage){
                    Integer movePosi = nowPag+1;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }else{
                    Integer movePosi = maxPage;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }
            }
        });

        page_move_dbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pag = page_move_edit.getText().toString();
                Integer nowPag = Integer.parseInt(pag);
                Integer maxPage = tx.size()/thePage;
                if(nowPag < maxPage-thePage){
                    Integer movePosi = nowPag+thePage;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }else{
                    Integer movePosi = maxPage;
                    page_move_seek.setProgress(movePosi);
                    txt_list.setSelection(movePosi);
                    page_move_edit.setText(movePosi.toString());
                }
            }
        });


        page_move_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer first = temporaryPage;
                page_move_seek.setProgress(first);
                txt_list.setSelection(first);
                page_move_edit.setText(first.toString());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == 0) {
                String root = data.getStringExtra("root");
                Integer page = data.getIntExtra("page",0);
                String name = data.getStringExtra("name");
                Integer id = data.getIntExtra("id",0);

                File file = new File(root);

                if(file.exists()==true){
                    nowFolder = root;
                    String nowName = name.replace(".txt", "");
                    txt_name.setText(nowName);
                    tx = ReadTextFile(nowFolder);

                    textPrintAdapter = new TextPrintAdapter(tx,dBmanager);
                    txt_list.setAdapter(textPrintAdapter);

                    Integer maxPag = tx.size()/thePage;
                    page_move_seek.setMax(maxPag);
                    page_move_page_num.setText(maxPag.toString());
                    page_move_seek.setProgress(page/thePage);
                    txt_page_full_num.setText("/"+maxPag);

                    txt_list.setSelection(page);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.notfile, Toast.LENGTH_SHORT).show();
                    dBmanager.deleteBookmark(id);

                }
            }else if(requestCode == 1){
                name = data.getStringExtra("name");
                nowFolder = data.getStringExtra("nowFolder");
                String nowName = name.replace(".txt", "");
                txt_name.setText(nowName);
                tx = ReadTextFile(nowFolder);

                textPrintAdapter = new TextPrintAdapter(tx,dBmanager);
                txt_list.setAdapter(textPrintAdapter);

                Integer maxPag = tx.size()/thePage;
                page_move_seek.setMax(maxPag);
                page_move_page_num.setText(maxPag.toString());
                page_move_seek.setProgress(0);
                txt_page_full_num.setText("/"+maxPag);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public int searchBefore(int nowPosition, String content){
        for(int i = nowPosition; i >= 0; i--){
            if(tx.get(i).contains(content)){
                return i;
            }
        }
        return -1;
    }

    public int searchNext(int nowPosition, String content){
        int pos = nowPosition+2;
        for(int i = pos; i < tx.size(); i++){
            if (tx.get(i).contains(content)) {
                return i;
            }
        }
        return -1;
    }


    public ArrayList<String> ReadTextFile(String text) {
        String encoding = "MS949";
        ArrayList<String> tx = new ArrayList<>();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] BOM = new byte[4];
        try {
            fis.read(BOM, 0, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((BOM[0] & 0xFF) == 0xEF && (BOM[1] & 0xFF) == 0xBB && (BOM[2] & 0xFF) == 0xBF) {
            encoding = "UTF-8";
        } else if ((BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
            encoding = "UTF-16BE";
        } else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE) {
            encoding = "UTF-16LE";
        } else if ((BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00 &&
                (BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
            encoding = "UTF-32BE";
        } else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE &&
                (BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00) {
            encoding = "UTF-32LE";
        } else{
            encoding = "MS949";
        }


        try {
            InputStream inputStream = new FileInputStream(text);
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(inputStream,encoding));
            String line="";
            while((line=reader.readLine())!=null){
                tx.add(line);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tx;
    }


    public void colorSelect(int color){
        if(color == WHITE){
            basic_background.setBackgroundResource(R.color.white);
            txt_main_menu.setBackgroundResource(R.color.white);
            txt_name.setTextColor(getColorStateList(R.color.black));
            text_open_file.setTextColor(getColorStateList(R.color.black));
            text_back_change.setTextColor(getColorStateList(R.color.black));
            text_text_change.setTextColor(getColorStateList(R.color.black));
            text_bookmark.setTextColor(getColorStateList(R.color.black));
            text_search.setTextColor(getColorStateList(R.color.black));
            text_page_move.setTextColor(getColorStateList(R.color.black));
            background_image.setBackgroundResource(R.color.black);
            text_change_relative.setBackgroundResource(R.color.tranWhite);
            text_change_title.setTextColor(getColorStateList(R.color.black));
            text_change_num.setTextColor(getColorStateList(R.color.black));
            search_main_line.setBackgroundResource(R.color.tranWhite);
            search_content_text.setTextColor(getColorStateList(R.color.black));
            search_content.setTextColor(getColorStateList(R.color.black));
            search_before.setBackgroundResource(R.color.tranWhite);
            search_before.setTextColor(getColorStateList(R.color.black));
            search_next.setBackgroundResource(R.color.tranWhite);
            search_next.setTextColor(getColorStateList(R.color.black));
            search_close.setBackgroundResource(R.color.tranWhite);
            search_close.setTextColor(getColorStateList(R.color.black));
            page_move_line.setBackgroundResource(R.color.tranWhite);
            page_move_edit.setTextColor(getColorStateList(R.color.black));
            page_move_dshbar.setTextColor(getColorStateList(R.color.black));
            page_move_page_num.setTextColor(getColorStateList(R.color.black));
            page_move_dbr.setBackgroundResource(R.color.tranWhite);
            page_move_dbr.setTextColor(getColorStateList(R.color.black));
            page_move_nor.setBackgroundResource(R.color.tranWhite);
            page_move_nor.setTextColor(getColorStateList(R.color.black));
            page_move_nol.setBackgroundResource(R.color.tranWhite);
            page_move_nol.setTextColor(getColorStateList(R.color.black));
            page_move_dbl.setBackgroundResource(R.color.tranWhite);
            page_move_dbl.setTextColor(getColorStateList(R.color.black));
            page_move_first.setBackgroundResource(R.color.tranWhite);
            page_move_first.setTextColor(getColorStateList(R.color.black));
            page_move_close.setBackgroundResource(R.color.tranWhite);
            page_move_close.setTextColor(getColorStateList(R.color.black));
            txt_page_num.setTextColor(getColorStateList(R.color.black));
            txt_page_full_num.setTextColor(getColorStateList(R.color.black));
            text_created.setTextColor(getColorStateList(R.color.black));


        }else if(color == BLACK){
            basic_background.setBackgroundResource(R.color.black);
            txt_main_menu.setBackgroundResource(R.color.black);
            txt_name.setTextColor(getColorStateList(R.color.white));
            text_open_file.setTextColor(getColorStateList(R.color.white));
            text_back_change.setTextColor(getColorStateList(R.color.white));
            text_text_change.setTextColor(getColorStateList(R.color.white));
            text_bookmark.setTextColor(getColorStateList(R.color.white));
            text_search.setTextColor(getColorStateList(R.color.white));
            text_page_move.setTextColor(getColorStateList(R.color.white));
            background_image.setBackgroundResource(R.color.white);
            text_change_relative.setBackgroundResource(R.color.tranBlack);
            text_change_title.setTextColor(getColorStateList(R.color.white));
            text_change_num.setTextColor(getColorStateList(R.color.white));
            search_main_line.setBackgroundResource(R.color.tranBlack);
            search_content_text.setTextColor(getColorStateList(R.color.white));
            search_content.setTextColor(getColorStateList(R.color.white));
            search_before.setBackgroundResource(R.color.tranBlack);
            search_before.setTextColor(getColorStateList(R.color.white));
            search_next.setBackgroundResource(R.color.tranBlack);
            search_next.setTextColor(getColorStateList(R.color.white));
            search_close.setBackgroundResource(R.color.tranBlack);
            search_close.setTextColor(getColorStateList(R.color.white));
            page_move_line.setBackgroundResource(R.color.tranBlack);
            page_move_edit.setTextColor(getColorStateList(R.color.white));
            page_move_dshbar.setTextColor(getColorStateList(R.color.white));
            page_move_page_num.setTextColor(getColorStateList(R.color.white));
            page_move_dbr.setBackgroundResource(R.color.tranBlack);
            page_move_dbr.setTextColor(getColorStateList(R.color.white));
            page_move_nor.setBackgroundResource(R.color.tranBlack);
            page_move_nor.setTextColor(getColorStateList(R.color.white));
            page_move_nol.setBackgroundResource(R.color.tranBlack);
            page_move_nol.setTextColor(getColorStateList(R.color.white));
            page_move_dbl.setBackgroundResource(R.color.tranBlack);
            page_move_dbl.setTextColor(getColorStateList(R.color.white));
            page_move_first.setBackgroundResource(R.color.tranBlack);
            page_move_first.setTextColor(getColorStateList(R.color.white));
            page_move_close.setBackgroundResource(R.color.tranBlack);
            page_move_close.setTextColor(getColorStateList(R.color.white));
            txt_page_num.setTextColor(getColorStateList(R.color.white));
            txt_page_full_num.setTextColor(getColorStateList(R.color.white));
            text_created.setTextColor(getColorStateList(R.color.white));
        }
    }


    //txt_content.setMovementMethod(new ScrollingMovementMethod());

        /*
        <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:textSize="20dp"
        android:text="test"
        android:id="@+id/txt_content"
        android:scrollbars="vertical"
        />
         */

    /*
    public String ReadTextFile(String text) {
        String encoding = "MS949";

        // 1. 파일 열기
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 2. 파일 읽기 (4Byte)
        byte[] BOM = new byte[4];
        try {
            fis.read(BOM, 0, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 파일 인코딩 확인하기
        if ((BOM[0] & 0xFF) == 0xEF && (BOM[1] & 0xFF) == 0xBB && (BOM[2] & 0xFF) == 0xBF) {
            encoding = "UTF-8";
        } else if ((BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
            encoding = "UTF-16BE";
        } else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE) {
            encoding = "UTF-16LE";
        } else if ((BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00 &&
                (BOM[0] & 0xFF) == 0xFE && (BOM[1] & 0xFF) == 0xFF) {
            encoding = "UTF-32BE";
        } else if ((BOM[0] & 0xFF) == 0xFF && (BOM[1] & 0xFF) == 0xFE &&
                (BOM[0] & 0xFF) == 0x00 && (BOM[1] & 0xFF) == 0x00) {
            encoding = "UTF-32LE";
        } else{
            encoding = "MS949";
        }


        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream inputStream = new FileInputStream(text);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,encoding));//MS949, euc-kr, UTF-8
            String line="";
            while((line=reader.readLine())!=null){
                strBuffer.append(line+"\n");
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuffer.toString();
    }
     */


}
