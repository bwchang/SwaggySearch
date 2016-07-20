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

    public SwaggySearch() {
        super(new GridBagLayout());

        textField = new JTextField(50);
        textField.addActionListener(this);

        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Add Components to this panel.
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

        String text = textField.getText();
        WikiSearch search = WikiSearch.search(text, index);
        List<Entry<String, Integer>> entries = search.sort();
		for (Entry<String, Integer> entry: entries) {
			textArea.append(entry.toString() + newline);
		}

        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());
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
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}