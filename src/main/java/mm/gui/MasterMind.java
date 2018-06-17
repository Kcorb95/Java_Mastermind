package mm.gui;

import mm.color.ColorSolution;
import mm.reference.Reference;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kevin Corbett on 12/6/2014.
 * The class which creates the game, and handles the majority of functions within
 */
public class MasterMind extends JFrame
{
    private GridBagConstraints gbc = new GridBagConstraints();//layout manager used a lot, more efficient if field
    private ColorChoicePanel ccp = new ColorChoicePanel(); //creates an instance of the ColorChoicePanel
    private ColorSolution cs = new ColorSolution(4); //creates an instance of ColorSolution class, and generates a new code to guess with length of 4

    private JPanel mainPanel;//the main panel which is added to frame

    private JPanel bottomPanel;//the panel at the bottom of the frame holding submit button and ColorChoicePanel
    private JPanel submitPanel;//panel which holds the button to submit guess

    private JPanel middlePanel;//panel which holds the results
    private JPanel[] rowPanels = new JPanel[12];//copy of submitted guess row
    private JPanel[] pegPanel = new JPanel[12];//the results of the code submitted, 12 rows of peg panels
    private JLabel[][] pegLabel = new JLabel[12][4];// [row 1][pegs 1-4 ordered 2x2]
    private JLabel[][] rowLabel = new JLabel[12][4];//[row number][4 buttons]

    private JPanel codePanel; //the panel at the top of the frame which will be revealed when game has ended, showing generated code
    private JLabel[] codeLabel = new JLabel[4]; //labels because buttons are not needed, no clicking involved
    private JPanel shieldPanel;//the panel that appears when code panel is generated, this hides the enter code number text from image
    private JLabel shieldLabel;//hold the edited picture

    private JPanel musicPanel;//the panel which holds the musicOn controls
    private JCheckBox musicToggle;//the check box that turns musicOn on or off

    private BufferedImage image;//for getting/setting images
    private AudioStream audioStream;//streams the music into the Audio player, set as field to the other methods can access it

    private int currentRowCounter = 0;//keeps track of current row in game 11 is highest
    private int pegCurrentRowCounter = 0;//24 rows of pegs

    public MasterMind()
    {
        setTitle("The Game of Mastermind");//sets title
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try//try this
        {
            image = ImageIO.read(new File(Reference.BOARD_BACKGROUND));//get the background image
            this.setContentPane(new JLabel(new ImageIcon(image)));//set the frames background to fetched image
        } catch (IOException e)//handle the ioExceptions
        {
            e.printStackTrace();//prints a stack trace of wat do
        }

        mainPanel = new JPanel();//holds all the panels together top to botom
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);//see through

        buildBottomPanel();//build panel that holds all the control buttons, and the main panel created in ColorChoicePanel
        buildRows();//builds the results rows
        buildCodePanel();//builds the reveal row at the top which holds the shield and the code generated
        buildMusicPanel();//builds the panel that holds the musicOn control button and sets the musicOn up at the top of the screen

        gbc.anchor = GridBagConstraints.CENTER;//anchor all stuff to center of panel
        gbc.gridx = 0;//aligns against left wall of panel
        gbc.gridy = 1;//on the second row
        gbc.insets = new Insets(16, 50, 50, 50);//top left bottom right padding
        mainPanel.add(codePanel, gbc); //adds code panel and shield panel on top of each other.
        mainPanel.add(shieldPanel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);//sets padding of the panel back to nothing

        gbc.gridx = 0;//left side
        gbc.gridy = 0;//very top
        mainPanel.add(musicPanel, gbc);//adds musicOn control panel


        gbc.anchor = GridBagConstraints.LINE_START;//adds the middle panel hugging the right side of the panel
        gbc.gridx = 0;//aligns against left wall
        gbc.gridy = 2;//on the second row
        gbc.ipadx = 50;//so pegs fit in place
        mainPanel.add(middlePanel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;//aligns to center of panel
        gbc.gridx = 0;//aligns against the left wall
        gbc.gridy = 3;//on the 3rd row
        mainPanel.add(bottomPanel, gbc);

        setResizable(false);//resizing window gets funky, lock it down
        add(mainPanel);//adds populated main panel to frame
        setLayout(new FlowLayout());//sets layout to flow layout
        pack();//packs the window to smallest size without eating components
        setVisible(true);//makes window visible
        musicOn();//turns up the musicOn
    }

    /**
     * Builds panel at bottom of page
     * Contains the submit, new game, clear button and the ColorChoicePanel
     */
    public void buildBottomPanel()
    {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setOpaque(false);//see through

        submitPanel = new JPanel();
        submitPanel.setLayout(new GridBagLayout());
        submitPanel.setOpaque(false);//no white background

        ccp.setOpaque(false);//sets color choice panel to be see through

        //connects the 2 buttons from color choice panel to this action listener
        ccp.restartButton.addActionListener(new ButtonListener());
        ccp.submitButton.addActionListener(new ButtonListener());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 0;
        bottomPanel.add(ccp, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);//padding of 5 all around so buttons have space
        bottomPanel.add(submitPanel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);//sets them back to 0
    }

    /**
     * Builds the rows that contain the pegs and the copied guess
     */
    public void buildRows()
    {
        middlePanel = new JPanel();
        middlePanel.setLayout(new GridBagLayout());
        middlePanel.setOpaque(false);

        int rowCounter = rowLabel.length;//set row counter to number of rows

        for (int y = 0; y < rowLabel.length; y++)//row counter
        {
            rowPanels[y] = new JPanel();//row number y
            rowPanels[y].setLayout(new GridLayout(1, 4));//one row 4 col
            rowPanels[y].setOpaque(false);
            pegPanel[y] = new JPanel();//row number 7
            pegPanel[y].setLayout(new GridLayout(2, 2));//2 row 2 col
            pegPanel[y].setOpaque(false);

            for (int x = 0; x < rowLabel[y].length; x++)//col counter
            {
                rowLabel[y][x] = new JLabel((new ImageIcon(Reference.COLOR_EMPTY)));//set row labels to the default icon
                pegLabel[y][x] = new JLabel((new ImageIcon(Reference.PEG_EMPTY)));//as with the pegs

                rowPanels[y].add(rowLabel[y][x]);
                pegPanel[y].add(pegLabel[y][x]);
            }


            gbc.ipady = 10;//add row labels with 150 pixels between and 10 vertically
            gbc.ipadx = 150;
            gbc.gridx = 0;
            gbc.gridy = rowCounter;
            middlePanel.add(rowPanels[y], gbc);

            gbc.ipadx = 5;
            gbc.ipady = 5;
            gbc.gridx = 1;
            gbc.gridy = rowCounter;
            rowCounter--;//count back to go up a row
            middlePanel.add(pegPanel[y], gbc);
        }
    }

    /**
     * Builds the panel which houses the generated code panel, and the shield panel
     */
    public void buildCodePanel()
    {
        codePanel = new JPanel();
        codePanel.setLayout(new GridBagLayout());
        codePanel.setOpaque(false);

        shieldPanel = new JPanel();
        shieldPanel.setLayout(new GridLayout());
        shieldPanel.setOpaque(false);
        try//try this
        {
            BufferedImage shieldPicture = ImageIO.read(new File(Reference.BOARD_SHIELD));//get the picture of the shield
            shieldLabel = new JLabel(new ImageIcon(shieldPicture));//set the gathered image and store it on the panel
        } catch (IOException e)//catches the Input Output exception because I am using file handling this time
        {
            e.printStackTrace();//prints out the wat do
        }

        gbc.ipadx = 25;
        gbc.gridy = 0;
        int xPos = 0;

        for (int i = 0; i < codeLabel.length; i++)
        {
            codeLabel[i] = new JLabel(new ImageIcon(cs.getCode(i)));//gets the string from the index of the generated code and sets the codeLabel in this index to it.
            gbc.gridx = xPos;
            xPos++;
            codePanel.add(codeLabel[i], gbc);
        }
        gbc.gridx = 0;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        shieldPanel.add(shieldLabel, CENTER_ALIGNMENT);//aligns shield in middle of panel

        hideCode();//makes code hidden
    }

    /**
     * Builds the panel which houses the checkbox that toggles music sound
     * Also handles the music itself
     */
    public void buildMusicPanel()
    {
        musicPanel = new JPanel();
        musicPanel.setOpaque(false);
        musicToggle = new JCheckBox("Toggle Music", true);
        musicToggle.setOpaque(false);
        musicToggle.addItemListener(new MusicListener());//adds item listener for when the checkbox changes
        musicPanel.add(musicToggle);

        try
        {
            InputStream in = new FileInputStream(Reference.MUSIC_GAME);//begins to stream the music from the file to the input
            audioStream = new AudioStream(in);//takes the input stream and streams to player
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void musicOn()
    {
        AudioPlayer.player.start(audioStream);//starts streaming music data to client
    }

    public void musicOff()
    {
        AudioPlayer.player.stop(audioStream);//stops that stream, but music continues on thread. Need way to pause instead

    }

    /**
     * Copies the submitted code to the row labels
     *
     * @param rowLabel submitted code 2d array
     */
    public void copyRow(JLabel[][] rowLabel)
    {
        for (int x = 0; x < rowLabel[currentRowCounter].length; x++)//uses the current row for this run as the y dimension
            rowLabel[currentRowCounter][x].setIcon(new ImageIcon(ccp.getSubmittedColors()[x]));//gets colors from this row
        currentRowCounter++;//increment row
    }

    /**
     * Sets the peg results of the guess
     *
     * @param guessList the list of player guesses as an array of stings
     */
    public void setResults(String[] guessList)
    {
        int redPegs = cs.getExactMatches(guessList);//gets and stores the number of pegs for exact matches
        int whitePegs = cs.getPartialMatches(guessList);//and for partial matches
        //flags for game state
        boolean win = false;
        boolean lose = false;
        if (redPegs == 4)//do we have a win? 4 is the length of code
        {
            win = true;//flag for win after making this now last row
        }

        if (currentRowCounter == 12) //do we have a loss? 12 is last row
        {
            lose = true;
        }

        for (int colCount = 0; colCount < 1; colCount++) //counts vertically, if we hit the second col, go down to the next row
        {
            while (redPegs > 0) //when there are red pegs, do this
            {
                pegLabel[pegCurrentRowCounter][colCount].setIcon(new ImageIcon(Reference.PEG_RED));//set the current row and current column to red peg
                colCount++;//add a column
                redPegs--;//a peg less
            }
            while (whitePegs > 0) //same as red
            {
                pegLabel[pegCurrentRowCounter][colCount].setIcon(new ImageIcon(Reference.PEG_WHITE));
                colCount++;
                whitePegs--;
            }
            pegCurrentRowCounter++;
            break;
        }
        if (win)//if we are in a win state according to the win flag
            makeWin();//run the win scenario
        if (lose) //and for lose
            makeLose();

    }

    /**
     * You won the game, this is what happens
     */
    public void makeWin()
    {
        revealCode();//opposite of hide code, shows the generated code on top of shield
        int condition = JOptionPane.showConfirmDialog(null, "YOU WIN!!\n Would you like to play again?"); //shows what happened, grats. Do you want to play again
        if (condition == JOptionPane.YES_OPTION)//if they hit yes
        {
            restartGame();//run the restart game routine
        } else if (condition == JOptionPane.NO_OPTION)//nah
        {
            System.exit(0);//quits game
        }
    }

    /**
     * You lost the game, what to do next
     */
    public void makeLose()
    {
        revealCode();//shows the code
        int condition = JOptionPane.showConfirmDialog(null, "GAME OVER\n Would you like to try again?");//pops up what happened, you messed up. Do you want to try again
        if (condition == JOptionPane.YES_OPTION)//sure
        {
            restartGame();//resets game state
        } else if (condition == JOptionPane.NO_OPTION)//no
        {
            System.exit(0);//rage quit
        }
    }

    /**
     * reveals the generated code at the top of the board on top of the shield
     */
    private void revealCode()
    {
        for (int i = 0; i < codeLabel.length; i++)//gets all labels
        {
            codeLabel[i].setVisible(true);//sets them all true
        }
        shieldLabel.setVisible(false);//makes shield invisible
    }

    /**
     * Hides the generated code "under the shield"
     */
    private void hideCode()
    {
        for (int i = 0; i < codeLabel.length; i++)
        {
            codeLabel[i].setVisible(false);//sets all labels to be invisible
        }
    }

    /**
     * Resets the game state by running clear guess row, setting flags to false, clearing the rows and pegs, getting a new random code, and hiding the generated code, putting up shield
     */
    public void restartGame()
    {
        ccp.clearGuess();//clears the guess row
        ccp.setFalse();//sets all flags to false so no stale data
        for (int y = 0; y < rowLabel.length; y++)//row counter
        {
            for (int x = 0; x < rowLabel[y].length; x++)//coll counter
            {
                rowLabel[y][x].setIcon(new ImageIcon(Reference.COLOR_EMPTY));
                pegLabel[y][x].setIcon(new ImageIcon(Reference.PEG_EMPTY));
            }
        }
        cs.setCode();//gets a new code
        for (int i = 0; i < codeLabel.length; i++)
        {
            codeLabel[i].setIcon(new ImageIcon(cs.getCode(i)));//sets the code labels to the newly generated code
        }
        //resets counters
        currentRowCounter = 0;
        pegCurrentRowCounter = 0;
        hideCode();//puts up the shield
    }

    /**
     * The listener for the submit button and restart button from the ColorChoicePanel class
     */
    private class ButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == ccp.submitButton && ccp.isRowFull())//Checks if all flags are go as well as the source actually being the submit button
            {
                //System.out.println(cs);//debug
                copyRow(rowLabel);//copies the guess row to the row of labels
                setResults(ccp.getSubmittedColors());//gets the results, passing the submitted colors array from ColorChoicePanel
                ccp.clearGuess();//clears the guess row
                ccp.setFalse();//sets all flags to false so no stale data
            } else if (e.getSource() == ccp.restartButton) //if source was the restart button
            {
                makeLose();//literally the same at the losing game state with an added game over screen because technically you gave up and lost
            }
        }
    }

    /**
     * The listener for the music toggle button
     */
    private class MusicListener implements ItemListener
    {
        @Override
        public void itemStateChanged(ItemEvent e)
        {
            if (e.getSource() == musicToggle) //if the source was the music button
            {
                if (musicToggle.isSelected())//if its on
                    musicOn();//turn on music
                else
                    musicOff();//turn off music
            }
        }
    }
}