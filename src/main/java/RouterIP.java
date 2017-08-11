import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kernel32 on 07.05.2017.
 */
public class RouterIP {
    public static void main(String[] args) throws IOException {
        Scheduler process = new Scheduler();
        process.checkIp();
    }
}

