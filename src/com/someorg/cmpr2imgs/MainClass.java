package com.someorg.cmpr2imgs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClass {
    public static int ETALON_DISTANCE = 7;
    public static int HALF_ETALON_DISTANCE = (ETALON_DISTANCE + 1) / 2;

    LinkedBlockingQueue<Area> areas = new LinkedBlockingQueue<Area>();

    public void cmpImgs(BufferedImage img1, BufferedImage img2, String path2img) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                        if (areas.isEmpty()) {
                            createArea(new Point(x, y));
                        } else {
                            chooseOrCreateAreaForPoint(new Point(x, y));
                        }

                    }
                }
            }
            try {
                BufferedImage imgSave = ImageIO.read(new File(path2img));
                Graphics2D g2d = imgSave.createGraphics();
                g2d.setColor(new Color(255, 0, 0));
                g2d.setStroke(new BasicStroke(1));
                for (Area area : areas) {
                    g2d.draw(area.getPolygon().getBounds());
                }
                ImageIO.write(imgSave, "jpg", new File(path2img + "_"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        //gets two urls of images to compare
        if (args.length == 2) {
            String path1 = args[1];
            String path2 = args[1];
        }

        final String[] args1 = args;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                MainClass mc = new MainClass();
                mc.saveTest(args1);
            }

        });

        t.start();


    }


    public void saveTest(String[] args) {
        BufferedImage image = null;
        try {
            String path2img1 = "/home/maks/maks/tmp/out1.png";
            String path2img2 = "/home/maks/maks/tmp/out2.png";

            BufferedImage img1 = ImageIO.read(new File(path2img1));
            BufferedImage img2 = ImageIO.read(new File(path2img2));

            cmpImgs(img1, img2, path2img1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    synchronized private void createArea(Point pnt) {
        if (areas == null) {
            areas = new LinkedBlockingQueue<Area>();
        }
        Area area = new Area();
        area.setZeroPoint(pnt);
        area.getArea().add(new Point(pnt.x, pnt.y));
        areas.add(area);
        System.out.println("area is created");
    }

    synchronized private void chooseOrCreateAreaForPoint(Point pnt) {
        synchronized (areas) {
            boolean firstRun = true;
            int minDstnc = 0;
            Area minArea = null;
            for (Area area : areas) {
                for (Point p : area.getArea()) {
                    if (firstRun) {
                        minDstnc = distance(p, pnt);
                        minArea = area;
                        firstRun = false;
                    } else {
                        int dstnc = distance(p, pnt);
                        if (minDstnc > dstnc) {
                            minArea = area;
                            minDstnc = dstnc;
                        }
                    }
                }
            }
            if (minDstnc >= HALF_ETALON_DISTANCE && minDstnc <= ETALON_DISTANCE) {
                minArea.getArea().add(new Point(pnt.x, pnt.y));
            } else if (minDstnc < HALF_ETALON_DISTANCE) {
                minArea.getZeroPoint().setLocation(pnt.x, pnt.y);
            } else {
                createArea(pnt);
            }
        }
        System.out.println("point is added to existing area");
    }

    public static int distance(Point p1, Point p2) {
        return (int) Math.sqrt(Math.pow((double) (p1.x - p2.x), 2) + Math.pow((double) (p1.y - p2.y), 2));
    }

    static class Area {
        private Point zeroPoint;
        private LinkedBlockingQueue<Point> area = new LinkedBlockingQueue<Point>();

        void setZeroPoint(Point zeroPoint) {
            this.zeroPoint = zeroPoint;
        }

        void setArea(LinkedBlockingQueue<Point> area) {
            this.area = area;
        }

        Point getZeroPoint() {

            return zeroPoint;
        }

        LinkedBlockingQueue<Point> getArea() {
            return area;
        }

        public Polygon getPolygon() {
            Polygon plgn = new Polygon();
            for (Point p : area) {
                plgn.addPoint(p.x, p.y);
            }
            plgn.addPoint(zeroPoint.x, zeroPoint.y);
            return plgn;
        }
    }

}

