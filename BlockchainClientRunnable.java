import java.net.*;
import java.io.*;

public class BlockchainClientRunnable implements Runnable {

    private String reply;
	private int serverNumber;
	private String serverName;
	private int portNumber;
	private String message;
	private boolean isReachable = true;

    public BlockchainClientRunnable(int serverNumber, String serverName, int portNumber, String message) {
        this.reply = "Server" + serverNumber + ": " + serverName + " " + portNumber + "\n"; // header string
		this.serverNumber = serverNumber;
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.message = message;
    }

    public void run() {
        // implement your code here
		try{
			Socket s = new Socket(serverName, portNumber);
			clientHandler(s.getInputStream(), s.getOutputStream());
			s.close();
		}
		catch(IOException e){
			isReachable = false;
		}
    }

    public String getReply() {
        return reply;
    }

    // implement any helper method here if you need any

	public void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream){
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
		PrintWriter outWriter = new PrintWriter(serverOutputStream, true);
		String output = "";

		outWriter.println(message);

		try{
			Thread.sleep(200);
		}
		catch(InterruptedException e){
			System.out.println(e);
		}

		try{
			String printer;
			while(((printer = inputReader.readLine()) != null)){
				output = output + printer + "\n";
				if(inputReader.ready() == false){
					break;
				}
			}

		}
		catch(IOException e){
			e.printStackTrace();
		}
		if(isReachable == true){
			reply = reply + output;
		}
		else{
			reply = reply + "Server is not available\n\n";
		}

		outWriter.println("cc");

		try{
			inputReader.close();
			outWriter.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}

	public boolean getServerReachable(){
		return isReachable;
	}
}
