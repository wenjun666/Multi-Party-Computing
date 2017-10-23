import java.io.DataInputStream; 
import java.io.PrintStream; 
import java.io.IOException; 
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.net.Socket; 
import java.net.ServerSocket; 
import java.util.*; 
 
/*
 * A server (actually rounter) that delivers public and private messages.
 * public messages are sent through braodcast(), private messages are sent through unicast().
 */ 
public class Server_MPC { 
  // Create a socket for the server  
  private static ServerSocket serverSocket = null; 
  // Create a socket for the user  
  private static Socket userSocket = null; 
  // Maximum number of users  
  private static int maxUsersCount = 5; 
  // An array of threads for users 
  private static userThread[] threads = null; 
   
  private static int userCount = 0; 
   
  private static int connectedUser=0; 
   
  public static int findIndex() 
  { 
    // Function that finds a null thread and asign to new user. 
    for(int i=0; i < maxUsersCount; ++i) 
    { 
      if (threads[i] == null) 
      { 
        return i; 
      } 
    } 
    return maxUsersCount; 
  } 
  
    // counts currently joined user number. For server determining if all users have joined the event and send READY message. 
  public static int userCount(){ 
    int result =0; 
    for(int i=0; i<maxUsersCount;i++){ 
      if (threads[i] != null){ 
        result +=1; 
      } 
       
    } 
    return result; 
  } 
   
  public static void main(String args[]) { 
    System.out.println("Server Started"); 
    // The default port number. 
    if (args.length < 1) 
    { 
      System.out.println("Invalid number of arguments. Please provide at least port number as argument"); 
      System.exit(1); 
    } 
    int portNumber = Integer.parseInt(args[0]);  
     
    // Override the value of maxUsersCount if provided. 
    if (args.length == 2) 
    { 
      maxUsersCount = Integer.parseInt(args[1]); 
    } 
    /* 
     * Create and assign a new thread for new user. 
     */ 
    try { 
      serverSocket = new ServerSocket (portNumber); 
    } catch (Exception e) 
    { 
      System.out.println("Exception creating Server Socket" + e.getMessage()); 
    } 
    threads = new userThread[maxUsersCount]; 
     
     
     
    while (true) { 
      int index = findIndex(); 
      if ( index < maxUsersCount ) 
      { 
        try { 
          userSocket = serverSocket.accept(); 
          connectedUser = userCount()+1; 
          threads[index] = new userThread(userSocket, threads,index, connectedUser); 
          threads[index].start(); 
          System.out.println("new user joined"); 
          System.out.println("user id is " + index); 
           
        } 
        catch (Exception e) { 
          System.out.println("Exception creating threads" + e.getMessage() ); 
        } 
      } 
      else { 
         
      } 
    } 
  } 
} 
 
 
class userThread extends Thread{ 
   
  private String userName = null; 
  private BufferedReader input_stream = null; 
  private PrintStream output_stream = null; 
  private Socket userSocket = null; 
  private final userThread[] threads; 
  private int index=0; 
  private int connectedUser=0; 
  private int maxUsersCount; 
   
  public userThread(Socket userSocket, userThread[] threads, int index, int connectedUser) { 
    this.userSocket = userSocket; 
    this.threads = threads; 
    this.maxUsersCount = threads.length; 
    this.index = index; 
    this.connectedUser = connectedUser; 
  } 
   
   
  public void writeToOutputStream ( String message) 
  { 
    synchronized(this) { 
      try  
      { 
        output_stream.println(message); 
      } 
      catch (Exception e) 
      { 
        System.out.println("Exception writing to output_stream" + index + e.getMessage()); 
      } 
    } 
  } 
   
  public void broadcast(String message) 
  { 
    synchronized (this) { 
      for (int i = 0; i < maxUsersCount; i++ ) 
      { 
        userThread thread = threads[i]; 
        if (thread == null ) 
        { 
        } 
        else  
        { 
          try { 
            threads[i].writeToOutputStream (message); 
             
          } 
          catch (Exception e) 
          { 
            System.out.println("Exception broadcasting message"); 
          } 
        } 
      } 
    } 
  } 
   
   
  void unicast (String toUser, String message ) 
  { 
    synchronized (this) 
    { 
       
      System.out.println("unicasting"); 
      try { 
         
        threads[Integer.parseInt(toUser)].output_stream.println( message); 
         
      } catch(Exception e) 
      { 
         
      } 
       
    } 
  } 
   
   
   
  public void run() { 
    String userMessage; 
     
    try { 
      input_stream = new BufferedReader(new InputStreamReader (userSocket.getInputStream()) ); 
      output_stream = new PrintStream ( userSocket.getOutputStream() ); 
       
    } 
    catch (IOException e) 
    { 
      System.out.println("Cannot get either Socket input or outputStream of user" + e.getMessage()); 
    } 
     
    while (true) 
    { 
      // variable to store messages to be relayed such as Would you like to be friends. 
      String message; 
      try { 
        userMessage = input_stream.readLine(); 
        System.out.println(userMessage); 
        //output_stream.println(userMessage); 
         
     
        if(userMessage.length() !=0){ 
           
          if (userMessage.charAt(0) == '@'){ 
            int indexOfSpace = userMessage.indexOf(' '); 
            String unicastUser = userMessage.substring(1,indexOfSpace); 
            String unicastMessage = userMessage.substring(indexOfSpace+1); 
            unicastMessage = "FROM"+Integer.toString(index)+" "+unicastMessage; 
            System.out.println(unicastMessage); 
            unicast(unicastUser , unicastMessage ); 
          } 
          else if(userMessage.equals("HELLOSERVER")){ 
            //userCount
              // server tells user who he is. 
            unicast(Integer.toString(index),"ID"+Integer.toString(index)); 
             System.out.println("Connected:" + Integer.toString(connectedUser)); 
              System.out.println("maxUsersCount:" + Integer.toString(maxUsersCount)); 
            if(connectedUser == maxUsersCount){
                // when all users are joined, broadcast READY. 
 
              broadcast ("READY"); 
              System.out.println("READY"); 
            } 
          } 
          else{ 
          broadcast (userMessage); 
          } 
        } 
         
      } catch (Exception e) 
      { 
        // If a user disconnects the service this exception will occur 
        // So we break out of the while loop so that we do not have infinite loop 
        System.out.println("Exception occurred" + index + e.getMessage()); 
       
         
        break; 
      } 
    } 
    try { 
      synchronized(this) 
      { 
        removeFromUserThread(); 
      } 
      userSocket.close(); 
       
    } 
    catch (Exception e) 
    { 
      System.out.println("Cannot close userSocket for user " + index + " " + e.getMessage()); 
    } 
  } 
   
   
   
  // sets the threads[i] of current user to null when the thread has to end 
  public void removeFromUserThread() 
  { 
    synchronized(this) 
    { 
      for(int i=0; i < maxUsersCount; ++i) 
      { 
        if (threads[i] == this ) 
        { 
          threads[i] = null; 
        } 
      } 
    } 
  } 
   
   
   
} 
 
