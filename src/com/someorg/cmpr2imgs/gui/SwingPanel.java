package com.someorg.cmpr2imgs.gui;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class SwingPanel extends JPanel implements ActionListener {
    private static final int BI_HEIGHT = 300;
    private static final int BI_WIDTH = BI_HEIGHT * 2;

    public static int ETALON_DISTANCE = 11;
    public static int HALF_ETALON_DISTANCE = (ETALON_DISTANCE + 1) / 2;

    LinkedBlockingQueue<Area> areas = new LinkedBlockingQueue<Area>();

    JLabel firstImgLabel;
    JLabel secondImgLabel;
    JButton openImage1Btn, openImage2Btn, showDiffBtn;
    JFileChooser filechooser;
    String pathToImg1, pathToImg2;

    private BufferedImage bImage1 = new BufferedImage(BI_WIDTH, BI_HEIGHT,
            BufferedImage.TYPE_INT_RGB);

    private BufferedImage bImage2 = new BufferedImage(BI_WIDTH, BI_HEIGHT,
            BufferedImage.TYPE_INT_RGB);

    public SwingPanel() {
        File currentDir = new File("./");
        filechooser = new JFileChooser(currentDir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PNG Images", "png");
        filechooser.setFileFilter(filter);

        openImage1Btn = new JButton("Open 1st image");
        openImage1Btn.addActionListener(this);

        openImage2Btn = new JButton("Open 2nd image");
        openImage2Btn.addActionListener(this);

        showDiffBtn = new JButton("Show Difference");
        showDiffBtn.addActionListener(this);

        JPanel btnPanel = new JPanel();
        btnPanel.add(openImage1Btn);
        btnPanel.add(openImage2Btn);
        btnPanel.add(showDiffBtn);

        JPanel imgsPanel = new JPanel();
        firstImgLabel = new JLabel(new ImageIcon(bImage1));
        firstImgLabel.setBorder(BorderFactory.createEtchedBorder());

        secondImgLabel = new JLabel(new ImageIcon(bImage2));
        secondImgLabel.setBorder(BorderFactory.createEtchedBorder());

        imgsPanel.setLayout(new BorderLayout());
        imgsPanel.add(firstImgLabel, BorderLayout.NORTH);
        imgsPanel.add(secondImgLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(imgsPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void openFirstImage() {
        int result = filechooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File firstImgFile = filechooser.getSelectedFile();
            pathToImg1 = firstImgFile.getAbsolutePath();
            if (bImage1 != null)
                try {
                    bImage1 = ImageIO.read(firstImgFile);

                    BufferedImage resized = new BufferedImage(BI_WIDTH, BI_HEIGHT, bImage1.getType());
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(bImage1, 0, 0, BI_WIDTH, BI_HEIGHT, 0, 0, bImage1.getWidth(), bImage1.getHeight(), null);
                    g.dispose();
                    bImage1 = resized;
                    firstImgLabel.setIcon(new ImageIcon((Image) bImage1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void openSecondImage() {
        int result = filechooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File secondImgFile = filechooser.getSelectedFile();
            pathToImg2 = secondImgFile.getAbsolutePath();
            try {
                bImage2 = ImageIO.read(secondImgFile);

                BufferedImage resized = new BufferedImage(BI_WIDTH, BI_HEIGHT, bImage2.getType());
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(bImage2, 0, 0, BI_WIDTH, BI_HEIGHT, 0, 0, bImage2.getWidth(), bImage2.getHeight(), null);
                g.dispose();
                bImage2 = resized;
                secondImgLabel.setIcon(new ImageIcon((Image) bImage2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void createAndShowUI() {

        JFrame frame = new JFrame("DrawAndSaveImage");
        frame.getContentPane().add(new SwingPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowUI();
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openImage1Btn) {
            openFirstImage();
        } else if (e.getSource() == openImage2Btn) {
            openSecondImage();
        } else if (e.getSource() == showDiffBtn) {
            try {
                if (pathToImg1 != null && pathToImg2 != null) {
                    BufferedImage img1 = ImageIO.read(new File(pathToImg1));

                    BufferedImage img2 = ImageIO.read(new File(pathToImg2));


                    cmpImgs(img1, img2, pathToImg2);

                    BufferedImage bi = ImageIO.read(new File(pathToImg2.replace(".", "_.")));

                    BufferedImage resized = new BufferedImage(BI_WIDTH, BI_HEIGHT, bi.getType());
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(bi, 0, 0, BI_WIDTH, BI_HEIGHT, 0, 0, bi.getWidth(), bi.getHeight(), null);
                    g.dispose();
                    bi = resized;
                    secondImgLabel.setIcon(new ImageIcon(bi));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    public void cmpImgs(BufferedImage img1, BufferedImage img2, String path2img) {
        areas = new LinkedBlockingQueue<Area>();
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
                ImageIO.write(imgSave, "png", new File(path2img.replace(".", "_.")));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private void createArea(Point pnt) {
        if (areas == null) {
            areas = new LinkedBlockingQueue<Area>();
        }
        Area area = new Area();
        area.setZeroPoint(pnt);
        area.getArea().add(new Point(pnt.x, pnt.y));
        areas.add(area);
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
