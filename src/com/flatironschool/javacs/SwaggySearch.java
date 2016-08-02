package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

    protected static URI uri;
    StyledDocument doc;
    SimpleAttributeSet attr;

    public SwaggySearch() {
        /*super(new GridBagLayout());

        textField = new JTextField(50);
        textField.addActionListener(this);

        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);

        textPane = new JTextPane();

        Container c = getContentPane();
        c.add(textField,BorderLayout.NORTH);
        c.add(scrollPane);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);*/

        try {
        	Jedis jedis = JedisMaker.make();
			index = new JedisIndex(jedis);
        } catch (IOException e) {
        	System.out.println("error");
        }

    }

    public void actionPerformed(ActionEvent evt) {
    	//textArea.setText("");

        String text = textField.getText().trim();
        
        WikiSearch search = searchFromString(text);

        List<Entry<String, Integer>> entries = search.sort();

        class OpenUrlAction implements ActionListener {
            @Override public void actionPerformed(ActionEvent e) {
                open(uri);
            }
        }

        // handle empty cases
        if (entries.isEmpty()) {
            //textArea.append("Sorry, we found no matches for your search terms.");
            JButton button = new JButton();
            button.setText("Sorry, we found no matches for your search terms.");
            formatAndInsertButton(button);
        }

        // limit to ten results
        for (Entry<String, Integer> entry: firstTen(entries)) {
            //textArea.append(entry.getKey() + newline);
        }

        textField.selectAll();
        //textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private void formatAndInsertButton(JButton button) {
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBackground(Color.WHITE);
        //button.setToolTipText(uri.toString());
        textPane.insertComponent(button);
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
        //Create and set up the window.
        /*JFrame frame = new JFrame("Main");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new SwaggySearch());

        //Display the window.
        frame.pack();
        frame.setVisible(true);*/

        setTitle("SwaggySearch");
        textField = new JTextField(10);
        textPane = new JTextPane();
        click = new JButton("Search");
        doc = textPane.getStyledDocument();
        attr = new SimpleAttributeSet();
        JScrollPane pane = new JScrollPane(textPane);
        JPanel nPanel = new JPanel();
        nPanel.add(textField);nPanel.add(click);
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

	/*public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}*/

    public static void main(String[] args) throws URISyntaxException
    {
        //uri = new URI("http://java.sun.com");
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