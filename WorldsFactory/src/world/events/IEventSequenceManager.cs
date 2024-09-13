using System.Collections.ObjectModel;
using System.ComponentModel;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.events;

/// <summary>
/// Holds information about related events 
/// with concrete event.
/// It also holds the info about the event 
/// container in which the event is.
/// </summary>
public interface IEventSequenceManager : INotifyPropertyChanged
{
	/// <summary>
	/// Binds Events OnDelete event, so when
	/// the Event is delted, the reference is deleted
	/// from the sequence. 
	/// </summary>
	public void TryToBindAllEventReferencesWithEvents();

	/// <summary>
	/// This should be read only! // TODO
	/// is used for avalonia bindings only!
	/// </summary>
	/// <returns></returns>
	public ObservableCollection<Reference<IEvent>> Events { get; }
	
	/// <summary>
	/// Specifies a container in which the event is.
	/// </summary>
	/// <value></value>
	public EventContainer? EventContainer { get; set; }
}
