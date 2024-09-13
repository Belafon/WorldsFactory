package com.belafon.world.maps.place;

import java.util.List;

import com.belafon.world.maps.Map;
import com.belafon.world.maps.weather.Weather;
import com.belafon.world.maps.weather.Weather.TypeClouds;
import com.belafon.world.maps.weather.Weather.TypeWeather;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.resources.Resource;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;

/**
 * this is an UnboundedPlace in concrete map.
 */
@WorldsFactoryClass(className = "Place", autoRegister = false)
public class Place extends UnboundedPlace {
    public final Map map;
    public final int positionX;
    public final int positionY;
    public int altitude;

    @WorldsFactoryObjectsName
    public String name;

    public static final BehavioursPossibleRequirement REQUIREMENT_IS_REACHABLE = new BehavioursPossibleRequirement(
            "A place is reachable.") {
    };

    public Place(Map map, int positionX, int positionY, int altitude, TypeOfPlace typeOfPlace) {
        super(typeOfPlace, map.game);

        this.map = map;
        this.positionX = positionX;
        this.positionY = positionY;
        this.altitude = altitude;
    }

    public Place(Map map, int positionX, int positionY, int altitude, TypeOfPlace typeOfPlace, String placeName) {
        super(typeOfPlace, map.game);

        this.map = map;
        this.positionX = positionX;
        this.positionY = positionY;
        this.altitude = altitude;

        this.name = placeName;
        WorldsFactoryStoryManager.bindObject(name, this);
    }

    /**
     * returns probable temerature of the place.
     * There is no random factor.
     */
    public int getTemperature() {
        // altitude = 1000, -> 30 - (1000 / 80) = 30 - 12
        // = 8 degrees celsius
        int temperature = 30 - (altitude / 80) + map.game.time.getPartOfDay().temperatureChange();
        if (map.sky != null) {
            Weather weather = map.sky.getWeather(this);
            temperature -= weather.getClouds();
            temperature -= weather.getWeather();
        }
        return temperature;
    }

    public Weather getWeather() {
        return map.sky.getWeather(this);
    }

    @Override
    public int getVisibility() {
        return visibility + map.sky.getWeather(this).getVisibility();
    }

    public String log() {
        String log = "\n";
        log += "Place: " + " in position = [" + positionX + ";" + positionY + "]  with altitude = "
                + altitude + "  " + typeOfPlace.name + "]";
        log += "\t\tResources = [ ";
        for (Resource resource : resources.values())
            log += resource.type.name + " ";
        log += " ]";
        return log;
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        var list = super.getBehavioursPossibleRequirementType(creature);
        if (this != creature.getLocation()) {
            if (creature.surroundingPlaces == null)
                list.add(REQUIREMENT_IS_REACHABLE);
            else if (creature.surroundingPlaces.isPlaceVisible(this))
                list.add(REQUIREMENT_IS_REACHABLE);
        }
        return list;
    }

    @Override
    public String getId() {
        return map.id + "$" + id;
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return new BehavioursPossibleIngredientID(UnboundedPlace.class, getId());
    }

    @WorldsFactoryPropertySetter(name = "weather")
    public void setFromStoryWeather(String typeWeather) {
        TypeWeather weather = TypeWeather.valueOf(typeWeather);
        map.sky.getWeather(this).setWeather(weather, this);
    }

    public void setStoryWeather(TypeWeather typeWeather) {
        WorldsFactoryStoryManager.setProperty("weather", typeWeather.name(), name);
    }

    @WorldsFactoryPropertySetter(name = "clouds")
    public void setFromStoryClouds(String clouds) {
        TypeClouds cloud = TypeClouds.valueOf(clouds);
        map.sky.getWeather(this).setClouds(cloud.ordinal() * 2, this);
    }

    public void setStoryClouds(TypeClouds clouds) {
        WorldsFactoryStoryManager.setProperty("clouds", clouds.name(), name);
    }
}