import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SwingControlDemo implements ActionListener {
    private JFrame mainFrame;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JMenuBar mb;
    private JMenu file, edit, help;
    private JMenuItem cut, copy, paste, selectAll;
    private JTextArea taLink; //typing area
    private JTextArea taSearch;
    private JTextArea taOutput;
    private int WIDTH=800;
    private int HEIGHT=700;
    JButton startButton = new JButton("Start");

    public String readOutput;

    public SwingControlDemo() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingControlDemo swingControlDemo = new SwingControlDemo();
        swingControlDemo.showEventDemo();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Java SWING Examples");
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new GridLayout(1, 1));

        //menu at top
        cut = new JMenuItem("cut");
        copy = new JMenuItem("copy");
        paste = new JMenuItem("paste");
        selectAll = new JMenuItem("selectAll");
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);

        mb = new JMenuBar();
        file = new JMenu("File");
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);
        mb.add(file);
        mb.add(edit);
        mb.add(help);
        //end menu at top

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

            mainFrame.setVisible(true);
    }

    private void showEventDemo() {

        startButton.setActionCommand("Start");

        startButton.addActionListener(new ButtonClickListener());

        mainFrame.add(startButton);

        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cut)
            taLink.cut();
        if (e.getSource() == paste)
            taLink.paste();
        if (e.getSource() == copy)
            taLink.copy();
        if (e.getSource() == selectAll)
            taLink.selectAll();
    }

    //private void jButtonActionPerformed(java.awt.event.ActionEvent evt){
        //JOptionPane.showMessageDialog(JRootPane, taSearch.getText());
    //}

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Start")) {
                mainFrame.remove(startButton);

                mainFrame.setLayout(new GridLayout(2,1));

                controlPanel = new JPanel();
                controlPanel.setLayout(new GridLayout(1,3)); //set the layout of the pannel

                //statusLabel = new JLabel("Waiting for input...", JLabel.CENTER);
                //statusLabel.setSize(350, 100);

                taOutput = new JTextArea("Waiting for input...");
                taOutput.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);

                mainFrame.add(controlPanel);
                mainFrame.add(taOutput);

                taLink = new JTextArea("https://www.nytimes.com/");
                taLink.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);

                JButton readButton = new JButton("Read HTML");
                readButton.setActionCommand("Read");
                readButton.addActionListener(new ButtonClickListener());

                taSearch = new JTextArea("");
                taSearch.setBounds(50,5,WIDTH-100,HEIGHT-100);

                mainFrame.add(mb);  //add menu bar
                controlPanel.add(taLink);//add typing area
                controlPanel.add(readButton);
                controlPanel.add(taSearch);
                mainFrame.setJMenuBar(mb); //set menu bar
                mainFrame.setVisible(true);
            }
            else if(command.equals("Read")){

                String searchTerm = taSearch.getText();
                String linkTerm = taLink.getText();

                URL url = null;
                try {
                    url = new URL(linkTerm);
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }

                URLConnection urlc = null;
                try {
                    urlc = url.openConnection();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(urlc.getInputStream())
                    );
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String line;
                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(line.contains(searchTerm)){
                        //readOutput += line;
                        //taOutput.setText(readOutput);

                        if(line.contains("src=\"")){
                            String[] doubleSourceLinks = line.split("src=\"");
                            for (int i = 0; i < doubleSourceLinks.length; i++) {
                                    doubleSourceLinks[i] = doubleSourceLinks[i].substring(0,doubleSourceLinks[i].indexOf("\""));
                                    System.out.println(doubleSourceLinks[i] + " SRC DOUBLE");
                            }
                        }

                        if(line.contains("src='")){
                            String[] singleSourceLinks = line.split("src='");
                            for (int i = 0; i < singleSourceLinks.length; i++) {
                                    singleSourceLinks[i] = singleSourceLinks[i].substring(0,singleSourceLinks[i].indexOf("'"));
                                    System.out.println(singleSourceLinks[i] + " SRC SINGLE");
                            }
                        }

                        if(line.contains("href=\"")){
                            String[] doubleReferenceLinks = line.split("href=\"");
                            for (int i = 0; i < doubleReferenceLinks.length; i++) {
                                    doubleReferenceLinks[i] = doubleReferenceLinks[i].substring(0,doubleReferenceLinks[i].indexOf("\""));
                                    System.out.println(doubleReferenceLinks[i] + " HREF DOUBLE");
                            }
                        }

                        if(line.contains("href='")){
                            String[] singleReferenceLinks = line.split("href='");
                            for (int i = 0; i < singleReferenceLinks.length; i++) {
                                    singleReferenceLinks[i] = singleReferenceLinks[i].substring(0,singleReferenceLinks[i].indexOf("'"));
                                    System.out.println(singleReferenceLinks[i] + " HREF SINGLE");
                            }
                        }

                        System.out.println(line);

                    }
                }
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try{
            } catch(Exception ex) {
                System.out.println(ex);
                }
            }
        }
    }
}