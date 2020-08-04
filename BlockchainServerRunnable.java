import java.net.*;
import java.io.*;

public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain) {
        // implement your code here
		this.clientSocket = clientSocket;
		this.blockchain = blockchain;
    }

    public void run() {
        // implement your code here
		try{
			serverHandler(clientSocket.getInputStream(), clientSocket.getOutputStream());
			clientSocket.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
    }


    // implement any helper method here if you need any


	public Socket getClientSocket(){
		return this.clientSocket;
	}

	public void setSocket (Socket clientSocket){
		this.clientSocket = clientSocket;
	}

	public Blockchain getBlockchain(){
		return this.blockchain;
	}

	public void setBlockchain(Blockchain blockchain){
		this.blockchain = blockchain;
	}

	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);
		// implement your code here.

		try{ //Stolen from Assignment 1
			String input;

			//Main Server Logic
			while(((input = inputReader.readLine()) != null)){
				String[] chunked = input.split("\\|");


				if(input.equals("cc")){ //If cc is called
					return;
				}

				if(input.equals("pb")){ //If pb is called
					outWriter.print(getBlockchain().toString() + "\n\n");
				}

				else if(chunked[0].equals("tx")){ //if tx is called
					boolean x = getBlockchain().addTransaction(input);
					if(x){
						outWriter.print("Accepted\n\n\n");
					}
					else{
						outWriter.print("Rejected\n\n\n");

					}
				}

				else{
					outWriter.print("Error\n\n\n");
				}
				outWriter.flush();
			}
			inputReader.close();
			outWriter.close();
		}

		catch(IOException e){
			e.printStackTrace();
		}

	}
}
