using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world;

public interface ICreateNewWorldsItemWithIdActions
{
	public ILibrary Library { get; init; }
	public void CreateNewClass(string prefixId, string postfixId);

	public void CreateNewObject(string name, WFType type);

	public void CreateNewEvent(string prefixId, string postfixId);

	public void CreateNewLinearEvent(string prefixId, string postfixId);
	
	public EventContainer CreateNewEventContainer(string name, EventContainer? parent);
}