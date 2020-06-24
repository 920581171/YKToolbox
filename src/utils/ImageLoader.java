package utils;

import javax.swing.*;
import java.awt.*;

public class ImageLoader {
    public static Load load(String fileName, boolean fromJar) {
        String url = fromJar ? ImageIcon.class.getResource(fileName).getPath() : "D:\\Workspaces\\IdeaProjects\\YKToolbox\\src\\" + fileName;
        return new Load(new ImageIcon(url));
    }

    public static class Load {
        private ImageIcon imageIcon;

        private Load() {
        }

        private Load(ImageIcon imageIcon) {
            this.imageIcon = imageIcon;
        }

        public ImageIcon size32() {
            return size(32);
        }

        public ImageIcon size64() {
            return size(64);
        }

        public ImageIcon size128() {
            return size(128);
        }

        public ImageIcon size(int size) {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT));
            return imageIcon;
        }
    }
}
