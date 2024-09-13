package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.ServerMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;

public class ServerPlayerMessages implements ServerMessages{
  public final PlayersMessageSender sendMessage;

  public ServerPlayerMessages(PlayersMessageSender sendMessage) {
    this.sendMessage = sendMessage;
  }
  @Override
  public void setNumberOfPlayersInQueue(int number) {
		sendMessage.sendLetter("server number_of_players_to_wait " + number, PlayersMessageSender.TypeMessage.other);
	}
  @Override
  public void startGame() {
		sendMessage.sendLetter("server startGame", PlayersMessageSender.TypeMessage.other);
  }
}
