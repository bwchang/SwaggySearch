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

public class SwaggySearch extends JPanel implements ActionListener {

	protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
    private JedisIndex index;

    private static int AND_LENGTH = 5;
    private static int OR_LENGTH = 4;
    private static int NOT_LENGTH = 5;

    public SwaggySearch() {
        super(new GridBagLayout());

        textField = new JTextField(50);
        textField.addActionListener(this);

        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);

        try {
        	Jedis jedis = JedisMaker.make();
			index = new JedisIndex(jedis);
        } catch (IOException e) {
        	System.out.println("error");
        }
        
    }

    public void actionPerformed(ActionEvent evt) {
    	textArea.setText("");

        String text = textField.getText().trim();
        
        // handle AND, OR, MINUS cases
        WikiSearch search = searchFromString(text);

        List<Entry<String, Integer>> entries = search.sort();

        // handle empty cases
        if (entries.isEmpty()) {
            textArea.append("Sorry, we found no matches for your search terms.");
        }

        // limit to ten results
        if (entries.size() <= 10) {
            for (Entry<String, Integer> entry: entries) {
                textArea.append(entry.getKey().toString() + newline);
            }
        } else {
            for (Entry<String, Integer> entry: entries.subList(0, 10)) {
                textArea.append(entry.getKey().toString() + newline);
            }
        }

        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());
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

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Main");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new SwaggySearch());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}