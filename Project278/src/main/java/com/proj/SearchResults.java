package com.proj;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchResults extends JFrame {
    private JPanel pnlSearchMain;
    private JTable tblSearchItems;
    private JButton btnBack;
    private List<SearchResult> searchResults;

    public SearchResults(String searchText) {
        setTitle("Search Results");
        setContentPane(pnlSearchMain);
        setSize(580, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Image", "Item Name"}, 0);
        tblSearchItems.setModel(tableModel);

        try {
            searchResults = listItems(searchText);
            for (SearchResult result : searchResults) {
                tableModel.addRow(new Object[]{result.getImageUrl(), result.getItemName()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tblSearchItems.setRowHeight(200);

        tblSearchItems.getColumnModel().getColumn(0).setCellRenderer(new ImageTableCellRenderer());

        // click to go to website
        tblSearchItems.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                int row = tblSearchItems.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    SearchResult selectedItem = searchResults.get(row);
                    String itemName = selectedItem.getItemName();
                    String imageUrl = selectedItem.getImageUrl();
                    String trimmedUrl = selectedItem.getTrimmedUrl();

                    new ComparePrice(itemName, imageUrl, trimmedUrl);
                }
            }
        });

        btnBack.addActionListener(e -> {
            dispose();
            new Home();
        });

        setVisible(true);
    }

    // scrape data
    public List<SearchResult> listItems(String searchText) throws IOException {
        List<SearchResult> searchResults = new ArrayList<>();

        String searchUrl = "https://www.startech.com.bd/product/search?search=" + searchText;
        Document doc = Jsoup.connect(searchUrl).get();

        Elements resultElements = doc.select(".p-item-inner");

        int count = 0;

        for (Element element : resultElements) {
            if (count >= 10) {
                break;
            }
            String imageUrl = element.select(".p-item-img a img").attr("src");
            String itemName = element.select(".p-item-name a").text();

            String url = element.select(".p-item-name a").attr("href");
            String trimmedUrl = url.replaceFirst("^" + "https://www.startech.com.bd/", "");

            SearchResult result = new SearchResult(imageUrl, itemName, trimmedUrl);
            searchResults.add(result);

            count++;
        }

        return searchResults;
    }

    // Render image
    private static class ImageTableCellRenderer extends DefaultTableCellRenderer {
        private ImageIcon placeholderIcon;

        // placeholder image
        public ImageTableCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            placeholderIcon = new ImageIcon("awesomeface.png");
            Image placeholderImage = placeholderIcon.getImage();
            Image scaledPlaceholderImage = placeholderImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            placeholderIcon = new ImageIcon(scaledPlaceholderImage);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();

            if (value != null) {
                try {
                    URL imageUrl = new URL((String) value);

                    boolean isWebP = imageUrl.getFile().toLowerCase().endsWith(".webp");

                    if (isWebP) {
                        ImageIO.scanForPlugins();
                        ImageIO.setUseCache(false);

                        ImageReader reader = ImageIO.getImageReadersByFormatName("webp").next();
                        reader.setInput(ImageIO.createImageInputStream(imageUrl.openStream()));

                        Image image = reader.read(0);

                        if (image != null) {
                            ImageIcon icon = new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                            label.setIcon(icon);
                        }
                    }
                    else {
                        Image image = ImageIO.read(imageUrl);
                        if (image != null) {
                            ImageIcon icon = new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                            label.setIcon(icon);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                label.setIcon(placeholderIcon);
            }
            return label;
        }
    }
}
