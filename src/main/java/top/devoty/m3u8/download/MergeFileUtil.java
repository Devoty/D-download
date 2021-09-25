package top.devoty.m3u8.download;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MergeFileUtil {

    private static final int BUFFER_SIZE = 8192;
    //合并文件

    public static void main(String[] args) {
        String tarFileName = "/Users/devoty/test/hebing.mp4";
        String tarPath = "/Users/devoty/test/2e0fd73f-5dce-4767-be9d-ba853d2c17ef/";
        try {
            merge(tarFileName,tarPath);
            System.out.println("合并完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param tarFileName  合并后的文件名
     * @param tarPath      待合并的目录
     * @throws IOException
     */
    public static void merge(String tarFileName, String tarPath) throws Exception {
        Path outPath = Paths.get(tarFileName);

        OutputStream outputStream = Files.newOutputStream(outPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        File file = new File(tarPath);
        if(!file.isDirectory()){
            throw new Exception("待合并的必须是目录");
        }
        File[] files = file.listFiles();

        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, (o1, o2) -> {

            String fileName1 = o1.getName();
            String num1 = fileName1.substring(0,fileName1.length()-3);

            String fileName2 = o2.getName();
            String num2 = fileName2.substring(0,fileName2.length()-3);

            Integer n1 = Integer.valueOf(num1);
            Integer n2 = Integer.valueOf(num2);
            return n1.compareTo(n2);
        });
        for (File s: fileList) {
            System.out.println(s.getName());
            byte[] bytes = Files.readAllBytes(s.toPath());
            int len = bytes.length;
            int ren = len;
            while (ren>0){
                int n = Math.min(ren, BUFFER_SIZE);
                outputStream.write(bytes,(len-ren),n);
                ren -= n;
            }
        }
        outputStream.close();
    }


}
