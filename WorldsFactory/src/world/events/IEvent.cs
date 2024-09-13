using System.ComponentModel;
using WorldsFactory.project;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.events;

public interface IEvent : INotifyPropertyChanged, IConceptWithID, NamedConcept
{
	public new string Name { get; set; }
	public IMethod Condition { get; init; }
	public IMethod Action { get; init; }
	public event EventHandler? OnDelete;
	public Reference<IEvent> GetReference();
	public IEventSequenceManager SequenceManager { get; }
	public IIDConceptManager IdEventsManager { get; }
	public void Delete();
	public IClass Class { get; }
}
