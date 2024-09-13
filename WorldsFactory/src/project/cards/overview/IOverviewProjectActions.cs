using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WorldsFactory.world.events;
using WorldsFactory.world.library;
using WorldsFactory.world.objects;

namespace WorldsFactory.project;
public interface IOverviewProjectActions
{
	public Action<IClass, PlaceToShowNewCard> OnClassSelected { get; init; }

    public Action<IEvent, PlaceToShowNewCard> OnEventSelected { get; init; }

    void OnEventSequenceVisualisationSelected(EventSequenceVisualisation obj, PlaceToShowNewCard placeToShowNewCard);
    void OnObjectSelected(WFObject wfObject, PlaceToShowNewCard placeToShowNewCard);
}

