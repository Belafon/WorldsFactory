using System.Collections.ObjectModel;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;

namespace WorldsFactory.world.events;

public interface IEventLoader : ILibraryLoader
{
	void Save(Event event_);
	void Delete(Event event_);
	Task LoadAll(ObservableCollection<IEvent> events);
	void Rename(Event event_, string oldId);
	/// <summary>
	/// root id manager in context of everything
	/// </summary>
	IIDConceptManager IdManager { get; }
	/// <summary>
	/// id manager in context of all events
	/// </summary>
	IIDConceptManager IdEventsManager { get; }
	ITagManager TagManager { get; }
	IClass CreateNewClass(string name);
}
