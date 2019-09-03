import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BlockchainClientRunnable implements Runnable {

    private String reply;
	private String serverName;
	private int portNumber;
	private String message;
	private Socket socket;
	private boolean serverUnreachable = false;

    public BlockchainClientRunnable(int serverNumber, String serverName, int portNumber, String message) {
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.message = message;
        this.reply = "Server" + serverNumber + ": " + serverName + " " + portNumber + "\n"; // header string
    }

    public void run() {
        // implement your code here
		try{
			socket = new Socket(serverName, portNumber);
			clientHandler(socket.getInputStream(), socket.getOutputStream());
			socket.close();
		}
		catch(IOException e){
			this.serverUnreachable = true;
		}
    }

    public String getReply() {
        return reply;
    }

	// implement any helper method here if you need any

	public void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream){
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
		PrintWriter pr = new PrintWriter(serverOutputStream, true);
		String buffer = "";
		String output = "";
		pr.println(this.message);

		try{
			Thread.sleep(200);
		}

		catch(InterruptedException e){
			System.out.println(e);
		}
		try{
			while(inputReader .ready() && (buffer = inputReader.readLine()) != null){
				output += buffer + "\n";
			}
		}
		catch(IOException e){
			System.out.println(e);
		}

		if(serverUnreachable == false){
			this.reply += output;
		}

		else{
			this.reply += "Server is not available\n\n";
		}

		pr.println("cc");

		try{
			inputReader.close();
		}

		catch(IOException e){
			System.out.println(e);
		}
		pr.close();
	}

	public boolean getServerReachable(){
		return this.serverUnreachable;
	}

}
