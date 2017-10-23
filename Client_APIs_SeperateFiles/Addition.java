package com.example.wenjun.client1;

/**
 * Created by lindsay on 12/10/16.
 */

import android.util.Log;

import java.util.Scanner;
import java.util.Random;


public class Addition {


    public int x; // value
    public int p; // prime
    public int n; // participants number

    public Addition() {

    }

    public Addition(int x, int p, int n) {
        this.x = x;
        this.p = p;
        this.n = n;
    }

    public void setValue(int new_x, int new_p, int new_n) {
        x = new_x;
        p = new_p;
        n = new_n;
    }

    public int[] shares() {
        int[] r = new int[n];
        Random rand = new Random();
        int sum = 0;
        for (int i = 0; i < r.length - 1 ; i++) {
            r[i] = (rand.nextInt(p)) % p;
            Log.i("share ", Integer.toString(i)+Integer.toString(r[i]));
            sum += r[i];
            sum = sum%p;
        }
        Log.i("p is ", Integer.toString(p));

        r[r.length - 1] = (((x - sum) % p)+p)%p;
        Log.i("last share ", Integer.toString(r[r.length-1]));
        return r;
    }

    /*public static void main(String[] args) {

        Addition a = new Addition();
        a.setValue(1, 10000457, 6); // this is the field that need to be changed
        int[] r = a.shares();
        int sum = 0;
        for (int i = 0; i < r.length; i++) {
            System.out.println(r[i]);
            sum += r[i];
        }
        System.out.println(sum % a.p);
    }*/

    // r is the result

    public int combine(String[] shares){
        int result = 0;
        for (int i = 0; i < shares.length; i++){
            result += Integer.parseInt(shares[i]);
            result = result%p;
        }
        Log.i("p is now ", Integer.toString(p));
        Log.i("result: " , Integer.toString(result%p));
        return (result%p);
    }



}
