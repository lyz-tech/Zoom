package com.third.zoom.common.utils;

/**
 * Created by Administrator on 2018/3/25.
 */

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件工具类
 */
public class SpaceFileUtil {

    public static List<String> getFileByPath(String path) {
        File file = new File(path);
        if(file == null || !file.exists()){
            return null;
        }
        File[] t_Files = file.listFiles();
        if (t_Files == null){
            return null;
        }
        List<String> fileNames = new LinkedList<String>();
        for (int i = 0; i < t_Files.length; i++) {
            fileNames.add(t_Files[i].getAbsolutePath());
        }
        sortByType(fileNames);
        return fileNames;
    }

    public static void sortByType(List<String> a_FileNames) {
        List<String> Res = new LinkedList<String>();

        Collections.sort(a_FileNames);

        // Dir
        for (int i = 0; i < a_FileNames.size(); i++) {
            if (getFileType(a_FileNames.get(i)) == Global.FileType_Dir)
                Res.add(a_FileNames.get(i));
        }

        for (int j = 0; j < Global.FileTypes.length; j++) {
            for (int k = 0; k < Global.FileTypes[j].length; k++) {
                for (int i = 0; i < a_FileNames.size(); i++) {
                    if (a_FileNames.get(i).endsWith(Global.FileTypes[j][k]))
                        Res.add(a_FileNames.get(i));
                }
            }
        }

        for (int i = 0; i < a_FileNames.size(); i++) {
            if (getFileType(a_FileNames.get(i)) == Global.FileType_Other)
                Res.add(a_FileNames.get(i));
        }

        a_FileNames.clear();

        for (int i = 0; i < Res.size(); i++)
            a_FileNames.add(Res.get(i));
    }


    public static int getFileType(String a_FilePath) {
        File l_File = new File(a_FilePath);

        if (l_File.isDirectory())
            return Global.FileType_Dir;

        String l_FileName = l_File.getName();

        for (int i = 0; i < Global.FileTypes.length; i++) {

            int j = checkStringEnds(l_FileName, Global.FileTypes[i]);

            if (j == -1)
                continue;

            return Global.TypeStart[i] + j;
        }

        return Global.FileType_Other;

    }

    private static int checkStringEnds(String a_CheckItem, String[] a_Array) {

        for (int i = 0; i < a_Array.length; i++)

            if (a_CheckItem.endsWith(a_Array[i]))
                return i;

        return -1;
    }

}
