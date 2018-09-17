package kr.co.z_eong.textviewer.sub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Folder {
    private String name;
    private String nowFolder;
    private int number;
}
