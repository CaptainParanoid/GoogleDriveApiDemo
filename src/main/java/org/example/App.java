package org.example;
import java.io.*;
import java.security.GeneralSecurityException;


public class App 
{

    public static void main(String... args) throws IOException, GeneralSecurityException {
        String loc ="C:\\timount\\";
        if(args.length!=0){
            loc =args[0];
        }
        String driveUrl="https://drive.google.com/file/d/1_uf9AAIG3GCypmO2RpAUyoGNjxq3i8Ie/view?usp=sharing";
        if(args.length==2){
            driveUrl=args[1];
        }

        DriveUtil.init(App.class.getResourceAsStream("/config.json"));
        FileOutputStream fos = new FileOutputStream( loc + DriveUtil.getFileNameFileUrl(driveUrl));
        DriveUtil.downloadToStreamWithUrl(fos,driveUrl);
    }
}
