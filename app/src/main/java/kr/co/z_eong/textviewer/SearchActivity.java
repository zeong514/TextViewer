package kr.co.z_eong.textviewer;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.z_eong.textviewer.adapter.FileListAdapter;
import kr.co.z_eong.textviewer.db.DBmanager;
import kr.co.z_eong.textviewer.sub.Folder;

public class SearchActivity extends AppCompatActivity {
    public static int WHITE = 0;
    public static int BLACK = 1;
    @BindView(R.id.sear_edit)EditText sear_edit;
    @BindView(R.id.sear_text)TextView sear_text;
    @BindView(R.id.sear_list)ListView sear_list;
    @BindView(R.id.sear_main_back)LinearLayout sear_main_back;
    @BindView(R.id.sear_mainMemory)CheckBox sear_mainMemory;
    @BindView(R.id.sear_SDCard)CheckBox sear_SDCard;
    @BindView(R.id.sear_esc)Button sear_esc;
    @BindView(R.id.sear_mem_line)LinearLayout sear_mem_line;
    ArrayList<Folder> filesNameList = new ArrayList<>();
    ArrayList<Folder> originalFilesNameList = new ArrayList<>();
    FileListAdapter fileListAdapter;
    DBmanager dBmanager;

    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    String nowRoot = root;
    Integer nowPos = 0;

    int backgroundColor = WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        dBmanager = new DBmanager(SearchActivity.this,"background.db",null,1);

        nowRoot = dBmanager.seleteRoot();
        sear_list.setSelection(dBmanager.seletePage());

        backgroundColor = dBmanager.seleteBackground();
        colorSelect(backgroundColor);
        sear_mainMemory.setChecked(true);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "승인된 권한", Toast.LENGTH_SHORT).show();

                printList();

                fileListAdapter = new FileListAdapter(filesNameList,dBmanager);
                sear_list.setAdapter(fileListAdapter);
                sear_list.setSelection(dBmanager.seletePage()+1);

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(MainActivity.this, "사용 권한이 거부되었습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(SearchActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("허가를 거부하면 이 서비스를 사용할 수 없습니다.\n\n [설정]> [권한]에서 권한을 켜십시오.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)//이곳에 권한을 입력
                .check();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
                fileListAdapter.notifyDataSetChanged();
            }
        };

        sear_edit.addTextChangedListener(textWatcher);

        sear_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                nowPos = firstVisibleItem;
            }
        });



        sear_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Folder item = filesNameList.get(position);
                sear_edit.setText("");
                File f = new File(nowRoot);
                File[] files = f.listFiles();
                if (item.getName().equals("/...")){
                    nowRoot = item.getNowFolder();
                    printList();
                    fileListAdapter.notifyDataSetChanged();
                }else if(files[item.getNumber()].isDirectory()){
                    nowRoot = item.getNowFolder();
                    printList();
                    fileListAdapter.notifyDataSetChanged();
                }else if(item.getName().contains(".txt")){
                    dBmanager.updateRoot(nowRoot);
                    dBmanager.updatePage(nowPos);
                    Intent intent = getIntent();
                    intent.putExtra("name",item.getName());
                    intent.putExtra("nowFolder",item.getNowFolder());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });

        sear_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sear_mainMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sear_SDCard.setChecked(false);
                sear_mainMemory.setChecked(true);
                root = Environment.getExternalStorageDirectory().getAbsolutePath();
                nowRoot = root;
                printList();
                fileListAdapter = new FileListAdapter(filesNameList,dBmanager);
                sear_list.setAdapter(fileListAdapter);
            }
        });

        sear_SDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sear_mainMemory.setChecked(false);
                sear_SDCard.setChecked(true);
                root = getExternalStorageDirectories()[0];
                nowRoot = root;
                printList();
                fileListAdapter = new FileListAdapter(filesNameList,dBmanager);
                sear_list.setAdapter(fileListAdapter);
            }
        });

    }

    public void printList(){
        File f = new File(nowRoot);
        File[] files = f.listFiles();

        filesNameList.clear();
        originalFilesNameList.clear();

        if(!nowRoot.equals(root)){
            String pastFolder = f.getParent();
            Folder folder = new Folder("/...",pastFolder,-1);
            filesNameList.add(folder);
            originalFilesNameList.add(folder);
        }


        for (int i=0; i< files.length; i++) {
            String fils = "";
            if(files[i].isDirectory())
                fils = files[i].getName()+"/";
            else
                fils = files[i].getName();
            String nowRoott = nowRoot+"/"+files[i].getName();
            Folder folder = new Folder(fils,nowRoott,i);
            filesNameList.add(folder);
            originalFilesNameList.add(folder);
        }
        String root = "..."+ nowRoot.substring(8);
        sear_text.setText(root);
    }

    public void search(String searchName){
        filesNameList.clear();
        if (searchName.equals("")) {
            filesNameList.addAll(originalFilesNameList);
        }else{
            for(int i = 0; i < originalFilesNameList.size(); i++){
                if(originalFilesNameList.get(i).getName().toLowerCase().contains(searchName.toLowerCase())){
                    filesNameList.add(originalFilesNameList.get(i));
                }
            }
        }
    }


    public void colorSelect(int color){
        if(color == WHITE){
            sear_main_back.setBackgroundResource(R.color.white);
            sear_edit.setTextColor(getColorStateList(R.color.black));
            sear_text.setTextColor(getColorStateList(R.color.black));
            sear_mem_line.setBackgroundResource(R.color.white);
            sear_mainMemory.setTextColor(getColorStateList(R.color.black));
            sear_SDCard.setTextColor(getColorStateList(R.color.black));
            sear_esc.setBackgroundResource(R.color.white);
            sear_esc.setTextColor(getColorStateList(R.color.black));

        }else if(color == BLACK){
            sear_main_back.setBackgroundResource(R.color.black);
            sear_edit.setTextColor(getColorStateList(R.color.white));
            sear_text.setTextColor(getColorStateList(R.color.white));
            sear_mem_line.setBackgroundResource(R.color.black);
            sear_mainMemory.setTextColor(getColorStateList(R.color.white));
            sear_SDCard.setTextColor(getColorStateList(R.color.white));
            sear_esc.setBackgroundResource(R.color.black);
            sear_esc.setTextColor(getColorStateList(R.color.white));
        }
    }

    public String[] getExternalStorageDirectories() {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                }
                else{
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if(addPath){
                    results.add(path);
                }
            }
        }

        if(results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if(!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for(String voldPoint: devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d("LOG_TAG", results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d("LOG_TAG", results.get(i)+" might not be extSDcard");
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for(int i=0; i<results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }

}
