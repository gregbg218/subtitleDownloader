package bootstrap;

import domain.SubtitleDownloader;

public class Driver {
    public static void main(String[] args)
    {
        SubtitleDownloader sub = new SubtitleDownloader();
        sub.download();
    }


}
