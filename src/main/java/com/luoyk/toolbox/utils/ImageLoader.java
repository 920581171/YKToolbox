package com.luoyk.toolbox.utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader {

    public static final String MAIN_ICON = "/icon/main/icon.png";
    public static final String MAIN_MYSQL = "/icon/main/mysql.png";
    public static final String MAIN_REDIS = "/icon/main/redis.png";
    public static final String MAIN_WEBSOCKET = "/icon/main/websocket.png";
    public static final String MAIN_INFO = "/icon/main/info.png";

    public static final String MYSQL_CONNECTION = "/icon/mysql/connection.png";
    public static final String MYSQL_SQL = "/icon/mysql/sql.png";

    public static Load load(String filePath) {
        try {
            URL url = ImageLoader.class.getResource(filePath);
            InputStream input = url.openStream();
            return new Load(javax.imageio.ImageIO.read(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Load {
        private Image image;

        private Load() {
        }

        private Load(Image image) {
            this.image = image;
        }

        public ImageIcon size32() {
            return size(32);
        }

        public ImageIcon size48() {
            return size(48);
        }

        public ImageIcon size64() {
            return size(64);
        }

        public ImageIcon size128() {
            return size(128);
        }

        public ImageIcon size(int size) {
            ImageIcon imageIcon = new ImageIcon();
            imageIcon.setImage(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            return imageIcon;
        }
    }
}
