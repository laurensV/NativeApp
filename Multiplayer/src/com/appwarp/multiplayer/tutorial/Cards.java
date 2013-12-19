package com.appwarp.multiplayer.tutorial;

public class Cards {
	
	public static String[][] cardsp1;
	public static String[][] cardsp2;
	
	public static void initCards(){
		cardsp1 = new String[][] {
				  //{name, attack, health}
					{"card0", "0", "0"},
					{"card1", "2", "3"},
					{"card2", "5", "6"},
					{"card3", "1", "2"},
					{"card4", "8", "9"}
			};
		cardsp2 = cardsp1;
	}
	
	public static String getName(int id, boolean secondPlayer){
		if (secondPlayer) 
			return cardsp2[id][0];
		else              
			return cardsp1[id][0];
	}
	
	public static int getAttack(int id, boolean secondPlayer){
		if (secondPlayer) 
			return Integer.parseInt(cardsp2[id][1]);
		else
		return Integer.parseInt(cardsp1[id][1]);
	}
	public static int getHealth(int id, boolean secondPlayer){
		if (secondPlayer) 
			return Integer.parseInt(cardsp2[id][2]);
		else
		return Integer.parseInt(cardsp1[id][2]);
	}
	
	public static void setAttack(int id, int attack, boolean secondPlayer){
		if (secondPlayer) 
			cardsp2[id][1] = ""+ attack;
		else
		cardsp1[id][1] = ""+ attack;
	}
	public static void setHealth(int id, int health, boolean secondPlayer){
		if (secondPlayer) 
			cardsp2[id][2] = ""+ health;
		else
		cardsp1[id][2] = ""+ health;
	}

}
