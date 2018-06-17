package mm.gui;

import mm.reference.Reference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Kevin Corbett on 12/3/14.
 * <p/>
 * This class is responsible for the bottom panel of the game board.
 * Extends JPanel because this will be added to the main game panel as a panel
 */
public class ColorChoicePanel extends JPanel
{
    private JPanel mainPanel;//main panel for bottom of the board

    private JPanel guessPanel;//the panel which holds the player's guesses
    private JButton[] guessButton = new JButton[4];//the buttons that are pressed to set guessed color

    private JPanel colorPickerPanel;//the panel which will hold the buttons that set the current color
    private JButton[] colorButton = new JButton[8];//sets to 8 because only 8 colors

    private JPanel controlPanel;//the panel which holds the buttons controlling game
    private JButton clearButton;//clears guess
    public JButton submitButton;//public so MasterMind can handle actions while maintaining my preferred formatting. Submits code for evaluation
    public JButton restartButton;//public so MasterMind can handle actions while maintaining my preferred formatting Restarts game

    private String currentColor = Reference.COLOR_EMPTY; //container for the current color selected from the colorPickerPanel
    private String[] guessList = new String[4];//the list of guesses stored in guessPanel
    private boolean[] allClicked = new boolean[4];//flags for making sure all guess buttons have been defined for the round, else stale data occurs

    private Toolkit toolkit = Toolkit.getDefaultToolkit(); //used for getting and setting images

    private Image cursorImage; //for setting mouse cursor to an image
    private GridBagConstraints gbc = new GridBagConstraints(); //for panel layouts, more efficient as a field instead of creating a new instance for every panel that needs it.

    /**
     * Creates the main panel that gets sent off to MasterMind
     */
    public ColorChoicePanel()
    {
        buildGuessPanel();//builds the panel for player guesses
        buildColorPickerPanel();//Builds the panel which contains the buttons that change your active color so you may begin assigning colors to the guessButtons
        buildControlPanel();//builds panel for buttons that control game
        setFalse();//sets all flags to false
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);//gets rid of the white background of panel, so the only thing visible is the contents
        gbc.insets = new Insets(0, 0, 0, 0);//no padding between panels

        //higher numbers for coordinated means further left or down
        gbc.gridx = 0;//set against left side of panel
        gbc.gridy = 1;//right above colorPickerPanel
        mainPanel.add(guessPanel, gbc);//adds guess panel with gbc parameters
        gbc.gridx = 2;//hugs against guessPanel
        gbc.gridy = 2;//goes at the bottom
        mainPanel.add(controlPanel, gbc);//adds the control panel with parameters gbc
        gbc.gridx = 0;//hugs the left side of the panel
        gbc.gridy = 2;//goes at end of page
        mainPanel.add(colorPickerPanel, gbc);//adds the colorPickerPanel with parameters gbc

        add(mainPanel);//adds combined panels to the main class panel that is sent off to MasterMind
    }

    /**
     * This creates the guessPanel that holds the player's guesses at the generated code
     */
    public void buildGuessPanel()
    {
        guessPanel = new JPanel();
        guessPanel.setLayout(new GridBagLayout());
        guessPanel.setOpaque(false);//no white background

        gbc.ipadx = 25;//adds padding between each button, 25 pixels
        gbc.gridy = 0;//set to the bottom of the panel

        for (int i = 0; i < guessButton.length; i++)
        {
            guessButton[i] = new JButton(new ImageIcon(Reference.COLOR_EMPTY));//Creates a new JButton that is using an Icon of the empty image I have created and defined within Reference
            guessButton[i].setBorder(BorderFactory.createEmptyBorder());//no border make button look good
            guessButton[i].setContentAreaFilled(false);//border =/= the extra gray stuff inside
            guessButton[i].addActionListener(new ActionsListener());//connect
            gbc.gridx = i;//sets the x coordinate to current index
            guessPanel.add(guessButton[i], gbc);//add button to panel
        }
    }

    /**
     * Builds the panel which contains the buttons that change your active color so you may begin assigning colors to the guessButtons
     */
    public void buildColorPickerPanel()
    {

        colorPickerPanel = new JPanel();
        colorPickerPanel.setLayout(new GridBagLayout());
        colorPickerPanel.setOpaque(false);//no background
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.ipady = 2;//space of 2 pixels for y coord
        gbc.ipadx = 2;//space of 2 pixels for x coord
        gbc.gridy = 0;//hugs the bottom of panel

        //counter for rows so we get nice 2x4
        int row1 = 0;
        int row2 = 0;

        for (int i = 0; i < colorButton.length; i++)//creating all 8 color selection buttons
        {
            switch (i)//switch to get the color to be assigned
            {
                case 0:
                    currentColor = Reference.COLOR_RED;//sets color location
                    break;
                case 1:
                    currentColor = Reference.COLOR_BLUE;
                    break;
                case 2:
                    currentColor = Reference.COLOR_GREEN;
                    break;
                case 3:
                    currentColor = Reference.COLOR_BLACK;
                    break;
                case 4:
                    currentColor = Reference.COLOR_WHITE;
                    break;
                case 5:
                    currentColor = Reference.COLOR_ORANGE;
                    break;
                case 6:
                    currentColor = Reference.COLOR_YELLOW;
                    break;
                case 7:
                    currentColor = Reference.COLOR_MAGENTA;
                    break;
            }//ends switch
            colorButton[i] = new JButton(new ImageIcon(currentColor));//Creates a new JButton that is using an Icon of the currentColor String
            colorButton[i].setBorder(BorderFactory.createEmptyBorder());//no border make button look good
            colorButton[i].setContentAreaFilled(false);//border =/= the extra gray stuff inside
            colorButton[i].addActionListener(new ColorListener());//connect
            if (row1 < 4)//are less than 5 buttons in this row yet?
            {
                gbc.gridx = row1; //yes, set x coordinate to row1 counter
                row1++;//increment counter
            } else//nope, all full
            {
                gbc.gridy = 1;//change already defined row to 2 so we have a 2x4 grid
                gbc.gridx = row2; //set to the new counter
                row2++;//increment new counter
            }
            colorPickerPanel.add(colorButton[i], gbc);//adds color button to panel

        }//ends for loop
        setCurrentColor(Reference.COLOR_EMPTY);//sets current color back to empty so we aren't changing guesses without a color selected. Helps combat stale data
    }

    /**
     * Builds the control panel which houses the three buttons controlling the game
     */
    public void buildControlPanel()
    {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));//grid layout of 3 rows 1 column
        controlPanel.setOpaque(false);//no white space
        clearButton = new JButton("Clear");//standard buttons
        clearButton.addActionListener(new ActionsListener());//the only button handled from this panel within this class, others are handled in MasterMind, see lines 120 and 121 of MasterMind.class
        submitButton = new JButton("Submit");
        restartButton = new JButton("New Game");
        controlPanel.add(submitButton);
        controlPanel.add(clearButton);
        controlPanel.add(restartButton);
    }

    /**
     * @return Returns the array of strings that are the list of player guesses.
     */
    public String[] getSubmittedColors()
    {
        return this.guessList;
    }

    /**
     * The method ran when we need to clear the guess row
     */
    public void clearGuess()
    {
        for (int i = 0; i < guessButton.length; i++)
        {
            guessButton[i].setIcon(new ImageIcon(Reference.COLOR_EMPTY)); //sets the guess button to the default icon
            guessList[i] = Reference.COLOR_EMPTY; //sets list of guesses to default icon which is not allowed to pass in error checking to help prevent stale data
        }
        getRootPane().setCursor(Cursor.getDefaultCursor());//sets cursor back to the default cursor for OS
        setCurrentColor(Reference.COLOR_EMPTY);//sets current color back to empty so we arent adding colors despite not clicking a color this round. prevents stale data
        setFalse();//sets all flags back to no go
    }

    /**
     * Sets all flags for the submit button to false
     * allClicked is the flag name
     */
    public void setFalse() //sets flags false so no messed up submits
    {
        for (int i = 0; i < allClicked.length; i++)
        {
            allClicked[i] = false;
        }
    }

    /**
     * This is the method that utilized the flags. If the flags are ALL true, submit button in MasterMind may proceed.
     *
     * @return boolean Returns whether or not all flags are true
     */
    public boolean isRowFull() //checks flags if they are all cleared before submitting
    {
        boolean bool = false;
        for (int i = 0; i < allClicked.length; i++)
        {
            if (!allClicked[i])//is flag in position i false?
            {
                bool = false;//set bool to false
                break;//break for loop
            } else if (allClicked[i])//nope this one is good
            {
                bool = true;//set bool to true, continue to next iteration
            }
        }
        return bool;//return state of flags
    }

    /**
     * @return returns the string for currentColor
     */
    public String getCurrentColor()
    {
        return this.currentColor;
    }

    /**
     * sets current color to provided color string
     *
     * @param currentColor String for color icon
     */
    public void setCurrentColor(String currentColor)
    {
        this.currentColor = currentColor;
    }

    /**
     * Handles setting current color from the color selection panel
     */
    private class ColorListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            for (int i = 0; i < colorButton.length; i++)//loop until we have correct button clicked
            {
                if (e.getSource() == colorButton[i])//is it the button clicked?
                {
                    switch (i)//switching on i because we need to set color
                    {
                        case 0://if red button was clicked
                            setCurrentColor(Reference.COLOR_RED);//set current color to the string where the red Icon is located
                            cursorImage = toolkit.getImage(getCurrentColor());//get the image defined from currentColor
                            break;
                        case 1:
                            setCurrentColor(Reference.COLOR_BLUE);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 2:
                            setCurrentColor(Reference.COLOR_GREEN);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 3:
                            setCurrentColor(Reference.COLOR_BLACK);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 4:
                            setCurrentColor(Reference.COLOR_WHITE);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 5:
                            setCurrentColor(Reference.COLOR_ORANGE);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 6:
                            setCurrentColor(Reference.COLOR_YELLOW);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                        case 7:
                            setCurrentColor(Reference.COLOR_MAGENTA);
                            cursorImage = toolkit.getImage(getCurrentColor());
                            break;
                    }//end for
                    Point origin = new Point(16, 16);//sets the point at which the cursor clicks somewhere towards the middle of the circle, whereas 0,0 is the tip of the mouse pointer arrow, the icon set to cursor is 32x32 pixels, so middle should be 16, 16
                    Cursor cursor = toolkit.createCustomCursor(cursorImage, origin, "Colored cursor");//creates a cursor using parameter cursor image, with a point defined in origin and a generic name
                    getRootPane().setCursor(cursor);//sets cursor to created cursor
                    break;//break out of for loop because we have found the button and did stuff
                }//end if e is button
            }//ends for loop
        }
    }

    /**
     * Handles the setting of a guess button, and the clear button
     */
    private class ActionsListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            for (int i = 0; i < guessButton.length; i++)//for loop to find clicked button
            {
                if (e.getSource() == guessButton[i])//is this the button clicked
                {
                    allClicked[i] = true;//set flag in position i to true
                    guessButton[i].setIcon(new ImageIcon(currentColor));//yup so lets set its icon to the currently selected color, which is set up inside of the ColorListener Class
                    guessList[i] = currentColor;//set current index of guess list to the current color
                    break;//break the loop for we have found our button
                } else if (e.getSource() == clearButton)
                {
                    clearGuess();//runs the clear guess method at line 187
                }
            }//end for
        }
    }
}
