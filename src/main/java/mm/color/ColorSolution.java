package mm.color;

import mm.reference.Reference;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kevin Corbett on 12/3/14.
 * This class is responsible for the generation of the code to guess
 */
public class ColorSolution
{
    private String[] codePicked;//The code to break
    private int codeLength;//Holds the length of code
    private int redPegs;//counts red pegs
    private int whitePegs;//counts white pegs
    private String[] strCodePicked;//Holds the string of codes generated

    public ColorSolution(int codeLength)
    {
        setCodeLength(codeLength);//sets length of codePicked
        setCode();//sets colors to each
    }

    public void setCodeLength(int length)//sets codePicked length
    {
        codeLength = length;
        codePicked = new String[codeLength];
    }

    /**
     * Generates code for user to try and guess
     */
    public void setCode()
    {
        Random rand = new Random();
        strCodePicked = new String[getCodeLength()];//initializes code array
        for (int i = 0; i < codePicked.length; i++)
        {
            int picked = rand.nextInt(8);//gets random number 0-7
            switch (picked)//switches on picked color
            {
                case 0://if 0 was picked
                    codePicked[i] = Reference.COLOR_RED;//make codePicked[i] equal the string for the location of the red icon
                    strCodePicked[i] = ("Red");//adds red to strCodePicked[i]
                    break;
                case 1:
                    codePicked[i] = Reference.COLOR_BLUE;
                    strCodePicked[i] = ("Blue");
                    break;
                case 2:
                    codePicked[i] = Reference.COLOR_GREEN;
                    strCodePicked[i] = ("Green");
                    break;
                case 3:
                    codePicked[i] = Reference.COLOR_BLACK;
                    strCodePicked[i] = ("Black");
                    break;
                case 4:
                    codePicked[i] = Reference.COLOR_WHITE;
                    strCodePicked[i] = ("White");
                    break;
                case 5:
                    codePicked[i] = Reference.COLOR_ORANGE;
                    strCodePicked[i] = ("Orange");
                    break;
                case 6:
                    codePicked[i] = Reference.COLOR_YELLOW;
                    strCodePicked[i] = ("Yellow");
                    break;
                case 7:
                    codePicked[i] = Reference.COLOR_MAGENTA;
                    strCodePicked[i] = ("Magenta");
                    break;
            }//end switch
        }//end for
    }

    /**
     * returns the string for current code in position i
     *
     * @param i the location to get the code from
     * @return String version of the code
     */
    public String getCode(int i)
    {
        if (i <= codePicked.length)//if i is a number not outside of initialized array
            return codePicked[i]; //return that number
        else
            return codePicked[0];//return lowest
    }

    /**
     * Calculates the amount of partial matches between guessed code and the code to break
     *
     * @param guess the player's current guess
     * @return number of partial matches as an integer
     */
    public int getPartialMatches(String[] guess)
    {
        ArrayList<Integer> alreadyMatched = new ArrayList<Integer>();//to store matched guess so no repeats
        for (String CodePicked : codePicked)
            for (int i = 0; i < codePicked.length; i++) //for the guesses
                if (guess[i].equals(CodePicked) && !alreadyMatched.contains(i))//does guess[g] match this code picked, and has the guess not been used?
                {
                    alreadyMatched.add(i);//Yeah, so remove that guess so it does not get another peg.
                    whitePegs++;//add a peg
                    break;//break for loop because we found a match for this piece
                }

        return whitePegs;//return white pegs as int
    }

    /**
     * Calculates the number of exact matches between guessed code and the code to break
     *
     * @param guess the player's current guess
     * @return number of exact guesses as integer
     */
    public int getExactMatches(String[] guess)
    {
        //set pegs to 0
        redPegs = 0;
        whitePegs = 0;

        for (int i = 0; i < codePicked.length; i++)
        {
            if (guess[i].equals(codePicked[i])) //does this guess equal this codePicked in the same index?
                redPegs++;//yes, add a red peg
        }
        whitePegs -= redPegs; //remove a white peg for every red peg. Since whitePegs is an integer, will not go below zero

        return redPegs; //returns the calculated number of red pegs as integer
    }

    /**
     * @return length of code generated array
     */
    public int getCodeLength()
    {
        return codeLength;
    }

    /**
     * @return returns string version of the code generated
     */
    public String toString()
    {
        int counter = 1;//line numbers
        String output = "Code:\n";//holds the output
        for (String StrCodePicked : strCodePicked)
        {
            output += "\t" + counter + ": " + StrCodePicked + "\n";//adds a line with the current position and color to output
            counter++;
        }
        return output;//returns formatted output
    }
}