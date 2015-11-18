/*
 * Client Protocol List
 * 00001 Name protocol
 * 00010 Bad Name
 * 00011 Name Accepted
 * 00100 Print
 * 00101 User disconnecting
 * 00110 User 2 send
 * 00111 ClearClientMessagebox
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Server {
	
	private static final int PORT = 9001;
	private static HashSet<ClientData> ClientData = new HashSet<ClientData>();
	private static Game g;
	
    public static void main(String args[]) throws Exception{
    	System.out.println("Starting server...");
    	new Listener().start();
    	Scanner scan = new Scanner(System.in);
    	while(true){
    		String s = scan.next();
    		switch(s){
	    		case "list":
	    			System.out.println("Currently connected clients");
	    			for(ClientData p : ClientData){
	    				System.out.println(p.getClientName());
	    			}
	    			break;
	    		case "quit":
	    			System.exit(0);
	    			scan.close();
	    			break;
	    		case "kick":
	    			s = scan.next();
	    			for(ClientData p : ClientData){
	    				if(p.getClientName().equals(s)){
	    					String reason = scan.nextLine();
	    					p.getClientWriter().println("00100You have been kicked from the server,    Reason: " + reason);
	    					p.getClientWriter().close();
	    					ClientData.remove(p);
	    					for (ClientData writer : ClientData) {
	    	                    writer.getClientWriter().println("00100" + p.getClientName() + " was kicked from the server");
	    	                }
	    				}
	    			}
	    			break;
	    		case "gamestart":
	    			g = new Game(ClientData,scan);
	    			g.start();
	    			break;
	    		case "restart":
	    			if(g!=null)
	    				g.restart = true;
    		}		
    	}
    } 
    private static class Game extends Thread{
    	
    	private static HashSet<ClientData> ClientData;
    	private static Team[] teams;
    	private int ws;
    	private static ArrayList<String> words = new ArrayList<String>();
    	public boolean restart = false;
    	
    	public Game(HashSet<ClientData> playerlist,Scanner scan1){
    		ClientData = playerlist;
			System.out.println("Amount of teams?");
			int team = scan1.nextInt();
			System.out.println("Amount of rounds to win?");
			ws = scan1.nextInt();
			int teamsize = 1;
			boolean success = false;
			teams = new Team[team];
			for(int i = 0;i<team;i++){
				System.out.println("Enter size of team " + (i+1));
				while(true){
					try{
						teamsize = scan1.nextInt();
						break;
					}catch(Exception e){
						System.out.println(e);
					}
				}
				teams[i] = new Team(teamsize);
				for(int p = 0;p<teamsize;p++){
					System.out.println("Currently connected clients not on a team");
				for(ClientData n : ClientData){
					if(!n.onTeam)
						System.out.println(n.getClientName());
				}
					System.out.println("Enter the name of the player to add to team " + (i+1) + ", slot " + (p+1) + " or 'skip' to go to next slot");
					String name = scan1.nextLine();
					while(name.equals("")){
						name = scan1.nextLine();
					}
					if(!name.equals("skip")){
						for(ClientData g : ClientData){
							if(g.getClientName().toUpperCase().equals(name.toUpperCase()))
								if(g.onTeam==false){
									teams[i].addClient(g);
									g.onTeam = true;
									success = true;
									break;
								}
								else{
									System.out.print("Player already on a team");
									success = true;
									p--;
									break;
								}
						}
						if(!success){
							p--;
							System.out.println("Couldnt find player: " + name);
						}
						success = false;
					}
				}
			}
    	}
    	
    	public void run(){
    		int[] scores = new int[teams.length];
    		//construct words library
    		try {
				constructLibrary();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
    		
    		for(int i = 0;i<teams.length;i++){
				for(int p = 0;p<teams[i].getTeamSize();p++){
					if(teams[i].getClient(p)!=null)
						teams[i].getClient(p).getClientWriter().println("00101Team " + (i+1));
				}
    		}
			Random r = new Random();
			int l = r.nextInt(teams.length),g = 0,k = r.nextInt(10)+5*10,s;
			//Choose a random team
			s = r.nextInt(teams[l].getTeamSize());
			while(teams[l].getClient(s) == null){
				 s = r.nextInt(teams[l].getTeamSize());
			}
			while(true){
				
    			String j = words.get(r.nextInt(words.size()));
    			teams[l].getClient(s).getClientWriter().println("00101Your word is " + j + " try to describe it to your teammates");
    			//Send out the word
    			for(int i = 0;i<teams[l].getTeamSize();i++){
    				if(!teams[l].getClient(i).getClientName().equals(teams[l].getClient(s).getClientName()))
    					teams[l].getClient(i).getClientWriter().println("00101" + teams[l].getClient(s).getClientName() + " is trying to describe a word to you!");
    			}
    			//Check to see if someone has guessed it
    			for(g+=0;g<k;g++){
					if(teams[l].getClient(s)!=null){
						for(int p = 0;p<teams[l].getTeamSize();p++)
							teams[l].getClient(p).getClientWriter().println("01000" + (k-g));
						try {
							if(teams[l].getClient(s).getS().equals("00110")){
								teams[l].getClient(s).setS("");
								for(int p = 0;p<teams[l].getTeamSize();p++){
		    	    					teams[l].getClient(p).getClientWriter().println("00111");
		    	    			}
								scores[l]++;
								break;
							}
							Thread.sleep(1000);;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
    			/*
    			for(int i = 0;i<teams[l].getTeamSize();i++){
    				if(teams[l].getClient(i)!=null){    					
    					try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				if(teams[l].getClient(i).getS().equals(j.toUpperCase())){
	    					scores[l]++;
	    					teams[l].getClient(i).setS("");
	    					for(int p = 0;p<teams[l].getTeamSize();p++){
	    	    				if(!teams[l][p].getClientName().equals(teams[l].getClient(s).getClientName()))
	    	    					teams[l][p].getClientWriter().println("00111");
	    	    					teams[l][p].getClientWriter().println("00101That was it!");
	    	    			}
	    					break;
	    				}
	    				if(i>=teams[l].getTeamSize()-1)
	    					i=0;
    				}
    			}
    			*/
    			//Check to see if someone has won
    			for(int i = 0; i<scores.length;i++){
    				if(scores[i]>ws){
    					System.out.println("Team " + i + " has won!");
    					for (ClientData writer : ClientData) {
    	                    writer.getClientWriter().println("00100Team " + i + " has won!");
    	                }
    					if(restart){
    						restart = false;
    						break;
    					}
    					for(int h = 0; h<teams.length;h++){
    						for(int l1 = 0;l1<teams[h].getTeamSize();l1++){
    							teams[h].getClient(l1).onTeam = false;
    						}
    					}
    					return;
    				}
    			}
    			
    			if(g>=k){
    				g = 0;
    				k = r.nextInt(10)+5*10;
    				if(l<teams.length-1)
    					l++;
    				else
    					l = 0;
    				s = r.nextInt(teams[l].getTeamSize());
    				while(teams[l].getClient(s) == null){
    					 s = r.nextInt(teams[l].getTeamSize());
    				}
    				for(int i = 0;i<teams[l].getTeamSize();i++)
    					teams[l].getClient(i).getClientWriter().println("00101Your team is next");
    				teams[l].getClient(s).getClientWriter().println("00101Press the Next Player button to start your teams turn");
    					teams[l].getClient(s).setS("");
    				for(int i = 0;i<teams[l].getTeamSize();i++){
	    				if(teams[l].getClient(s).getS().equals("00110")){
	    					break;
	    				}
	    				try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				if(i>=teams[l].getTeamSize()-1){
	    					i=0;
	    				}
    				}
    			}else{
    				if(l<teams.length-1)
    					l++;
    				else
    					l = 0;
    				s = r.nextInt(teams[l].getTeamSize()-1);
    				while(teams[l].getClient(s) == null){
    					 s = r.nextInt(teams[l].getTeamSize()-1);
    				}
    			}
    			
    			//Go to next team
    			if(++l>=teams.length){
    				l = 0;
    			}
    		}
    	}

		private void constructLibrary() throws FileNotFoundException {
			File file = new File("r.txt");
			Scanner fr = new Scanner(file);
			while(fr.hasNext()){
				words.add(fr.next());
			}
			fr.close();
		}
    }
    private static class Listener extends Thread{
    	ServerSocket listener;
	    public void run() {
	    	try {
	    		listener = new ServerSocket(PORT);
	            while (true) {
	                new Handler(listener.accept()).start();
	                System.out.println("Client connecting...");
	            }
	        }catch(IOException e){}
	    	finally {
	            try {
					listener.close();
				}catch(IOException e){}
	        }
	    }
    }
    private static class Handler extends Thread{
    	
    	
    	private String ClientName;
    	private BufferedReader in;
        private PrintWriter out;
        private ClientData CD = new ClientData();

    	
    	Handler(Socket sock){
    		CD.setSock(sock);
    	}

    	public void run() {
    		
    		try{
    			in = new BufferedReader(new InputStreamReader(CD.getSock().getInputStream()));
    	        out = new PrintWriter(CD.getSock().getOutputStream(), true);
    	        boolean done = false;
    	        boolean found;
    			while(!done){
    				out.println("00001");
    				ClientName = in.readLine();
    				if(ClientName==null)
    					return;
					synchronized (ClientData){
						found = false;
						for(ClientData p : ClientData){
							if(p.getClientName().equals(ClientName)){
								out.println("00010");
								found = true;
								break;
		    				}
						}
						if(!found){
							CD.setClientName(ClientName);
							ClientData.add(CD);
							CD.setClientWriter(out);
							done = true;
							break;
						}
    				}
    			}
    			
				out.println("00011");
				System.out.println(ClientName + " connected to the server");
				
				for (ClientData writer : ClientData) {
                    writer.getClientWriter().println("00100" + ClientName + " connected to the server!");
                }
				
				String input = in.readLine();
                if (input == null) 
                    return;
                //handle text chat
                while (!input.equals("00101")) {
                	String input2 = input.substring(0,5);
                	switch(input2){
                		case "00100": 
                			System.out.printf("%-15s: %s\n",ClientName, input);
		                    for (ClientData writer : ClientData) {
		                        writer.getClientWriter().println("00100" + ClientName + ": " + input);
		                    }
                		case "00110":
                			synchronized (CD){
                				CD.setS(input);
                			}
                	}
                    input = in.readLine();
                }
                disconnect(ClientName);
    		}
    		
    		catch(Exception e){
    			System.out.print(e);
    		}
    		
    		finally{
    			if (CD != null) {
                    ClientData.remove(CD);
                }
                try {
                	CD.getSock().close();
                } catch (IOException e) {
                }
    		}
    	}
    	public void disconnect(String name){
            System.out.println(name + " disconnected...");
            for (ClientData writer : ClientData) {
                writer.getClientWriter().println("00100 " + ClientName + " left the server");
            }
            if (CD != null) {
                ClientData.remove(CD);
            }
            try {
            	CD.getSock().close();
            } catch (IOException e) {}
    	}
    }
}