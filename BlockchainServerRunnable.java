import java.net.*;
import java.io.*;

public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;


	//I added this
	private Blockchain getBlockchain(){
		return this.blockchain;
	}

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain) {
        // implement your code here
		this.clientSocket = clientSocket;
		this.blockchain = blockchain;
    }

    public void run() {
        // implement your code here
		try{
			serverHandler(clientSocket.getInputStream(),clientSocket.getOutputStream());
			clientSocket.close();
		}

		catch(IOException e){
			System.out.println(e);
		}
    }


    // implement any helper method here if you need any


	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream)); //THIS IS READING USER INPUT FROM CLIENT
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true); //THIS IS FOR SENDING OUTPUT TO CLIENT

		try{
			String str = "";
			while((str = inputReader.readLine()) != null){
				if(str.equals("pb")){
					outWriter.print(getBlockchain().toString() + "\n"); //If pb was sent
					outWriter.flush();
				}

				else if(str.charAt(0) == 't' && str.charAt(1) == 'x'){
					boolean checker = getBlockchain().addTransaction(str);
					if(!checker){
						outWriter.print("Rejected\n\n");
						outWriter.flush();
					}
					else{
						outWriter.println("Accepted\n");
						outWriter.flush();
					}
				}
				else if(str.equals("cc")){
					break;
				}
				else{
					outWriter.print("Error\n\n");
					outWriter.flush();
				}
			}

			//Close the datastreams
			outWriter.close();
			inputReader.close();

		}
		catch(IOException e){
			System.out.println(e);
		}

    }
}
