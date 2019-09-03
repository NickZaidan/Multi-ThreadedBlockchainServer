import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.net.*;
public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];


		ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);
		BlockchainClient bc = new BlockchainClient();

        while (true) {
            String message = sc.nextLine();
            // implement your code here


			//sd command
			if(message.equals("sd")){
				break; //exit loop which kills client
			}

			//ls command
			else if(message.equals("ls")){
				System.out.printf(pl.toString() + "\n");
			}


			//ad command
			else if(message.charAt(0) == 'a' && message.charAt(1) == 'd'){
				String[] splitted = message.split("\\|");
				if(splitted.length != 3 && !splitted[0].equals("ad")){
					System.out.printf("Failed\n\n");
				}
				else{
					bc.adFunction(splitted[1], Integer.parseInt(splitted[2]), pl);
				}
			}


			//rm command
			else if(message.charAt(0) == 'r' && message.charAt(1) == 'm'){
				String[] splitted = message.split("\\|");
				if(splitted.length != 2 && !splitted[0].equals("rm")){
					System.out.printf("Failed\n\n");
				}
				else{
					bc.rmFunction(Integer.parseInt(splitted[1]), pl);
				}
			}

			//up command
			else if(message.charAt(0) == 'u' && message.charAt(1) == 'p'){
				String[] splitted = message.split("\\|");
				if(splitted.length != 4 && !splitted[0].equals("up")){
					System.out.printf("Failed\n\n");
				}
				else{
					bc.upFunction(Integer.parseInt(splitted[1]), splitted[2], Integer.parseInt(splitted[3]), pl);
				}
			}


			//cl command
			else if(message.equals("cl")){
				bc.clFunction(pl);
			}

			//tx command
			else if(message.charAt(0) == 't' && message.charAt(1) == 'x'){
				bc.broadcast(pl, message);
			}

			//pb command
			else if(message.charAt(0) == 'p' && message.charAt(1) == 'b'){
				if(message.equals("pb")){
					bc.broadcast(pl, message);
				}
				else{
					String[] splitted = message.split("\\|");
					if(splitted.length == 2){
						bc.unicast(Integer.parseInt(splitted[1]), pl.getServerInfos().get(Integer.parseInt(splitted[1])) ,message);
					}
					else if (splitted.length > 2){
						ArrayList<Integer> serverIndices = new ArrayList<Integer>();
						for(int i = 1; i < splitted.length; i++){
							serverIndices.add(Integer.parseInt(splitted[i]));
						}
						bc.multicast(pl, serverIndices, message);
					}

				}
			}

			//Invalid command
			else{
				System.out.printf("Unknown Command\n\n");
			}
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        // implement your code here
		BlockchainClientRunnable r = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
		try{
			Thread t = new Thread(r);
			t.start();
			t.join();
		}
		catch(InterruptedException e){
			System.out.println(e);
		}
		String reply = r.getReply();
		if(r.getServerReachable() == true){
			reply += "Server is not available\n\n";
		}
		System.out.printf(reply);

		return;
    }

    public void broadcast (ServerInfoList pl, String message) {
        // implement your code here
		ArrayList<BlockchainClientRunnable> r = new ArrayList<BlockchainClientRunnable>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(int i = 0 ; i < pl.getServerInfosSize(); i++){
			if(pl.getServerInfos().get(i) != null){
				r.add(new BlockchainClientRunnable(i, pl.getServerInfos().get(i).getHost(), pl.getServerInfos().get(i).getPort(), message));
				threads.add(new Thread(r.get(r.size() - 1)));
			}
		}

		for(int i = 0; i < threads.size(); i++){
			threads.get(i).start();
		}
		String reply = "";
		for(int i = 0; i < threads.size(); i++){
			try{
				threads.get(i).join();
			}
			catch(InterruptedException e){
				System.out.println(e);
			}
			reply += r.get(i).getReply();
			if(r.get(i).getServerReachable() == true){
				reply += "Server is not available\n\n";
			}
		}
		System.out.printf(reply);
		return;
    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) {
        // implement your code here


		ArrayList<BlockchainClientRunnable> r = new ArrayList<BlockchainClientRunnable>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(int i = 0 ; i < serverIndices.size(); i++){
			int serverIndex = serverIndices.get(i);
			r.add(new BlockchainClientRunnable(i, serverInfoList.getServerInfos().get(serverIndex).getHost(), serverInfoList.getServerInfos().get(serverIndex).getPort(), message));
			threads.add(new Thread(r.get(i)));
		}

		for(int i = 0; i < threads.size(); i++){
			threads.get(i).start();
		}

		String reply = "";
		for(int i = 0; i < threads.size(); i++){
			try{
				threads.get(i).join();
			}
			catch(InterruptedException e){
				System.out.println(e);
			}
			reply += r.get(i).getReply();
			if(r.get(i).getServerReachable() == true){
				reply += "Server is not available\n\n";
			}
		}
		System.out.printf(reply);
		return;


    }

    // implement any helper method here if you need any

	public void adFunction(String host, int port, ServerInfoList pl){
		if(port < 1024 || port > 65535){
			System.out.printf("Failed\n\n");
			return;
		}
		else{
			pl.addServerInfo(new ServerInfo(host, port));
			System.out.printf("Succeeded\n\n");
		}
	}

	public void rmFunction(int index, ServerInfoList pl){
		if(index < 0 || index > pl.getServerInfosSize()){
			System.out.printf("Failed\n\n");
			return;
		}

		pl.removeServerInfo(index);
		System.out.printf("Succeeded\n\n");
	}

	public void upFunction(int index, String host, int port, ServerInfoList pl){
		if(index > pl.getServerInfosSize()){
			System.out.printf("Failed\n\n");
			return;
		}

		if(port < 1024 || port > 65535){
			System.out.printf("Failed\n\n");
			return;
		}

		pl.updateServerInfo(index, new ServerInfo(host, port));
		System.out.printf("Succeeded\n\n");
	}


	public void clFunction(ServerInfoList pl){
		for(int i = 0; i < pl.getServerInfosSize(); i++){
			if(pl.getServerInfos().get(i) == null){
				pl.getServerInfos().remove(i);
			}
		}
		System.out.printf("Succeeded\n\n");
	}
}
