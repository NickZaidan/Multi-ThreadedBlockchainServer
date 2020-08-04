import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class ServerInfoList {

    ArrayList<ServerInfo> serverInfos;

	public ServerInfoList() {
        serverInfos = new ArrayList<>();
    }

	//3 Step Process:
	//1: Fill up ServerInfoList with empty ServerInfos with specified size of latest servers.num command
	//2: Put data into each of the empty ServerInfos, filling up its data pairings
	//3: Check function at end which turns incomplete objects null

    public void initialiseFromFile(String filename) {
        // implement your code here
		ArrayList<String> lines = readFile(filename); //Reads the file and stores each line in an ArrayList
		fillingServerInfo(lines); //Determines how many empty objects should be added to the list

		if(serverInfos.size() == 0){ //If function doesn't increse size of server infos, then data was missing and should be left empty
			return;
		}
		fillingServerInfoData(lines); //Putting all ServerInfo objects into list whether it should or shouldn't
		cleaningServerInfo(); //Cleans out ServerInfo objects that shouldn't be
    }

    public ArrayList<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public void setServerInfos(ArrayList<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean addServerInfo(ServerInfo newServerInfo) {
        // implement your code here
		serverInfos.add(newServerInfo);
		return true;
	}

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) {
        // implement your code here
		serverInfos.set(index, newServerInfo);
		return true;
    }

    public boolean removeServerInfo(int index) {
        // implement your code here
		serverInfos.set(index, null);
		return true;
    }

    public boolean clearServerInfo() {
        // implement your code here
		serverInfos.clear();
		return true;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < serverInfos.size(); i++) {
            if (serverInfos.get(i) != null	) {
                s += "Server" + i + ": " + serverInfos.get(i).getHost() + " " + serverInfos.get(i).getPort() + "\n";
            }
        }
        return s;
    }

    // implement any helper method here if you need any

	//Reading file to ArrayList
	public ArrayList<String> readFile(String filename){
		ArrayList<String> lines = new ArrayList<String>();

		try{
			Scanner scan = new Scanner(new FileReader(filename));
			while(scan.hasNextLine()){
				String s = scan.nextLine();
				if(s.length() == 0){
					continue;
				}
				lines.add(s);
			}
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
		return lines;
	}

	//Step 1:
	public void fillingServerInfo(ArrayList<String> lines){
		int serverNumber = 0;
		for(int i = 0; i < lines.size(); i++){
				if(lines.get(i).contains("servers.num")){
					String[] split = lines.get(i).split("=");
					serverNumber = Integer.parseInt(split[1]);
				}
		}

		for(int i = 0; i < serverNumber; i++){
			ServerInfo t = new ServerInfo();
			t.setId(Integer.toString(i));
			serverInfos.add(t);
		}
	}

	//Step 2:
	public void fillingServerInfoData(ArrayList<String> lines){
		for(int i = 0; i < lines.size(); i++){
			String[] chunkedLine = lines.get(i).split("=");
			
			//Lines not needed
			if(lines.get(i).contains("servers.num")){ 
				continue;
			}
			else if(chunkedLine.length != 2){
				continue;
			}


			//We have the index
			String[] command = chunkedLine[0].split("\\.");
			int numberGiven =  Integer.parseInt(chunkedLine[0].replaceAll("[^0-9]", ""));


			if(numberGiven < serverInfos.size()){ //If the index is within the range
				if(command[1].equals("host")){
					serverInfos.get(numberGiven).setHost(chunkedLine[1]);
				}

				else if(command[1].equals("port")){
					serverInfos.get(numberGiven).setPort(Integer.parseInt(chunkedLine[1]));
				}
			}
		}
	}

	//Step 3:
	public void cleaningServerInfo(){
		for(int i = 0; i < serverInfos.size(); i++){
			if(serverInfos.get(i).isPaired() == false){
				serverInfos.set(i, null);
			}
		}
	}

	public void printId(){
		for(int i = 0; i < serverInfos.size(); i++){
			System.out.println(serverInfos.get(i).getId());
		}
	}
}
