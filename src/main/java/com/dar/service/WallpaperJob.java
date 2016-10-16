package com.dar.service;


import com.dar.api.APIRequestBuilder;
import com.dar.api.DeckChair;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;


class WallpaperJob implements Runnable {

    private static final int native4kHeight = 2160;
    private static final int native4kWidth = 2*native4kHeight;
    // TODO remplacer le path actuel par un path qui marche
    //private static final String imagesPath = "src/main/resources/images/";
    private static final String imagesPath = "/opt/tomcat/webres/";
    private String cameraID;

    WallpaperJob(String camString){
        cameraID = camString;
    }

    public static void main(String[] args) {
        System.out.println("Start test");
        new WallpaperJob("5568862a7b28535025280c72").run();
    }

    @Override
    public void run() {
        //Construct the api call
        System.out.println("Launching background routine");
        String apicall = new APIRequestBuilder<DeckChair>()
                .addDomain(DeckChair.CAMERA)
                .addStr(cameraID)
                .addParam(DeckChair.WIDTH, native4kWidth)
                .addParam(DeckChair.HEIGHT, native4kHeight)
                .addParam(DeckChair.RESIZE, "fill")
                .addParam(DeckChair.GRAVITY, "South")
                .addParam(DeckChair.QUALITY, 100)
                .get();
        String redirect = apicall;
        while (apicall != null){
            apicall = getRedirect(apicall);
            if(apicall != null) redirect = apicall;
        }
        try {
            writeImage(redirect, imagesPath+"original4k.jpg");
            resizeImage(2560, 1440);
            resizeImage(1920, 1080);
            resizeImage(1366, 768);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    private String getRedirect(String urlString){
        try {
            boolean redirect = false;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }
            if (redirect) {
                return conn.getHeaderField("Location");
            }
            return null;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static void writeImage(String imageURL, String fileName) throws IOException{
        URL url = new URL(imageURL);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(fileName);
        byte[] data = new byte[2048];
        int length;
        while ((length = is.read(data)) != -1) {
            os.write(data, 0, length);
        }
        is.close();
        os.close();
    }

    private static void resizeImage(int imgWidth, int imgHeight){
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagesPath + "original4k.jpg"));
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
            g.dispose();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            File output = new File(imagesPath + imgWidth + "x" + imgHeight + ".jpg");
            ImageIO.write(resizedImage,"jpg", output);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}