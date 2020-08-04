import java.net.*;
import java.io.*;

public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }

		int portNumber;

		try{
			portNumber = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e){
			System.out.println(e);
			return;
		}


		if(portNumber < 1024 || portNumber > 65535){
			return;
		}
        Blockchain blockchain = new Blockchain();


        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        // implement your code here
		try{
			ServerSocket s = new ServerSocket(portNumber);
			while(true){
				Socket c = s.accept();
				BlockchainServerRunnable r = new BlockchainServerRunnable(c, blockchain);
				Thread t = new Thread(r);
				t.start();
			}
		}
		catch(IOException e){
			System.out.println(e);
		}

        pcr.setRunning(false);

		try{
			pct.join();
		}
		catch(InterruptedException e){
			System.out.println(e);
		}
    }

    // implement any helper method here if you need any
}
