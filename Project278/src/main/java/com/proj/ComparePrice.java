package com.proj;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ComparePrice extends JFrame {
    private JLabel lbItemName;
    private JPanel pnlMain;
    private JButton btnBack;
    private JLabel lbImage;
    private JLabel lbStartechPrice;
    private JLabel lbTechlandPrice;
    private JButton btnTechlandLink;
    private JButton btnStartechLink;
    private JLabel lbFeatures;
    private String linkTechland, linkStartech;

    public ComparePrice (String itemName, String imageUrl, String url) {
        setTitle(itemName);
        setContentPane(pnlMain);
        setSize(580, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        lbItemName.setText("<html><body style='width: 350px; text-align:center'>" + itemName + "</body></html>");
        setLbImage(imageUrl);

        scrapeData(url);

        btnBack.addActionListener(e -> {
            dispose();
            new Home();
        });

        btnStartechLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebPage(linkStartech);
            }
        });
        btnTechlandLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebPage(linkTechland);
            }
        });

        setVisible(true);
    }

    public void setLbImage(String url) {
        try {
            URL imageUrl = new URL(url);

            boolean isWebP = imageUrl.getFile().toLowerCase().endsWith(".webp");

            if (isWebP) {
                ImageIO.scanForPlugins();
                ImageIO.setUseCache(false);

                ImageReader reader = ImageIO.getImageReadersByFormatName("webp").next();
                reader.setInput(ImageIO.createImageInputStream(imageUrl.openStream()));

                Image image = reader.read(0);

                if (image != null) {
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                    lbImage.setIcon(icon);
                }
            }
            else {
                Image image = ImageIO.read(imageUrl);
                if (image != null) {
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                    lbImage.setIcon(icon);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrapeData(String itemUrl) {
        linkStartech = "https://www.startech.com.bd/" + itemUrl;

        String searchText = itemUrl.replace("-", " ");

        String techlandUrl = "https://www.techlandbd.com/index.php?route=product/search&search=" + searchText;

        try {
            String startechPrice = scrapeStartechPrice(linkStartech);
            lbStartechPrice.setText(startechPrice);
            String techlandPrice = scrapeTechlandPrice(techlandUrl);
            lbTechlandPrice.setText(techlandPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String scrapeStartechPrice(String startechUrl) throws IOException {
        Document startechDoc = Jsoup.connect(startechUrl).ignoreHttpErrors(true).get();

        if (startechDoc.select("body.error-not_found").size() > 0) {
            return "Item not found";
        } else {
            Element priceElement = startechDoc.select(".product-price").first();
            Elements featureElements = startechDoc.select("div.short-description li:not(.view-more)");

            StringBuilder features = new StringBuilder();

            for (Element li : featureElements) {
                String text = li.text();
                features.append("• ").append(text).append("<br>");
            }

            String formattedText = features.toString();
            lbFeatures.setText("<html>" + formattedText + "</html>");

            if (priceElement != null) {
                return priceElement.text();
            } else {
                return "Price not found";
            }
        }
    }

    private String scrapeTechlandPrice(String techlandUrl) throws IOException {
        Document techlandDoc = Jsoup.connect(techlandUrl).ignoreHttpErrors(true).get();

        Element notFoundElement = techlandDoc.select("div.main-products-wrapper p:contains(There is no product that matches the search criteria.)").first();

        if (notFoundElement != null) {
            return "Item not found";
        } else {
            Element priceElement = techlandDoc.select("div.price > div > span:first-child").first();
            linkTechland = techlandDoc.select(".name a").first().attr("href");

            if (priceElement != null) {
                return priceElement.text();
            } else {
                return "Price not found";
            }
        }
    }

    private void openWebPage(String url) {
        try {
            URI uri = new URI(url);
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
