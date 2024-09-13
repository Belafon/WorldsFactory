using System.Linq;
using System.Reactive;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Media;
using ReactiveUI;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.events.eventsInfoCard;

public partial class EventBasicEventSequenceManagerView : UserControl
{
	public AutoCompleteBox ReferenceAutoCompleteBox { get; private set; }
	public AutoCompleteBox EventContainerReferenceBoxAutoCompleteBox { get; private set; }
	public ListBox EventListBox { get; private set; }
	public EventBasicEventSequenceManagerViewModel Model { get; private set; }

	public EventBasicEventSequenceManagerView()
	{
		throw new NotImplementedException();
	}
	public EventBasicEventSequenceManagerView(IEventSequenceManager sequenceManager, IEvents events)
    {
        InitializeComponent();
        ReferenceAutoCompleteBox = this.FindControl<AutoCompleteBox>("EventReferenceBox")!;
        EventListBox = this.FindControl<ListBox>("EventList")!;
        EventContainerReferenceBoxAutoCompleteBox = this.FindControl<AutoCompleteBox>("EventContainerReferenceBox")!;
        DataContext = Model = new EventBasicEventSequenceManagerViewModel(sequenceManager, this, events);
        bindOnContainerChanged(sequenceManager);

        sequenceManager.PropertyChanged += (sender, args) =>
        {
            if (args.PropertyName == "EventContainer")
            {
                bindOnContainerChanged(sequenceManager);
            }
        };

    }

    private void bindOnContainerChanged(IEventSequenceManager sequenceManager)
    {
        EventContainerReferenceBoxAutoCompleteBox.Text = sequenceManager.EventContainer?.Id;
        if (sequenceManager.EventContainer != null)
        {
            sequenceManager.EventContainer.OnIdChanged += (sender, args) =>
            {
                EventContainerReferenceBoxAutoCompleteBox.Text = sequenceManager.EventContainer?.Id;
            };
        }
    }
}

public class EventBasicEventSequenceManagerViewModel : ViewModelBase
{
	public IEventSequenceManager SequenceManager { get; private set; }
	public IEvents Events { get; private set; }

	private EventBasicEventSequenceManagerView view;
	public ReactiveCommand<Unit, Unit> AddEventCommand { get; }

	public ReactiveCommand<Reference<IEvent>, Unit> RemoveEventCommand { get; }
	public EventBasicEventSequenceManagerViewModel(
		IEventSequenceManager sequenceManager,
		EventBasicEventSequenceManagerView view,
		IEvents events)
	{
		this.view = view;
		Events = events;
		SequenceManager = sequenceManager;
		AddEventCommand = ReactiveCommand.Create(AddEvent);
		RemoveEventCommand = ReactiveCommand.Create<Reference<IEvent>>(RemoveEvent);
	}

	private void AddEvent()
	{
		var eventsId = view.ReferenceAutoCompleteBox.Text;
		try
		{
			var reference = Events.Collection.First(e => e.Id == eventsId).GetReference();
			if (SequenceManager.Events.Count(e => e.Id == reference.Id) == 0)
				SequenceManager.Events.Add(reference);
		}
		catch (InvalidOperationException)
		{
			Log.Error("Error while adding event, cannot find event with id: {id}", eventsId);
		}
	}

	public void RemoveEvent(Reference<IEvent> eventReference)
	{
		SequenceManager.Events.Remove(eventReference);
	}


	public void SetEventContainer_Click()
	{

		var allContainers = Events.ContainersLoader.IdManager.AllIdConcepts;
		var EventContainerId = view.EventContainerReferenceBoxAutoCompleteBox.Text!;
		if (EventContainerId == "" || EventContainerId == null)
		{
			SequenceManager.EventContainer = null;
			return;
		}

		if (!allContainers.ContainsKey(EventContainerId))
		{
			Log.Error("Error while setting event container, cannot find event container with id: {id}", EventContainerId);
			view.EventContainerReferenceBoxAutoCompleteBox.Foreground = Brushes.Red;
			return;
		}
		if (allContainers[EventContainerId] is EventContainer eventContainer)
		{
			view.EventContainerReferenceBoxAutoCompleteBox.Foreground = Brushes.Green;
			SequenceManager.EventContainer = eventContainer;
		}
		else
		{
			Log.Error("PANIC while setting event container, object with id: {id} is not an event container", EventContainerId);
			view.EventContainerReferenceBoxAutoCompleteBox.Foreground = Brushes.Yellow;
		}
	}
}
