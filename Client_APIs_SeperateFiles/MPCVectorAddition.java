package com.example.wenjun.client1;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.concurrent.Semaphore;

/**
 * Created by lindsay on 12/1/16.
 */

public class MPCVectorAddition {
    private int secret;
    private Socket socket;
    private int SERVERPORT;
    private String SERVER_IP;
    private String serverMessage;
    private KeyPair keyPair;
    private String strippedKey;
    private String publicKey;
    private int myID;
    static Semaphore connected;
    static Semaphore publicKeyReceived;
    static Semaphore shareReceivedFlag;
    static Semaphore combineShareReceivedFlag;

    static Semaphore lastElementSent;


    // this should be the same as number of parties.
    private int numParties;
    private int numPubKeyReceived;
    private int numShareReceived;
    private int combinedShareReceived;
    private String[] partyPublicKeys;

    private String[] shareReveived;
    private String[] combineReceived;
    private int[] distance;
    private int[] result;
    private int singleResult;


    private int combined;

    //private int result;

    public MPCVectorAddition() {

    }

    public MPCVectorAddition(int SERVERPORT, String SERVER_IP, int numParties, int[] distance) {

        this.SERVERPORT = SERVERPORT;
        this.SERVER_IP = SERVER_IP;
        this.numParties = numParties;
        this.numPubKeyReceived = 0;
        this.numShareReceived = 0;
        this.combinedShareReceived = 0;
        this.partyPublicKeys = new String[numParties];
        this.shareReveived = new String[numParties];
        this.combineReceived = new String[numParties];
        // use it when encrypting: RSA.encryptWithKey(Crypto.stripPublicKeyHeaders(Crypto.writePublicKeyToPreferences(keyPair)),message);
        this.keyPair = RSA.generate();
        // use it when decrypting: RSA.decryptFromBase64(Crypto.getRSAPrivateKeyFromString(strippedKey), encryptedMessage)
        this.publicKey = Crypto.stripPublicKeyHeaders(Crypto.writePublicKeyToString(keyPair));
        this.strippedKey = Crypto.stripPrivateKeyHeaders(Crypto.writePrivateKeyToString(keyPair));

        this.distance = distance;

        /*
            semaphores for enabling stable concurrency.
            connected: when all users are connected, server respond READY, and only then will hthe process begin.
            publicKeyReceived: when all users receive pubic key of others, they can proceed to share their secrets.
            shareReceivedFalg: when all users receive others' shares. Only then can they conbine shares and begin nexr phase.
            combineShareReceivedFlag: when all users receive combined shares from each other. Then begin calculating final result.
            lastElementSent: when received last element of vector, begin next secret addition round.
         */
        this.connected = new Semaphore(0);
        this.publicKeyReceived = new Semaphore(0);
        this.shareReceivedFlag = new Semaphore(0);
        this.combineShareReceivedFlag = new Semaphore(0);
        this.lastElementSent = new Semaphore(0);

        // this is the result vector to be returned.
        this.result = new int[distance.length];

        // this is for storing single secret addition element.
        this.singleResult = 0;

    }



    public int[] sendMessage(){
        new Thread(new SendKeyThread("HELLOSERVER")).start();
        try {
            connected.acquire();
        }catch (InterruptedException e){

        }
        Log.i("Reached ", "Hereeeeeeeeee");
        new Thread(new ClientThread()).start();
        try {
            publicKeyReceived.acquire();
        }catch (InterruptedException e){

        }
        /*
        // share secret.
        */


        for(int j = 0; j < distance.length; j++) {
            Addition a = new Addition(distance[j], 10000457, numParties);
            int[] sharedSecret = a.shares();

            // share ith share to party i.
            for (int i = 0; i < numParties; i++) {
                String ithshare = Integer.toString(sharedSecret[i]);
                Log.i("sent share", ithshare);
                if(i==myID){
                    shareReveived[i] = ithshare;
                }else {
                    // encrypt this share using party i's public key.
                    String encryptedMes = RSA.encryptWithKey(partyPublicKeys[i], ithshare);
                    // better for rounting - styip out new lines. Thus the key would not be seperated into many messages.
                    encryptedMes = Crypto.stripPrivateKeyHeaders(encryptedMes);

                    new Thread(new SendKeyThread("@" + Integer.toString(i) + " " + encryptedMes)).start();
                }
            }

            try {
                shareReceivedFlag.acquire();
            }catch (InterruptedException e){

            }
        /*
        // combine shares received.
        */
            //Addition combineA = new Addition();
            combined = a.combine(shareReveived);

        /*
        // broadcast combined share.
        */
            for (int i = 0; i < numParties; i++) {


                String combieMes = Integer.toString(combined);
                if(i==myID){
                    combineReceived[i] = combieMes;
                }else {
                    String encryptedMes = RSA.encryptWithKey(partyPublicKeys[i], combieMes);
                    encryptedMes = Crypto.stripPrivateKeyHeaders(encryptedMes);
                    Log.i("sent combined share", combieMes);
                    new Thread(new SendKeyThread("@" + Integer.toString(i) + " " + encryptedMes)).start();
                }
            }

            try {
                combineShareReceivedFlag.acquire();
            }catch (InterruptedException e){

            }
        /*
        // show result.
        */


            this.singleResult = a.combine(combineReceived);

            result[j] = singleResult;
            Log.i("Result: ", Integer.toString(this.singleResult));

            this.numPubKeyReceived = 0;
            this.numShareReceived = 0;
            this.combinedShareReceived = 0;

            this.shareReveived = new String[numParties];
            this.combineReceived = new String[numParties];

            //this.secret = secret;

            this.singleResult = 0;

            this.lastElementSent.release();



        }
        return result;

    }

    class SendKeyThread implements Runnable {
        private String message;

        public SendKeyThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                if (socket == null) {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    socket = new Socket(serverAddr, SERVERPORT);
                    //connected = true;
                    connected.release();
                    Log.i("Socoket was null", "Socket was nulllllllllll");
                }else {
                    connected.release();
                }
                Log.i("debuggggg", String.valueOf(socket == null));
                //EditText et = (EditText) findViewById(R.id.EditText01);
                //String str = et.getText().toString();
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                /*
                //used when sending encrypted message.
                encryptedMessage = RSA.encryptWithKey(Crypto.stripPublicKeyHeaders(Crypto.writePublicKeyToPreferences(keyPair)),str);
                out.println(encryptedMessage);
                 */

                /*
                //used when sending unencryted key.
                 */
                out.println(message);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            String tag = "debug";
            try {
                if (socket == null) {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    socket = new Socket(serverAddr, SERVERPORT);
                }
                Log.i("Thread called", "hereeeeeee");
                BufferedReader input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                // accept server message for ID

                while (true) {
                    serverMessage = input_stream.readLine();
                    if (serverMessage.contains("ID")) {
                        myID = Integer.parseInt(serverMessage.substring(2, 3));
                        Log.i("ID: ", Integer.toString(myID));
                        break;
                    }
                }
                // wait for ready: every one is joined to the server.
                while (true) {
                    serverMessage = input_stream.readLine();
                    if (serverMessage!=null && serverMessage.contains("READY")) {

                        Log.i("ready.", "Server respond");
                        break;
                    }
                }

                /*
                    now start the key exchange phase.
                */

                // first send our public key to let server braodcast it.
                out.println("PUB" + Integer.toString(myID) + " " + publicKey);
                // if we have the ID, we can now reveive other users' public key.
                while (numPubKeyReceived < numParties) {
                    serverMessage = input_stream.readLine();
                    if (serverMessage != null && serverMessage.contains("PUB")) {
                        //returnText.setText(serverMessage);
                        Log.d(tag, serverMessage);


                        //parse public key
                        int indexofspace = serverMessage.indexOf(" ");
                        int parti = Integer.parseInt(serverMessage.substring(3, indexofspace));
                        String pubKey = serverMessage.substring(indexofspace + 1);
                        partyPublicKeys[parti] = pubKey;
                        numPubKeyReceived++;
                        Log.i("public key", "public key of party " + Integer.toString(parti) + " is " + pubKey);



                    }
                }


                publicKeyReceived.release();


                // for each round
                while(true) {

                    Log.i("numshare received", Integer.toString(numShareReceived) + " " + Integer.toString(numParties));
                    while (numShareReceived < (numParties-1)) {
                        serverMessage = input_stream.readLine();
                        if (serverMessage != null && serverMessage.contains("FROM")) {
                            int indexofspace = serverMessage.indexOf(" ");
                            int parti = Integer.parseInt(serverMessage.substring(4, indexofspace));
                            String share = serverMessage.substring(indexofspace + 1);
                            try {
                                String decrypted = RSA.decryptFromBase64(Crypto.getRSAPrivateKeyFromString(strippedKey), share);
                                shareReveived[parti] = decrypted;

                                Log.i("share", "FROM" + Integer.toString(parti) + " is " + decrypted);
                                numShareReceived++;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }

                    numShareReceived = 0;

                    shareReceivedFlag.release();


                    while (combinedShareReceived < (numParties-1)) {
                        serverMessage = input_stream.readLine();
                        if (serverMessage != null && serverMessage.contains("FROM")) {
                            int indexofspace = serverMessage.indexOf(" ");
                            int parti = Integer.parseInt(serverMessage.substring(4, indexofspace));
                            String share = serverMessage.substring(indexofspace + 1);
                            try {
                                String decrypted = RSA.decryptFromBase64(Crypto.getRSAPrivateKeyFromString(strippedKey), share);
                                combineReceived[parti] = decrypted;

                                Log.i("Combine Share", "FROM" + Integer.toString(parti) + " is " + decrypted);
                                combinedShareReceived++;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }

                    combinedShareReceived = 0;
                    combineShareReceivedFlag.release();

                    try {
                        lastElementSent.acquire();
                    }catch (InterruptedException e){

                    }


                }



            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();

            }
        }
    }
}
