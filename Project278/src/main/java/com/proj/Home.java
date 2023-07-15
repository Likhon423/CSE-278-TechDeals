package com.proj;

import javax.swing.*;

public class Home extends JFrame {
    private JPanel pnlMain;
    private JTextField tfSearch;
    private JButton btnSearch;

    public Home () {
        setTitle("TechDeals");
        setContentPane(pnlMain);
        setSize(580, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnSearch.addActionListener(e -> {
            openResults();
            dispose();
        });

        setVisible(true);
    }

    public void openResults() {
        String searchText = tfSearch.getText().trim();
        new SearchResults(searchText);
    }

    public static void main(String[] args) {
        new Home();
    }
}
