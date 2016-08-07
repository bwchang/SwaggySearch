package com.flatironschool.javacs;

import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.net.URL;

import redis.clients.jedis.Jedis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;

public class SwaggySearch extends JFrame implements ActionListener {

	protected JTextField textField;
    protected JTextArea textArea;
    protected JTextPane textPane;
    private final static String newline = "\n";
    private JedisIndex index;
    protected JButton click;

    private static int AND_LENGTH = 5;
    private static int OR_LENGTH = 4;
    private static int NOT_LENGTH = 5;

    protected URI uri;
    StyledDocument doc;
    SimpleAttributeSet attr;

    public SwaggySearch() {
        try {
        	Jedis jedis = JedisMaker.make();
			index = new JedisIndex(jedis);
        } catch (IOException e) {
        	System.out.println("error");
        }
    }

    public void actionPerformed(ActionEvent evt) {

        textPane.setText("");

        String text = textField.getText().trim();

        if (text.equals("")) {
            JButton button = new JButton();
            button.setText("Please enter a search term.");
            formatAndInsertButton(button);
            return;
        }
				try {
					searchFunction(text);
				}
				catch (IOException e) {}
			}

			public void searchFunction(String text) throws IOException {
				try {
					String correctedSearchTerm = new FuzzySearch().correct(text);
	        WikiSearch search = searchFromString(correctedSearchTerm);
					if (! text.equals(correctedSearchTerm)) {
						JButton button = new JButton();
						button.setText("We found your closest match to be " + correctedSearchTerm);
						formatAndInsertButton(button);
					}
	        List<Entry<String, Integer>> entries = search.sort();

	        class OpenUrlAction implements ActionListener {
	            @Override public void actionPerformed(ActionEvent e) {
	                open(uri);
	            }
	        }

	        // handle empty cases
	        if (entries.isEmpty()) {
	            JButton button = new JButton();
	            button.setText("Sorry, we found no matches for your search terms.");
	            formatAndInsertButton(button);
	        } else {
	            try {
	                doc.insertString(0, "Top 10 search results:" + newline, null);
	            } catch (Exception e) {}
	        }

	        // limit to ten results
	        for (Entry<String, Integer> entry: firstTen(entries)) {
	            JButton button = new JButton();
	            button.setText(entry.getKey());
	            hyperlinkButton(button);
	            formatAndInsertButton(button);
	        }

	        textField.selectAll();
	        textPane.setCaretPosition(textPane.getDocument().getLength());
	    }
			catch (IOException e) {
				System.out.println("error");
			}

				}
				//calling the search here
				//compile the dictionary and call the class


    private void formatAndInsertButton(JButton button) {
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBackground(Color.WHITE);
        try {
            doc.insertString(doc.getLength(), newline, null);
        } catch (Exception e) {}

        textPane.insertComponent(button);
    }

    private void hyperlinkButton(final JButton button) {
        final String s = button.getText();
        final URI uri = getURI(s);
        if (uri != null) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent arg0) {
                    button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    button.setText(s);
                }

                @Override
                public void mouseEntered(MouseEvent arg0) {
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    button.setText(String.format("<HTML><FONT color = \"#000099\"><U>%s</U></FONT></HTML>", s));
                }

                @Override
                public void mouseClicked(MouseEvent arg0) {
                    open(uri);
                }
            });
        }
    }

    private URI getURI(String url) {
        try {
            uri = new URI(url);
            return uri;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private List<Entry<String, Integer>> firstTen(List<Entry<String, Integer>> list) {
        if (list.size() <= 10) {
            return list;
        } else {
            return list.subList(0, 10);
        }
    }

    private WikiSearch searchFromString(String text) {

        WikiSearch search;

        if (text.contains(" AND ")) {
            int start = text.indexOf(" AND ");
            String first = text.substring(0, start).toLowerCase().trim();
            String second = text.substring(start + AND_LENGTH).toLowerCase().trim();
            WikiSearch firstSearch = WikiSearch.search(first, index);
            WikiSearch secondSearch = WikiSearch.search(second, index);
            search = firstSearch.and(secondSearch);
        } else if (text.contains(" OR ")) {
            int start = text.indexOf(" OR ");
            String first = text.substring(0, start).toLowerCase().trim();
            String second = text.substring(start + AND_LENGTH).toLowerCase().trim();
            WikiSearch firstSearch = WikiSearch.search(first, index);
            WikiSearch secondSearch = WikiSearch.search(second, index);
            search = firstSearch.or(secondSearch);
        } else if (text.contains(" NOT ")) {
            int start = text.indexOf(" NOT ");
            String first = text.substring(0, start).toLowerCase().trim();
            String second = text.substring(start + AND_LENGTH).toLowerCase().trim();
            WikiSearch firstSearch = WikiSearch.search(first, index);
            WikiSearch secondSearch = WikiSearch.search(second, index);
            search = firstSearch.minus(secondSearch);
        } else {
            search = WikiSearch.search(text.toLowerCase(), index);
        }

        return search;
    }

    private void createAndShowGUI() {
        setTitle("SwaggySearch");
        textField = new JTextField(10);
        textPane = new JTextPane();
        textPane.setEditable(false);
        click = new JButton("Search");
        doc = textPane.getStyledDocument();
        attr = new SimpleAttributeSet();
        JScrollPane pane = new JScrollPane(textPane);
        JPanel nPanel = new JPanel();
        nPanel.add(textField);
        nPanel.add(click);
        textField.addActionListener(this);
        click.addActionListener(this);
        Container c = getContentPane();
        c.add(nPanel,BorderLayout.NORTH);
        c.add(pane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws URISyntaxException
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                SwaggySearch lta = new SwaggySearch();
                lta.createAndShowGUI();
            }
        });
    }

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(uri);
          } catch (IOException e) { /* TODO: error handling */ }
        } else { /* TODO: error handling */ }
    }
	}
