package com.tansci.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64Util {
    public static String PICTURE_PARENT_PATH ;

    static {
        if (!SystemUtil.isWindows()) {
            PICTURE_PARENT_PATH = "/app/samplepictures/";
        } else {
            File directory = new File("");//设定为当前文件夹
            PICTURE_PARENT_PATH = "samplepictures" + File.separator;
        }
    }

    public static String convertImageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        byte[] imageBytes = baos.toByteArray();

        Base64.Encoder encoder = Base64.getEncoder();
        String base64String = encoder.encodeToString(imageBytes);

        return base64String;
    }

    public static void main(String[] args) {
        try {
            File directory = new File("");//设定为当前文件夹
            String picture_parent_path =
                    directory.getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "resources"
                            + File.separator + "samplepictures" + File.separator;
            System.out.println(picture_parent_path);

//            String imagePath = "E:\\ideaworkspaces\\tools\\src\\main\\resources\\samplepictures\\Apple000000.jpg";
            String imagePath = picture_parent_path + File.separator + "Apple000000.jpg";

            String base64String = convertImageToBase64(imagePath);
            System.out.println(base64String);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
