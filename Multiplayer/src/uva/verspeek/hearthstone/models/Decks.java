package uva.verspeek.hearthstone.models;

public class Decks {
	
	public static String[][] cardsp1;
	public static String[][] cardsp2;
	
	public static void initCards(){
		cardsp1 = new String[][] {
				  //{name, attack, health, mana, type}
					{"back", "0", "0", "0", ""},
					{"hogger", "4", "5", "5", "m"},
					{"harisson", "5", "6", "1", "m"},
					{"geddon", "1", "2", "2", "m"},
					{"chayes", "8", "9", "4", "m"},
					{"leeroy", "8", "9", "3", "m"},
					{"terror", "8", "9", "1", "m"},
					{"stormrage", "8", "9", "2", "m"},
					{"windrunner", "8", "9", "2", "m"},
					{"spell_hellfire", "3", "0", "5", "s"}
			};
		cardsp2 = new String[][] {
				  //{name, attack, health, mana, type}
					{"back", "0", "0", "0", ""},
					{"hogger", "4", "5", "5", "m"},
					{"harisson", "5", "6", "1", "m"},
					{"geddon", "1", "2", "2", "m"},
					{"chayes", "8", "9", "4", "m"},
					{"leeroy", "8", "9", "3", "m"},
					{"terror", "8", "9", "1", "m"},
					{"stormrage", "8", "9", "2", "m"},
					{"windrunner", "8", "9", "2", "m"},
					{"spell_hellfire", "3", "0", "5", "s"}
			};
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
	public static int getMana(int id, boolean secondPlayer){
		if (secondPlayer) 
			return Integer.parseInt(cardsp2[id][3]);
		else
			return Integer.parseInt(cardsp1[id][3]);
	}
	
	public static String getType(int id, boolean secondPlayer){
		if (secondPlayer) 
			return cardsp2[id][4];
		else
			return cardsp1[id][4];
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
	public static void setMana(int id, int mana, boolean secondPlayer){
		if (secondPlayer) 
			cardsp2[id][3] = ""+ mana;
		else
			cardsp1[id][3] = ""+ mana;
	}
}
