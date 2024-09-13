using System.Collections.ObjectModel;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace WorldsFactory.world.events.eventsInfoCard;

public partial class EventInfoCardView : UserControl
{
	public IEvent Event { get; init; }
	EventInfoCardViewModel model;
	
	public EventInfoCardView()
	{
		throw new NotImplementedException();	
	}
	public EventInfoCardView(IEvent event_, IEvents events)
	{
		InitializeComponent();
		Event = event_;
		var content = this.FindControl<DockPanel>("contentEventInfo")!;
		DataContext = model = new EventInfoCardViewModel(event_, content, events);
	}
}

internal class EventInfoCardViewModel
{
	public ObservableCollection<EventBasicInfo> EventTreeViewRootItems { get; init; }

	public ObservableCollection<object> SelectedItems { get; init; }
		= new ObservableCollection<object>();

	public EventInfoCardViewModel(IEvent event_, DockPanel content, IEvents events)
	{
		EventTreeViewRootItems = new ObservableCollection<EventBasicInfo>
		{
			new EventConditionAndAction(),
			new EventSequenceManager()
		};

		SelectedItems.CollectionChanged += (sender, args) =>
		{
			if (SelectedItems.Count == 0 || sender is null)
				return;

			OnSelectionChanged(content, event_, events);
		};
		displayViewInContentSpace(content, new EventBasicInfoView(event_));
	}

	private void OnSelectionChanged(DockPanel content, IEvent event_, IEvents events)
	{
		switch (SelectedItems[0])
		{
			case EventConditionAndAction _:
				displayViewInContentSpace(content, new EventBasicInfoView(event_));
				break;
			case EventSequenceManager _:
				displayViewInContentSpace(content, new EventBasicEventSequenceManagerView(event_.SequenceManager, events));
				break;
		}
	}

	private void displayViewInContentSpace(DockPanel contentControl, UserControl view)
	{
		contentControl.Children.Clear();
		contentControl.Children.Add(view);
	}
}

public interface EventBasicInfo
{
	public string Name { get; init; } 
}

public class EventConditionAndAction : EventBasicInfo
{
	public string Name { get; init; } = "Condition & Action"; 
}

public class EventSequenceManager : EventBasicInfo
{
	public string Name { get; init; } = "Events sequence"; 
}
