public class Test{
	public static void main(String[] args){
		ServerInfoList n = new ServerInfoList();
		n.initialiseFromFile("config.txt");
		System.out.println(n.toString());
	}
}
