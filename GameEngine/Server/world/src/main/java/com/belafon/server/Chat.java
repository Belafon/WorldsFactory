package com.belafon.server;


public class Chat {
	
	/*public static void sendLetter(String[] string, Client client) {
		if(!client.actual_game.isPlaying()) return;
		Place playerPosition = client.player.position;

		for(Player player : playerPosition.players) {
			if(player != client.player) {
				player.client.writer.sendLetter("chat " +  client.name + " /// " + string);
				ConsolePrint.new_message("Player " + " : " + string + "  - to player: " + player.id);
			}
		}
	}*/
	/*public static void sendLetterToClient(String string, Client client, Client targetClient) {
		if(!client.actual_game.isPlaying()) return;
		Place playerPosition = client.player.position;

		for(Player player : playerPosition.players) {
			if(player != client.player) {
				player.client.writer.sendLetter( "chat " +  client.name + " /// " + string);
				ConsolePrint.new_message("Player " + " : " + string + "  - to player: " + player.id);
			}
		}
	}*/
}
