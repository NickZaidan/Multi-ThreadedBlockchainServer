public class ServerInfo {

	private String id;
    private String host;
    private int port;


	public ServerInfo(){

	}
    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }


	public String getId(){
		return id;
	}

	public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

	public void setId(String id){
		this.id = id;
	}
    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // implement any helper method here if you need any

	public boolean isPaired(){
		if(host == null || port == 0){
			return false;
		}

		if(port < 1024 || port > 65535){
			return false;
		}

		return true;
	}
}
