/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiktionary;

import java.awt.EventQueue;
import java.math.BigInteger;

/**
 *
 * @author rose
 */
public class testCon {
    public static void main(String[] args) throws Exception {
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Integer i0 = 0, i1 = 1, i2 = 2, i3 = 20000000;
                    BigInteger ikk = BigInteger.valueOf(i3);
                    test1();
                    test2(i2);
                    test3(i1);
                }
            });
        } catch(Exception e) {
                e.printStackTrace();
        } finally {
            
        }
    }

    private static void test1() {
        int k = 0;
        Integer ij = 2000000000;
        for (int i = 0; i < ij; i ++) {
            k = k + 1;//
        }
        System.out.print("\nTest1 cycled " + ij.toString() + "times.");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    private static void test2( Integer ij) {
        int k = 0;
        for (int i = 0; i < ij; i ++) {
            k = k + 1;//
        }
        System.out.print("\nTest2 cycled " + ij.toString() + "times.");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    private static void test3( Integer ij) {
        int k = 0;
        for (int i = 0; i < ij; i ++) {
            k = k + 1;//
        }
        System.out.print("\nTest3 cycled " + ij.toString() + "times.");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
