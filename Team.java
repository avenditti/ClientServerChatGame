
public class Team {
	private ClientData[] team;
	private int nextSlot = 0;
	public Team(int size){
		team = new ClientData[size];
	}

	public int getTeamSize() {
		return nextSlot;
	}

	public ClientData getClient(int i) {
		return team[i];
	}

	public void addClient(ClientData d) {
		if(nextSlot!=team.length){
			if(d==null){
				return;
			}
			this.team[nextSlot++] = d;
			return;
		}else{
			System.out.println("Team Full");
		}
	}
}
