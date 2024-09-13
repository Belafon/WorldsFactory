using System.Collections.ObjectModel;
using WorldsFactory.world.events.eventContainer;

namespace WorldsFactory.world.events;

public interface IEvents
{
	ObservableCollection<IEvent> Collection { get; }
	ObservableCollection<EventContainer> FirstLevelEventContainers { get; }
	IEventLoader EventLoader { get; }
	IEventContainersLoader ContainersLoader { get; }

	EventContainer CreateNewEventContainer(string name, EventContainer? parent);
}