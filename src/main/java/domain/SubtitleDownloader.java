package domain;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class SubtitleDownloader {
    public static final String URL    = "https://www.podnapisi.net/en/subtitles/search/?keywords=";
    Scanner sc = new Scanner(System.in);

    public void download (){
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println();
        String destPath = System.getProperty("user.dir");
        String searchURL = getSearchURL();
        try
        {
            downloadFile(new URL(chooseFile(searchURL)),destPath+File.separator+"test.zip");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unzip(destPath+File.separator+"test.zip", destPath);
        new File(destPath+File.separator+"test.zip").delete();
    }

    public String getSearchURL()
    {
        String finalURL = "";
        String name = "";
        System.out.println("Choose Movie(M) or TV Series(T)");
        if(sc.nextLine().toLowerCase().equals("m"))
        {
            System.out.println();
            name = getName().trim().replace(' ','+');
            System.out.println();
            finalURL =URL + name + "&movie_type=movie&seasons=&episodes=&year=&type=";
        }
        else
        {
            System.out.println();
            name = getName().trim().replace(' ','+');
            System.out.println();

            String type ="&movie_type=tv-series&";

            System.out.println("Enter Season number");
            String seasonNo = "seasons="+sc.nextLine().trim()+"&";
            System.out.println();

            System.out.println("Enter episode number");
            String episodeNo = "episodes="+sc.nextLine().trim()+"&year=";
            System.out.println();

            finalURL = URL + name + type + seasonNo + episodeNo;

        }

        //EXAMPLE
        //https://www.podnapisi.net/en/subtitles/search/?keywords=Only+Murders+in+the+Building&movie_type=tv-series&seasons=1&episodes=1&year=
        //https://www.podnapisi.net/en/subtitles/search/?keywords=Only+Murders+in+the+Building&movie_type=tv-series&seasons=1&episodes=&year=
        // https://www.podnapisi.net/en/subtitles/search/?keywords=reservation+dogs&movie_type=&seasons=1&episodes=&year=&type=
        System.out.println(finalURL);
        System.out.println();
        return finalURL;
    }

    public String getName()
    {
        String source[] = System.getProperty("user.dir").split(File.separator);
        String name = source[source.length-1].toLowerCase();

        if(name.contains("season"))
        {
            name = name.substring(0,name.indexOf("season"));
        }

        if (name.contains("s0"))
        {
            name = name.substring(0,name.indexOf("s0"));
        }

        if(name.contains("("))
        {
            name = name.substring(0,name.indexOf("("));
        }
        name=name.replace(".","").replace("-","");
        System.out.println(name);
        return name;
    }


    public String chooseFile(String searchURL)
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
        Element table  = doc.getElementsByClass("table table-striped table-hover").first();
        Elements body = table.select("tbody");
        Elements rows = body.select("tr");
        ArrayList<String> links = new ArrayList<String>();
        for(int i=0;i< rows.size();i++)
        {
            Element td = rows.get(i).select("td").first();
            System.out.println(i+")  "+td.text());
            links.add(td.select("a[href]").first().attr("href"));
        }
        System.out.println("Choose file number");
        int fileNo = sc.nextInt();
        System.out.println();
        String downloadURL = "https://www.podnapisi.net"+links.get(fileNo);
        return downloadURL;
    }

    public void downloadFile(URL url, String fileName) throws IOException {
        FileUtils.copyURLToFile(url, new File(fileName));
    }

    public void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
