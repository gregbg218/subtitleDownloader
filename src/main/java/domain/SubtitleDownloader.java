package domain;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class SubtitleDownloader {
    public static final String URL    = "https://www.podnapisi.net/en/subtitles/search/?keywords=";
    Scanner sc = new Scanner(System.in);

    public void download (){
        String searchURL = getSearchURL();
        System.out.println("Enter destination path");
        String destPath = sc.nextLine().trim();
        try
        {
            downloadFile(new URL(extractDownloadURL(searchURL)),destPath+"//test.zip");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unzip(destPath+"//test.zip", destPath);
        new File(destPath+"//test.zip").delete();
    }

    public String getSearchURL()
    {
        String finalURL = "";
        System.out.println("Choose Movie(M) or TV Series(T)");
        if(sc.nextLine().toLowerCase().equals("m"))
        {
            System.out.println("Enter movie name");
            String movieName = sc.nextLine().trim().toLowerCase().replace(' ','+');
            finalURL =URL + movieName + "&movie_type=movie&seasons=&episodes=&year=&type=";
        }
        else
        {
            System.out.println("Enter tv series name");
            String seriesName = sc.nextLine().trim().toLowerCase().replace(' ','+');
//        String seriesName = "reservation+dogs";

            String type ="&movie_type=tv-series&";

            System.out.println("Enter Season number");
            String seasonNo = "seasons="+sc.nextLine().trim()+"&";
//        String seasonNo = "1&";

            System.out.println("Enter episode number");
            String episodeNo = "episodes="+sc.nextLine().trim()+"&year=";

            finalURL = URL + seriesName + type + seasonNo + episodeNo;

        }

        //EXAMPLE
        //https://www.podnapisi.net/en/subtitles/search/?keywords=Only+Murders+in+the+Building&movie_type=tv-series&seasons=1&episodes=1&year=
        //https://www.podnapisi.net/en/subtitles/search/?keywords=Only+Murders+in+the+Building&movie_type=tv-series&seasons=1&episodes=&year=
        // https://www.podnapisi.net/en/subtitles/search/?keywords=reservation+dogs&movie_type=&seasons=1&episodes=&year=&type=
        System.out.println(finalURL);
        return finalURL;
    }

    public String extractDownloadURL(String searchURL)
    {
        Document doc = null;
        try
        {
            doc = Jsoup.connect(searchURL).get();
        }
        catch (IOException io)
        {
            System.out.println(io);
        }
        //table table-striped table-hover
        Element table  = doc.getElementsByClass("table table-striped table-hover").first();
        Element td = table.getElementsByTag("td").first();
        Elements links = td.select("a[href]");
        String downloadURL = "https://www.podnapisi.net"+links.last().attr("href")+"/download";
        System.out.println(downloadURL);
        return downloadURL;
    }

    public void downloadFile(URL url, String fileName) throws IOException {
        FileUtils.copyURLToFile(url, new File(fileName));
    }

    public void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
