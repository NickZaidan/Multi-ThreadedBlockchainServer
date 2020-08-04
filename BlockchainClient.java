import java.util.Scanner;
import java.util.ArrayList;

public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];
		BlockchainClient bc = new BlockchainClient();
        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);


        while (true) {
            String message = sc.nextLine();
            // implement your code here
			String[] chunked = message.split("\\|");
			
			//In the future I will use Switch Statements as I later that if else chains are less operation efficient then a Switch. Especially in this case  
			
			//For commands with no arguments
			if(chunked.length == 1){

				//Shutdown command
				if(chunked[0].equals("sd")){
					break;
				}

				//List command
				else if(chunked[0].equals("ls")){
					System.out.printf(pl.toString() + "\n");
				}

				//Clear command
				else if(chunked[0].equals("cl")){
					for(int i = 0; i < pl.getServerInfos().size(); i++){
						if(pl.getServerInfos().get(i) == null){
							pl.getServerInfos().remove(i);
							System.out.printf("Succeeded\n\n");
						}
					}
				}

				//Pb command with no arguments
				else if(chunked[0].equals("pb")){
					bc.broadcast(pl, message);
				}

				else{
					System.out.printf("Unknown Command\n\n");
				}
			}

			//For commands with arguments
			else if(chunked.length > 1){


				//Add command
				if(chunked[0].equals("ad")){
					if(chunked.length != 3){
						System.out.printf("Failed\n\n");
						continue;
					}
					String host = chunked[1];
					int port;
					try{
						port = Integer.parseInt(chunked[2]);
					}
					catch(NumberFormatException e){
						System.out.printf("Failed\n\n");
						continue;
					}
					pl.addServerInfo(new ServerInfo(host, port));
					System.out.printf("Succeeded\n\n");
				}

				//Remove command
				else if(chunked[0].equals("rm")){
					if(chunked.length != 2){
						System.out.printf("Failed\n\n");
						continue;
					}

					int index;

					try{
						index = Integer.parseInt(chunked[1]);
					}
					catch(NumberFormatException e){
						System.out.printf("Failed\n\n");
						continue;
					}

					if(index < 0 || index > pl.getServerInfos().size() - 1){
						System.out.printf("Failed\n\n");
						continue;
					}

					pl.removeServerInfo(index);
					System.out.printf("Succeeded\n\n");

				}


				//Update command
				else if(chunked[0].equals("up")){
					if(chunked.length != 4){
						System.out.printf("Failed\n\n");
						continue;
					}

					String hostname = chunked[2];
					int index;
					int port;

					try{
						index = Integer.parseInt(chunked[1]);
						port = Integer.parseInt(chunked[3]);
					}
					catch(NumberFormatException e){
						System.out.printf("Failed\n\n");
						continue;
					}

					pl.updateServerInfo(index, new ServerInfo(hostname, port));
					System.out.printf("Succeeded\n\n");
				}

				//Transaction command
				else if(chunked[0].equals("tx")){
					bc.broadcast(pl, message);
				}

				//Pb command with arguments
				else if(chunked[0].equals("pb")){
					if(chunked.length == 2){
						int serverNumber;
						try{
							serverNumber = Integer.parseInt(chunked[1]);
						}
						catch(NumberFormatException e){
							continue;
						}

						bc.unicast(serverNumber, pl.getServerInfos().get(serverNumber), message);
					}

					else if(chunked.length > 2){
						ArrayList<Integer> list = new ArrayList<Integer>();
						for(int i = 1; i < chunked.length; i++){
							list.add(Integer.parseInt(chunked[i]));
						}
						bc.multicast(pl, list, message);
					}
				}

				else{
					System.out.printf("Unknown Command\n\n");
				}
			}

        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        // implement your code here
		BlockchainClientRunnable bcr = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
		Thread thread;
		try{
			thread = new Thread(bcr);
			thread.start();
			thread.join();
		}
		catch(InterruptedException e){
			System.out.println(e);
		}

		String reply = bcr.getReply();

		if(bcr.getServerReachable() == false){
			reply = reply + "Server is not available\n\n";
		}
		System.out.print(reply);
    }

    public void broadcast (ServerInfoList pl, String message) {
        // implement your code here
		ArrayList<BlockchainClientRunnable> bcr = new ArrayList<BlockchainClientRunnable>();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for(int i = 0 ; i < pl.getServerInfos().size(); i++){
			if(pl.getServerInfos().get(i) != null){
				ServerInfo tmp = pl.getServerInfos().get(i);
				int index = i;
				String host = tmp.getHost();
				int port = tmp.getPort();

				bcr.add(new BlockchainClientRunnable(index, host, port, message));
				threads.add(new Thread(bcr.get(bcr.size() - 1)));
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

			reply = reply + bcr.get(i).getReply();

			if(bcr.get(i).getServerReachable() == false){
				reply = reply + "Server is not available\n\n";
			}
		}
		System.out.printf(reply);

    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) {
        // implement your code here
		ArrayList<BlockchainClientRunnable> bcr = new ArrayList<BlockchainClientRunnable>();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for(int i = 0 ; i < serverIndices.size(); i++){
			int serverIndex = serverIndices.get(i);

			ServerInfo tmp = serverInfoList.getServerInfos().get(serverIndex);
			String host = tmp.getHost();
			int port = tmp.getPort();

			bcr.add(new BlockchainClientRunnable(i, host, port, message));
			threads.add(new Thread(bcr.get(i)));
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

			reply = reply + bcr.get(i).getReply();

			if(bcr.get(i).getServerReachable() == false){
				reply = reply + "Server is not available\n\n";
			}
		}
		System.out.printf(reply);
    }

    // implement any helper method here if you need any

}
