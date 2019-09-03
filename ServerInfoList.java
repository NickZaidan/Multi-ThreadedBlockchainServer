import java.util.ArrayList;
import java.io.*;

public class ServerInfoList {

    ArrayList<ServerInfo> serverInfos;

    public ServerInfoList() {
        serverInfos = new ArrayList<>();
    }

    public void initialiseFromFile(String filename) {
        // implement your code here

		int serverNumber = 0; //Default server number
		String line = "";
		try{
			FileReader f = new FileReader(filename); //Read from file
			BufferedReader r = new BufferedReader(f); //For reading each line
			ServerInfo tmp;
			boolean pairCheck = false;
			int serverNumberHolder = -999;
			String hostName = null;
			int portNumber = -999;
			while((line = r.readLine()) != null){
				if(line.contains("servers.num=") == true){
					//Regex was made through trial and error, with this for help: https://www.tutorialspoint.com/java/java_regular_expressions.htm
					serverNumber = Integer.parseInt(line.replaceAll("[^0-9]", ""));

					int difference = serverNumber - getServerInfosSize();

					if(difference > 0){	//If the server number is more than the already existing size, add null entries
						for(int i = 0; i < difference; i++){
							addServerInfo(null);
						}
					}

					else if(difference < 0){ //If the new server number is less than the already existing size, trim elements from the end
						//I'm going to ignore this case and see what happens!
					}
				}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else{

					//Okay this is a clusterfuck, lets break this down
					String[] parts = line.split("="); //Breaking string into two parts based around the '=' sign
					int numberGiven =  Integer.parseInt(parts[0].replaceAll("[^0-9]", "")); //This is the number assigned by server0.host or server0.port. This is needed to tell where to put the ServerInfo object
					if(numberGiven >= getServerInfosSize()){ //If the number doesn't exist as defined in server.num, just set everything to default and move onwards
						pairCheck = false;
						portNumber = -999;
						continue;
					}

					//We want to see if the commands inputted are done in pairs, otherwise the entry needs to be null. So I did this in the most confusing way possible!
					//ServerNmberHolder is the way we see if the server number has been repeated. -999 is the default since the server cannot be that number. So if its set to default, we can check the next time
					//Upon new pair to be tested, set the number being held to be the number given
					if(serverNumberHolder == -999){
						serverNumberHolder = numberGiven;
					}
					//If there is already a number being held, then we need to see if its equal to the number from line previous
					else if(serverNumberHolder != -999){

						//If the number being held is the same as the number being given
						if(numberGiven == serverNumberHolder){
							pairCheck = true; //This means that it will be added to the arraylist
							serverNumberHolder = -999; //reset pair counter as we don't want to check for this element again
						}
						else{
							serverNumberHolder = numberGiven; //Otherwise just continue as they weren't a pair
						}
					}
					//System.out.println("Part[0]: " + parts[0] + " | Part[1]: " + parts[1]);
					if(parts.length != 2){ //If there isn't 2 things in part, set all to default
						pairCheck = false;
						portNumber = -999;
						serverInfos.set(numberGiven, null);
						continue;
					}
					if(parts[0].contains("host")){ //If the line is determining the host

						if(parts[1] == null || parts[1] == ""){
							pairCheck = false;
							portNumber = -999;
							continue;
						}

						hostName = parts[1]; //Set it equal to whats written
						hostName = hostName.replaceAll("\\s",""); //Strip out any whitespace
					}

					if(parts[0].contains("port")){ //If the line is determining the port
						portNumber = Integer.parseInt(parts[1]);
						if(portNumber < 1024 || portNumber > 65535){ //Ensure number is in correct range
							System.out.println("Invalid port"); //If its not type Invalid Port
							pairCheck = false;
							portNumber = -999; //Reset port number to its default
							serverInfos.set(numberGiven, null);
							continue;
						}
					}
					if(pairCheck == true && portNumber != -999){ //If the pair is checked, and the number isn't the default
						if(hostName == null || hostName == ""){
							serverInfos.set(numberGiven, null);
							continue;
						}
						tmp = new ServerInfo(hostName, portNumber); //Create the object
						serverInfos.set(numberGiven, tmp); //Set it to the index
						pairCheck = false; //Reset pair check and begin again
					}

				}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
		}

		catch(IOException e){
			System.out.println(e);
		}
    }

    public ArrayList<ServerInfo> getServerInfos() {
        return serverInfos;
    }


    public void setServerInfos(ArrayList<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean addServerInfo(ServerInfo newServerInfo) {
        getServerInfos().add(newServerInfo);
		return true;
    }

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) {
        // implement your code here
		getServerInfos().set(index, newServerInfo);
		return false;
    }

    public boolean removeServerInfo(int index) {
        // implement your code here
		getServerInfos().set(index, null);
		return true;
    }

    public boolean clearServerInfo() {
        // implement your code here
		serverInfos = new ArrayList<>();
		return true;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < serverInfos.size(); i++) {
            if (serverInfos.get(i) != null) {
                s += "Server" + i + ": " + serverInfos.get(i).getHost() + " " + serverInfos.get(i).getPort() + "\n";
            }
        }
        return s;
    }

    // implement any helper method here if you need any


	//I made this to keep track of size
	public int getServerInfosSize(){
		return getServerInfos().size();
	}

	//This exist simply to see if a loop is enteered, I'm too lazy to type it every time
	public void testLoop(){
		System.out.println("Hello");
	}


	//So apparently we need to check if host is valid
	public boolean hostVerifier(String hostname){
		if(!hostname.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")){
			return false;
		}

		if(!hostname.matches("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9])$")){
			return false;
		}
		return true;
	}
}
