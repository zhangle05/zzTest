/**
 * 
 */
package test;

import java.util.Random;

/**
 * @author zhangle
 *
 */
public class NumGuess {

    enum Strategy {
        ALWAYS_CHANGE, NOT_CHANGE, RANDOM_CHANGE
    }

    private int boxCount = 0;
    private Strategy strategy;
    private int answer;
    private int open;
    private Random r;

    public NumGuess(Random r, Strategy strategy) {
        this.r = r;
        this.strategy = strategy;
    }

    public void init(int boxCount) {
        this.boxCount = boxCount;
        this.answer = r.nextInt(this.boxCount) + 1;
        System.out.println("answer is:" + answer);
    }

    public boolean start() throws IllegalStateException {
        int guess = r.nextInt(this.boxCount) + 1;
        System.out.println("first guess is:" + guess);
        openOneBox(guess);
        if (this.open == guess) {
            throw new IllegalStateException("Firt guess opened!");
        }
        if (this.open == this.answer) {
            throw new IllegalStateException("Answer box opened!");
        }
        if (this.strategy == Strategy.ALWAYS_CHANGE) {
            guess = changeGuess(guess);
        } else if (this.strategy == Strategy.RANDOM_CHANGE) {
            if ((r.nextInt(1000) % 2) > 0) {
                guess = changeGuess(guess);
            }
        }
        return guess == this.answer;
    }

    public void openOneBox(int firstGuess) {
        int available = this.boxCount;
        if (firstGuess == answer) {
            available = this.boxCount - 1;
        } else {
            available = this.boxCount - 2;
        }
        int openIdx = r.nextInt(available) + 1;
        for (this.open = 1; this.open < this.boxCount + 1; this.open++) {
            if (this.open == firstGuess || this.open == this.answer) {
                continue;
            }
            openIdx--;
            if (openIdx <= 0) {
                break;
            }
        }
        System.out.println("open box is:" + this.open);
    }

    public int changeGuess(int firstGuess) {
        int result = 0;
        int available = this.boxCount - 2;
        int secondIdx = r.nextInt(available) + 1;
        for (result = 1; result < this.boxCount + 1; result++) {
            if (result == firstGuess || result == this.open) {
                continue;
            }
            secondIdx--;
            if (secondIdx <= 0) {
                break;
            }
        }
        System.out.println("second guess is:" + result);
        return result;
    }

    public static void main(String[] args) {
        Random r = new Random(System.currentTimeMillis());
        NumGuess ng = new NumGuess(r, Strategy.ALWAYS_CHANGE);
        int boxCount = 4;
        int iterations = 10000;
        int wins = 0;
        for (int i = 0; i < iterations; i++) {
            ng.init(boxCount);
            if (ng.start()) {
                wins++;
            }
            System.out.println("-----------------");
        }
        System.out.println("win count is:" + wins);
        System.out.println("win percentage is:" + (double)wins / iterations * 100 + "%");
    }
}
