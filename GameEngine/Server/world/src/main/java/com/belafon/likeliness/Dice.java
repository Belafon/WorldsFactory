package com.belafon.likeliness;

import java.util.Random;

public class Dice {
	private int numOfFaces;
	private Random random; 
	
    public Dice(int numOfFaces) {
        this.numOfFaces = numOfFaces;
        random = new Random();
    }
	
    /**
     * @return Simulates the toss with the dice. 
     * It retunrs random number from 1 to number of faces range.
     */
    public int toss() {
        if (numOfFaces == 0)
            return 0;
        return (random.nextInt(this.numOfFaces) + 1);
    }

    /**
     * @return Simulates the series of tosses with the dice. 
     * It retunrs an array of random numbers from 1 to number of faces range. 
     */
	public int[] toss(int number) {
		int[] vectOfoutputs = new int[number];
		int i = number;
		while(i > 0) {
			vectOfoutputs[i - 1] = random.nextInt(this.numOfFaces) + 1;
			i--;
		}
		return vectOfoutputs;

	}
}
