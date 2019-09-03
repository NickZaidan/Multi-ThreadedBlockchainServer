public class Test{

	public static void main(String[] args){
		ServerInfoList l = new ServerInfoList();
		//System.out.println(l.hostVerifier("localhost"));
		l.initialiseFromFile("configwork.txt");
		System.out.println(l.toString());


	}
}
