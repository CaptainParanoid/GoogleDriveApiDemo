package org.example;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * @author Aby Kuruvilla
 */
public class DriveUtil {
    private static final String APPLICATION_NAME = "DriveUtilForServiceAccount";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_READONLY);
    private static final String SEPERATOR = "/";
    private static Drive service;


    /**
     * Have to initialize drive service account credentials, from file location
     */
    public static void init(String serviceAccountLoc) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, fromServiceAccount(new FileInputStream(serviceAccountLoc)))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Have to initialize drive service account credentials, from file stream
     */
    public static void init(InputStream configFileStream) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, fromServiceAccount(configFileStream))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Download as file using the full drive url, The fileId is parsed from the url,
     *
     * @param fileUrl             The full url of file from drive
     * @param fileLocation        The location to download the file along with filename, unless useOriginalName is true
     * @param useOriginalFileName Use original filename specified in the google drive.
     */
    public static void downloadFromUrl(String fileUrl, String fileLocation, boolean useOriginalFileName) throws IOException {
        String fileId = getFileIdFromFileUrl(fileUrl);
        downloadAsFileWithId(fileId, fileLocation, useOriginalFileName);
    }

    /**
     * Download as file using the full drive url, The fileId is parsed from the url,
     *
     * @param fileId              The fileId of the file in drive, id can found in the drive url,between 'https://drive.google.com/file/d/' and  '/view?usp=sharing'
     * @param fileLocation        The location to download the file along with filename, unless useOriginalName is true
     * @param useOriginalFileName Use original filename specified in the google drive.
     */
    public static void downloadAsFileWithId(String fileId, String fileLocation, boolean useOriginalFileName) throws IOException {
        if (useOriginalFileName) {
            fileLocation += SEPERATOR + getFileNameFileId(fileId);
        }
        FileOutputStream fout = new FileOutputStream(new File(fileLocation));
        downloadToStreamWithId(fout, fileId);
    }

    /**
     * Download as file using the full drive url, The fileId is parsed from the url,
     *
     * @param outputStream Write the bytes from drive file to the specified OutputStream
     * @param fileId       The fileId of the file in drive, id can found in the drive url,between 'https://drive.google.com/file/d/' and  '/view?usp=sharing'
     */
    public static void downloadToStreamWithId(OutputStream outputStream, String fileId) throws IOException {
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
    }

    /**
     * Download as file using the full drive url, The fileId is parsed from the url,
     *
     * @param outputStream Write the bytes from drive file to the specified OutputStream
     * @param fileUrl      The full url of file from drive
     */
    public static void downloadToStreamWithUrl(OutputStream outputStream, String fileUrl) throws IOException {
        String fileId = getFileIdFromFileUrl(fileUrl);
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
    }

    /**
     * Get the original name of the file from drive,
     *
     * @param url The full url of file from drive
     */
    public static String getFileNameFileUrl(String url) throws IOException {
        String id = getFileIdFromFileUrl(url);
        com.google.api.services.drive.model.File file = service.files().get(id).execute();
        return file.getName();
    }

    /**
     * Get the original name of the file from drive,
     *
     * @param fileId The fileId of the file in drive, id can found in the drive url,between 'https://drive.google.com/file/d/' and  '/view?usp=sharing'
     */
    public static String getFileNameFileId(String fileId) throws IOException {
        com.google.api.services.drive.model.File file = service.files().get(fileId).execute();
        return file.getName();
    }


    private static String getFileIdFromFileUrl(String fileUrl) {
        return fileUrl.replace("https://drive.google.com/file/d/","")
                .replace("/view?usp=sharing","");
    }

    private static HttpRequestInitializer fromServiceAccount(InputStream configJsonFile) throws IOException {
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(configJsonFile).createScoped(SCOPES);
        return new HttpCredentialsAdapter(credentials);
    }
}
