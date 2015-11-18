import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Client {
	
	Socket socket;
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Some clever game name");
    JPanel jp1 = new JPanel();
    JPanel jp2 = new JPanel();
    JTextField textField = new JTextField(15);
    Button button1 = new Button("Next Player");
    JTextArea messageArea = new JTextArea(40, 10);
    JTextArea messageArea2 = new JTextArea(40, 40);
    
    public static void main(String args[]) throws Exception{
    	Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        client.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
               try{
                  client.out.println("00101");
                  client.socket.close();
                  System.exit(0);
               }catch(Exception e){
               System.exit(0);
               }
            }
         });
        client.frame.setVisible(true);
        client.run();
    }
    
    public Client(){
    	
    	BuildGUI();
    	
    	textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println("00100"+textField.getText());
                textField.setText("");
            }
        });
    	button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println("00110");
            }
        });
    }
    
	private void run() throws Exception {
		try{
			String serverAddress = getServerAddress();
			if(serverAddress==null)
				System.exit(0);
	        socket = new Socket(serverAddress, 9001);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);
	        String s = "";
	        while (true) {
	            String line = in.readLine();
	            if (line.startsWith("00001"))
	                out.println(getName());
	            else if (line.startsWith("00011"))
	                textField.setEditable(true);
	            else if (line.startsWith("00100"))
	                messageArea.append(line.substring(5) + "\n");
	            else if (line.startsWith("00010"))
	                messageArea.append("Name already in use\n");
	            else if (line.startsWith("00101")){
	            	messageArea2.append(line.substring(5) + "\n");
	            	s = messageArea2.getText();
	            }
	            else if (line.startsWith("00111"))
	            	messageArea2.setText("");
	            else if (line.startsWith("01000")){
	            	messageArea2.setText(s);
	            	messageArea2.append(line.substring(5) + "\n");
	            }
	        }
		}catch(Exception e){
			System.out.println("Unknown Host");
			run();
		}
	}
	
	private String getServerAddress() {
        return JOptionPane.showInputDialog(frame,"Enter IP Address of the Server:","Connection",JOptionPane.QUESTION_MESSAGE);
    }

    private String getName() {
        return JOptionPane.showInputDialog( frame,"Choose a screen name:","Screen name selection", JOptionPane.PLAIN_MESSAGE);
    }

    
	private void BuildGUI(){
    	frame.setLayout( new FlowLayout() );
		frame.add(jp1);
		frame.add(jp2);
		jp1.setLayout(new BorderLayout(0, 0));
		jp2.setLayout(new BorderLayout(0, 0));
		jp1.add(new JLabel("Chat"),BorderLayout.NORTH);
		jp2.add(new JLabel("Game"),BorderLayout.NORTH);
		jp1.add(new Button("Connect"),BorderLayout.EAST);
		jp1.add(textField,BorderLayout.CENTER);
		jp2.add(button1,BorderLayout.CENTER);
		messageArea.setLineWrap(true);
		messageArea2.setLineWrap(true);
        jp1.add(new JScrollPane(messageArea),BorderLayout.SOUTH);
        jp2.add(new JScrollPane(messageArea2),BorderLayout.SOUTH);
        frame.pack();
		messageArea.setEditable(false);
		messageArea2.setEditable(false);
    }
}
