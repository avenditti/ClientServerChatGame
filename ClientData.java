import java.io.PrintWriter;
import java.net.Socket;

public class ClientData {
	
	private Socket sock;
	private String ClientName = "null";
    private PrintWriter ClientWriter;
    private String selection = "";
    public boolean onTeam = false;
    
    public ClientData(){
    }
    
    public String getS(){
    	return selection;
    }
    
    public void setS(String S){
    	selection = S;
    }
    
	public String getClientName() {
		return ClientName;
	}

	public PrintWriter getClientWriter() {
		return ClientWriter;
	}
	
	public void setClientWriter(PrintWriter w){
		this.ClientWriter = w;
	}

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public void setClientName(String ClientName) {
		this.ClientName = ClientName;
	}
}