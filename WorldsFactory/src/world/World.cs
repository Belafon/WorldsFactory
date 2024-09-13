using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DynamicData.Binding;
using WorldsFactory.project;
using WorldsFactory.world.events;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.world.objects;

namespace WorldsFactory.world;
public class World : ICreateNewWorldsItemWithIdActions
{
	public ILibrary Library { get; init; }
	public IObjects Objects { get; init; }
	public IEvents Events { get; init; }
	public IDConceptManager	IDConceptManager { get; init; }
	public ITagManager TagsManager { get; init; }
	public WorldLoader WorldLoader { get; init; }
	internal World(ILibrary library, IObjects objects, IEvents events, IDConceptManager iDConceptManager, ITagManager tagsManager, WorldLoader worldLoader)
	{
		Library = library;
		Objects = objects;
		IDConceptManager = iDConceptManager;
		TagsManager = tagsManager;
		WorldLoader = worldLoader;
		Events = events;
	}
	
	public void CreateNewClass(string prefixId, string postfixId)
	{
		Library.CreateClass(prefixId, postfixId, WorldLoader.ClassesFolder!);
	}

	public void CreateNewEvent(string prefixId, string postfixId)
	{
		var eventLoder = new EventLoader(WorldLoader.EventsFolder!, IDConceptManager, TagsManager);
		Events.Collection.Add(new Event(eventLoder, postfixId));
	}

	public void CreateNewLinearEvent(string prefixId, string postfixId)
	{
		throw new NotImplementedException();
	}

	public void CreateNewObject(string name, WFType type)
	{
		Objects.CreateNewObject(name, type);	
	}

    public EventContainer CreateNewEventContainer(string name, EventContainer? parent)
    {
        return Events.CreateNewEventContainer(name, parent);
    }
}
