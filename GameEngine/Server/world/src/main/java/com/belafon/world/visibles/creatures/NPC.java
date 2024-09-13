package com.belafon.world.visibles.creatures;

import com.belafon.server.sendMessage.MessageSender;
import com.belafon.world.World;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.inventory.Inventory;
import com.belafon.world.visibles.creatures.inventory.PlayersGear;

public class NPC extends Creature {
    private NPC(World game, String name, UnboundedPlace location, String appearence, MessageSender sendMessage,
            int weight) {
        super(game, name, location, appearence, sendMessage, weight);
    }

    @Override
    protected void setInventory(UnboundedPlace position) {
        inventory = new Inventory(new PlayersGear(), this, position);
    }

    public static class Builder {
        private World game;
        private String name;
        private UnboundedPlace location;
        private String appearence;
        private MessageSender sendMessage;
        private int weight;

        public Builder setWorld(World game) {
            this.game = game;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLocation(UnboundedPlace location) {
            this.location = location;
            return this;
        }

        public Builder setAppearence(String appearence) {
            this.appearence = appearence;
            return this;
        }

        public Builder setSendMessage(MessageSender sendMessage) {
            this.sendMessage = sendMessage;
            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public NPC build() {
            return new NPC(game, name, location, appearence, sendMessage, weight);
        }
    }

}
