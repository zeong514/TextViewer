package kr.co.z_eong.textviewer.sub;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bookmark {
    private int id;
    private String root;
    private int page;
    private String title;
    private String name;
}
