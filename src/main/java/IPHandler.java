import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by kernel32 on 11.08.2017.
 */
public class IPHandler {
    private static Logger log = Logger.getLogger("RouterIP");

    public static void checkIP()  {
        String routerIP = getRouterIP();
        System.out.println("Router IP: " + routerIP + " at " + new Date());

        String wcamIP = getWcamIP();
        System.out.println("Wcam IP: " + wcamIP + " at " + new Date());

        if(routerIP != null && wcamIP != null && !routerIP.equalsIgnoreCase(wcamIP)){
            log.info("RouterIP: IP was changed from " + wcamIP + " to " + routerIP);
            saveRouterIP(routerIP);
        }
    }

    public static String getRouterIP() {
        String html = getHtml();
        String ipAddress = null;
        Scanner scanner = null;
        try {
            scanner = new Scanner(html);
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String row = scanner.next();
                if(row.startsWith("function wanlink_ipaddr() { return")) {
                    ipAddress = row.substring(row.indexOf("'") + 1, row.lastIndexOf("'"));
                }
            }
        } finally {
            if(scanner != null)
                scanner.close();
        } if (ipAddress == null || ipAddress.isEmpty()){
            String errorMessage = "RouterIP: IP is empty after parsing router page";
            log.error(errorMessage);
            Phone.sendMessage(errorMessage);
        }
        return ipAddress;
    }

    public static String getWcamIP() {
        InputStream in = null;
        StringBuilder out = null;
        BufferedReader reader = null;

        String server = "javac.in.ua";
        int port = 21;

        /*should be added user/pass for FTP*/
        String user = "";
        String pass = "";

        FTPClient ftpClient = new FTPClient();
        try {
            out = new StringBuilder();
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            in = ftpClient.retrieveFileStream("ip.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } catch (IOException ex) {
            String errorMessage = "RouterIP: getting IP from FTP file error";
            log.error(errorMessage, ex);
            Phone.sendMessage(errorMessage);
            ex.printStackTrace();
        } finally {
            closeFtp(ftpClient);
            closeReader(reader);
            closeInputStream(in);
        }
        return out.toString();
    }

    private static void closeInputStream(InputStream in) {
        if(in != null){
            try {
                in.close();
            } catch (IOException ex) {
                String errorMessage = "RouterIP: FTP closing input stream error";
                log.error(errorMessage, ex);
                Phone.sendMessage(errorMessage);
                ex.printStackTrace();
            }
        }
    }

    private static void closeReader(BufferedReader reader) {
        if(reader != null){
            try {
                reader.close();
            } catch (IOException ex) {
                String errorMessage = "RouterIP: FTP closing reader error";
                log.error(errorMessage, ex);
                Phone.sendMessage(errorMessage);
                ex.printStackTrace();
            }
        }
    }

    private static void closeFtp(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            String errorMessage = "RouterIP: FTP closing connection error";
            log.error(errorMessage, ex);
            Phone.sendMessage(errorMessage);
            ex.printStackTrace();
        }
    }

    public static String getHtml() {
        /*should be added user/pass of router*/
        String username = "";
        String password = "";
        String login = username + ":" + password;
        String base64login = new String(Base64.encodeBase64(login.getBytes()));

        Document doc = null;
        try {
            doc = Jsoup
                    .connect("http://192.168.1.1")
                    .header("Authorization", "Basic " + base64login)
                    .get();
        } catch (IOException ex) {
            String errorMessage = "RouterIP: parsing router page error";
            log.error(errorMessage, ex);
            Phone.sendMessage(errorMessage);
            ex.printStackTrace();
        }
        return doc.toString();
    }

    public static void saveRouterIP(String ip) {
        OutputStream out = null;
        String server = "javac.in.ua";
        int port = 21;

        /*should be added user/pass of FTP*/
        String user = "";
        String pass = "";

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            byte[] ipBytes = ip.getBytes();
            out = ftpClient.storeFileStream("ip.txt");
            out.write(ipBytes);
            out.flush();

        } catch (IOException ex) {
            String errorMessage = "RouterIP: save IP in FTP error";
            log.error(errorMessage, ex);
            Phone.sendMessage(errorMessage);
            ex.printStackTrace();
        } finally {
            closeFtp(ftpClient);
            closeOutPutStream(out);
        }
    }

    private static void closeOutPutStream(OutputStream out) {
        if(out != null){
            try {
                out.close();
            } catch (IOException ex) {
                String errorMessage = "RouterIP: close output stream error";
                log.error(errorMessage, ex);
                Phone.sendMessage(errorMessage);
                ex.printStackTrace();
            }
        }
    }
}
