import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SwingControlDemo implements ActionListener {
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JMenuBar mb;
    private JMenu file, edit, help;
    private JMenuItem cut, copy, paste, selectAll;
    private JTextArea taLink; //typing area
    private JTextArea taSearch;
    private JTextArea taOutput;
    private final int WIDTH=800;
    private final int HEIGHT=700;
    JButton startButton = new JButton("Start");

    public String readOutput;
    public String[] manyTerms;
    public String[] singleReferenceLinks;
    public String[] doubleReferenceLinks;
    public String[] singleSourceLinks;
    public String[] doubleSourceLinks;
    public String finalOutput;

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

        mainFrame.add(startButton); //when program starts, adds massive start button to begin the program

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

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Start")) { //Once start of the program start button is pressed, sets up new GUI for all the input boxes and Read HTML button
                mainFrame.remove(startButton); //first removes the giant start button to show the rest of the gui

                mainFrame.setLayout(new GridLayout(2,1)); //changes grid layout of mainframe

                controlPanel = new JPanel();
                controlPanel.setLayout(new GridLayout(1,3)); //set the layout of the pannel

                taOutput = new JTextArea("Waiting for input..."); //creates JText Area Object
                taOutput.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);

                JScrollPane scrollPane = new JScrollPane(taOutput,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // I need to reference what I looked up for this https://www.google.com/search?q=how+to+add+scroll+wheel+java+swing+text+area&rlz=1C1CFYW_enUS1188&oq=how+to+add+scroll+wheel+java+swing+text+area&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQIRigATIHCAIQIRigATIHCAMQIRigATIHCAQQIRigATIHCAUQIRifBdIBCTExNjg2ajBqN6gCALACAA&sourceid=chrome&ie=UTF-8&safe=active&ssui=on

                mainFrame.add(controlPanel);
                mainFrame.add(scrollPane);

                taLink = new JTextArea("Input a link to a website for its HTML to be parsed for links.\nYou can also search for specific terms"); //creates JText Area Object
                taLink.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);

                JButton readButton = new JButton("Read HTML");
                readButton.setActionCommand("Read");
                readButton.addActionListener(new ButtonClickListener()); //adds a listener to whenever the button is clicked

                taSearch = new JTextArea("Input search terms separated\nby commas with no spaces"); //creates JText Area Object
                taSearch.setBounds(50,5,WIDTH-100,HEIGHT-100);

                mainFrame.add(mb);  //add menu bar
                controlPanel.add(taLink);//add typing area
                controlPanel.add(readButton); //adds button to read HTML
                controlPanel.add(taSearch); //adds typing area to identify search terms
                mainFrame.setJMenuBar(mb); //set menu bar
                mainFrame.setVisible(true);
            }
            else if(command.equals("Read")) { //if Read HTML button is clicked it sends this command and triggers process

                manyTerms = (taSearch.getText() + ",").split(","); //gets the search terms from the input search term box, adds comma at the end
                // so as to not get null return for trying to split a string's commas for a string that doesnt have any commas

                BufferedReader reader = getBufferedReader();
                String line;
                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                            if (line.contains(" src=\"")) {
                                doubleSourceLinks = line.split("src=\"");
                                for (int i = 1; i < doubleSourceLinks.length; i++) {
                                    doubleSourceLinks[i] = doubleSourceLinks[i].substring(0, doubleSourceLinks[i].indexOf("\"")); //shortens each shortened string to the first instance of a double quote which is where the link ends
                                    readOutput += doubleSourceLinks[i] + "\n"; //assigns the found link to the eventual output which is later checked if it contains a given search term
                                }
                            }

                            if (line.contains(" src='")) {
                                singleSourceLinks = line.split("src='");
                                for (int i = 1; i < singleSourceLinks.length; i++) {
                                    singleSourceLinks[i] = singleSourceLinks[i].substring(0, singleSourceLinks[i].indexOf("'")); //shortens each shortened string to the first instance of a single quote which is where the link ends
                                    readOutput += singleSourceLinks[i] + "\n"; //assigns the found link to the eventual output which is later checked if it contains a given search term
                                }
                            }

                            if (line.contains(" href=\"")) {  //checks if a given line contains an href" link
                                doubleReferenceLinks = line.split("href=\""); //splits the line into multiple strings at each instance of an href
                                for (int i = 1; i < doubleReferenceLinks.length; i++) {
                                    doubleReferenceLinks[i] = doubleReferenceLinks[i].substring(0, doubleReferenceLinks[i].indexOf("\"")); //shortens each shortened string to the first instance of a double quote which is where the link ends
                                    readOutput += doubleReferenceLinks[i] + "\n"; //assigns the found link to the eventual output which is later checked if it contains a given search term
                                }
                            }

                            if (line.contains(" href='")) {
                                singleReferenceLinks = line.split("href='");
                                for (int i = 1; i < singleReferenceLinks.length; i++) {
                                    singleReferenceLinks[i] = singleReferenceLinks[i].substring(0, singleReferenceLinks[i].indexOf("'"));
                                    readOutput += singleReferenceLinks[i] + "\n";
                                }
                            }
                    taOutput.setText(readOutput); //sets all links to readoutput so readoutput is not considered null (dont know why that was an issue)

                    String[] termCheck = taOutput.getText().split("\n"); //splits each link back into individual lines to be searched 1 by 1
                    for (int i = 0; i < manyTerms.length; i++) { //runs for loop for every search term identified
                        for (int j = 0; j < termCheck.length; j++) { //checks every line for a given search term
                            if(termCheck[j].contains(manyTerms[i])){
                                finalOutput += termCheck[j] + "\n";
                            }
                        }
                    }
                    taOutput.setText(finalOutput);
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

        private BufferedReader getBufferedReader() {
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
            return reader;
        }
    }
}