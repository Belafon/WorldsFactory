using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using System.Collections.ObjectModel;
using WorldsFactory.screen;
using WorldsFactory.world.library;
using Serilog;
using Avalonia.Interactivity;
using Avalonia.VisualTree;
using WorldsFactory.world.objects;
using WorldsFactory.world.events;
using WorldsFactory.world;
using WorldsFactory.src.screen;
using Avalonia.Input;
using WorldsFactory.world.events.eventContainer;
using System.ComponentModel;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.project;

/// <summary>
/// Control that displays the overview of the project
/// in a tree structure. 
/// It can show all classes, events, objects, event containers... 
/// etc.
/// </summary>
public partial class OverviewProjectView : UserControl
{

	private TreeView treeView;
	public OverviewProjectView(World world, IOverviewProjectActions overviewProjectActions)
	{
		InitializeComponent();
		treeView = this.FindControl<TreeView>("OverviewProjectTreeView")!;
		DataContext = new OverviewProjectViewModel(world, overviewProjectActions, treeView, tryToGetMainWindow);
	}

	public OverviewProjectView()
	{
		throw new NotImplementedException();
	}

	override protected void OnAttachedToVisualTree(VisualTreeAttachmentEventArgs e)
	{
		base.OnAttachedToVisualTree(e);

		var containers = treeView.GetRealizedTreeContainers();

		foreach (var item in containers)
		{
			var treeItem = treeView.TreeItemFromContainer(item);


			if (treeItem is null)
			{
				if (treeItem is TreeViewItem treeViewItem)
					treeView.ExpandSubTree(treeViewItem);
			}
		}

	}
	private MainWindow? tryToGetMainWindow()
	{
		Window? window = this.VisualRoot as Window;
		if (window is null)
		{
			Log.Error("Could not get main window");
			return null;
		}
		if (window is MainWindow mainWindow)
		{
			return mainWindow;
		}
		Log.Error("Could not get main window");
		return null;
	}
	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}
}

public class OverviewProjectViewModel : ViewModelBase
{
	private IOverviewProjectActions cardsActions;
	private Func<MainWindow?> tryToGetMainWindow;
	public IObjects Objects { get; init; }
	public OverviewProjectViewModel(
		World world,
		IOverviewProjectActions cardsActions,
		TreeView treeView,
		Func<MainWindow?> tryToGetMainWindow)
	{
		Objects = world.Objects;
		this.cardsActions = cardsActions;
		this.tryToGetMainWindow = tryToGetMainWindow;
		var works = new WorksTreeViewItem();
		var worldTreeViewItem = new WorldTreeViewItem
		{
			PartsOfWorld = new ObservableCollection<PartOfWorld>
				{
					world.Library,
					new ObjectsTreeViewItem(world.Objects),
					//new ObjectsGroupedByEventsTreeViewItem(world.Objects, world.Events),
					new EventsTreeViewItem
					{
						EventsPart = new ObservableCollection<EventsPartWorkTreeItem>
						{
							new AllEventsWorkTreeItem(world.Events),
							new EventContainersTreeViewItem(world.Events, tryToGetMainWindow),
							new LinearEventWorkTreeItem()
						}
					},
					new VisualizationsTreeViewItem
					{
						VisualisationParts = new ObservableCollection<VisualisationPart>
						{
							new EventSequenceVisualisation(world.Events)
						}
					}
				}
		};

		WorldOrWorksTreeViewItems = new ObservableCollection<WorldOrWorksTreeViewItem>
		{
			worldTreeViewItem,
			works
		};

		treeView.Tapped += OnTapped!;
		treeView.DoubleTapped += OnDoubleTapped!;
		var tree = new TreeViewItem();
	}
	private bool doubleTappedEventRaised = false;
	private bool tappedEventRaised = false;
	private async void OnTapped(object sender, RoutedEventArgs e)
	{
		Thread.CurrentThread.Name = "Tapped";
		doubleTappedEventRaised = false;
		tappedEventRaised = false;

		await Task.Delay(TimeSpan.FromMilliseconds(100));

		if (doubleTappedEventRaised)
			return;

		tappedEventRaised = true;

		MainWindow? mainWindow = tryToGetMainWindow();
		if (mainWindow is null)
		{
			onSelectionChanged(sender, PlaceToShowNewCard.NewPanelCardView);
		}
		else
		{
			onSelectionChanged(sender, PlaceToShowNewCard.LastSelected);
		}
	}

	private void OnDoubleTapped(object sender, RoutedEventArgs e)
	{
		if (tappedEventRaised)
			return;

		doubleTappedEventRaised = true;
		onSelectionChanged(sender, PlaceToShowNewCard.NewPanelCardView);
	}

	public ObservableCollection<WorldOrWorksTreeViewItem> WorldOrWorksTreeViewItems { get; init; }
	public ObservableCollection<object> SelectedItems { get; init; }
		= new ObservableCollection<object>();

	private void onSelectionChanged(object? sender, PlaceToShowNewCard placeToShowNewCard)
	{
		if (SelectedItems.Count == 0 || sender is null)
		{
			return;
		}

		if (SelectedItems[0] is IClass clazz)
		{
			cardsActions.OnClassSelected(clazz, placeToShowNewCard);
		}
		else if (SelectedItems[0] is IEvent event_)
		{
			cardsActions.OnEventSelected(event_, placeToShowNewCard);
		}
		else if (SelectedItems[0] is EventSequenceVisualisation obj)
		{
			cardsActions.OnEventSequenceVisualisationSelected(obj, placeToShowNewCard);
		}
		else if (SelectedItems[0] is WFObject wfObject)
		{
			cardsActions.OnObjectSelected(wfObject, placeToShowNewCard);
		}
		else if (SelectedItems[0] is EventWithRelatedObjectsTreeViewItem evItem)
		{
			var ev_ = evItem.Event;
			cardsActions.OnEventSelected(ev_, placeToShowNewCard);
		}



		SelectedItems.Clear();
	}


	public void RenameClass(StackPanel classPanel)
	{
		var clazz = classPanel.DataContext as IClass;

		if (clazz is null)
			throw new Exception("Class is null");

		var classTitle = ControlFinder.Find<TextBlock>(classPanel, "className");
		var child = classPanel.Children.FirstOrDefault();


		if (classTitle is null)
			return;

		classTitle.IsVisible = false;
		classTitle.IsHitTestVisible = false;

		var newNameBox = new TextBox
		{
			Text = clazz.PostfixId,
		};

		newNameBox.LostFocus += (sender, args) =>
				submitNewClassName(clazz, newNameBox, classPanel, classTitle);

		newNameBox.KeyDown += (sender, args) =>
			{
				if (args.Key == Key.Enter)
					submitNewClassName(clazz, newNameBox, classPanel, classTitle);
			};

		classPanel.Children.Add(newNameBox);
	}
	private void submitNewClassName(IClass clazz, TextBox newNameBox, StackPanel classPanel, TextBlock classTitle)
	{
		var newName = newNameBox.Text!;
		try
		{
			clazz.Rename(newName);
		}
		catch (ConceptWithIDAlreadyExistsException e)
		{
			Log.Error(e, "Class with this name already exists");
			tryToGetMainWindow()?.Notifications.ShowErrorNotification("cannot_create_new_class_title", "empty");
		}
		classPanel.Children.Remove(newNameBox);
		classTitle.IsVisible = true;
		classTitle.IsHitTestVisible = true;
	}

	public void RenameEvent(StackPanel eventPanel)
	{

		var eventObj = eventPanel.DataContext as IEvent;

		renameEvent(eventPanel, eventObj);
	}

	private void renameEvent(StackPanel eventPanel, IEvent? eventObj)
	{
		if (eventObj is null)
			throw new Exception("Event is null");

		var eventTitle = ControlFinder.Find<TextBlock>(eventPanel, "eventName");

		if (eventTitle is null)
			return;

		eventTitle.IsVisible = false;

		var newNameBox = new TextBox
		{
			Text = eventObj.Name,
		};

		newNameBox.LostFocus += (sender, args) =>
			SubmitNewEventName(eventObj, newNameBox, eventPanel, eventTitle);

		newNameBox.KeyDown += (sender, args) =>
		{
			if (args.Key == Key.Enter)
				SubmitNewEventName(eventObj, newNameBox, eventPanel, eventTitle);
		};

		eventPanel.Children.Add(newNameBox);
	}

	public void RenameEventWithRelatedObjects(StackPanel eventPanel)
	{
		var evTreeView = eventPanel.DataContext as EventWithRelatedObjectsTreeViewItem;
		var ev = evTreeView?.Event;
		renameEvent(eventPanel, ev);
	}

	private void SubmitNewEventName(IEvent eventObj, TextBox newNameBox, StackPanel eventPanel, TextBlock eventTitle)
	{
		var newName = newNameBox.Text!;
		try
		{
			eventObj.Name = newName;
		}
		catch
		{
			Log.Error("Event with this name already exists");
			tryToGetMainWindow()?.Notifications.ShowErrorNotification("cannot_create_new_event_title", "empty");

		}
		eventPanel.Children.Remove(newNameBox);
		eventTitle.IsVisible = true;
	}

	public void RenameEventContainer(StackPanel eventContainerPanel)
	{
		var eventContainerTreeItem = eventContainerPanel.DataContext as EventContainerTreeViewItem;
		var eventContainer = eventContainerTreeItem?.Container;

		if (eventContainer is null)
			throw new Exception("Event container is null");

		var eventContainerTitle = ControlFinder.Find<TextBlock>(eventContainerPanel, "eventContainerName");

		if (eventContainerTitle is null)
			return;

		eventContainerTitle.IsVisible = false;

		var newNameBox = new TextBox
		{
			Text = eventContainer.Name,
		};

		newNameBox.LostFocus += (sender, args) =>
			SubmitNewEventContainerName(eventContainer, newNameBox, eventContainerPanel, eventContainerTitle);

		newNameBox.KeyDown += (sender, args) =>
		{
			if (args.Key == Key.Enter)
				SubmitNewEventContainerName(eventContainer, newNameBox, eventContainerPanel, eventContainerTitle);
		};

		eventContainerPanel.Children.Add(newNameBox);
	}



	private void SubmitNewEventContainerName(EventContainer eventContainer, TextBox newNameBox, StackPanel eventContainerPanel, TextBlock eventContainerTitle)
	{
		var newName = newNameBox.Text!;
		try
		{
			eventContainer.Name = newName;
		}
		catch (ConceptWithIDAlreadyExistsException e)
		{
			Log.Error(e, "Event container with this name already exists");
			newNameBox.FindAncestorOfType<MainWindow>()?.Notifications.ShowErrorNotification("event_container_with_this_name_already_exists_title", "event_container_with_this_name_already_exists");
		}
		eventContainerPanel.Children.Remove(newNameBox);
		eventContainerTitle.IsVisible = true;
	}


	public void RenameObject(StackPanel objectPanel)
	{
		var obj = objectPanel.DataContext as WFObject;

		if (obj is null)
			throw new Exception("Object is null");

		var objectTitle = ControlFinder.Find<TextBlock>(objectPanel, "objectName");

		if (objectTitle is null)
			return;

		objectTitle.IsVisible = false;

		var newNameBox = new TextBox
		{
			Text = obj.Name,
		};

		newNameBox.LostFocus += (sender, args) =>
			SubmitNewObjectName(obj, newNameBox, objectPanel, objectTitle);

		newNameBox.KeyDown += (sender, args) =>
		{
			if (args.Key == Key.Enter)
				SubmitNewObjectName(obj, newNameBox, objectPanel, objectTitle);
		};

		objectPanel.Children.Add(newNameBox);
	}

	private void SubmitNewObjectName(WFObject obj, TextBox newNameBox, StackPanel objectPanel, TextBlock objectTitle)
	{
		var newName = newNameBox.Text!;
		try
		{
			obj.Name = newName;
		}
		catch (ConceptWithIDAlreadyExistsException e)
		{
			Log.Error(e, "Object with this name already exists");
			newNameBox.FindAncestorOfType<MainWindow>()?.Notifications.ShowErrorNotification("cannot_create_new_object_title", "empty");
		}
		objectPanel.Children.Remove(newNameBox);
		objectTitle.IsVisible = true;
	}

}

public interface WorldOrWorksTreeViewItem
{
	public abstract string Name { get; set; }
}

public class WorldTreeViewItem : WorldOrWorksTreeViewItem
{
	public string Name { get; set; } = "World";
	public ObservableCollection<PartOfWorld> PartsOfWorld { get; set; } = new ObservableCollection<PartOfWorld>();
}

public interface PartOfWorld
{
	public abstract string NameOfPartOfWorld { get; init; }
}

public class ObjectsTreeViewItem : PartOfWorld
{
	public string NameOfPartOfWorld { get; init; } = "Objects";
	public IObjects Objects { get; private set; }
	public ObjectsTreeViewItem(IObjects objects)
	{
		Objects = objects;
	}
}

/// <summary>
/// Displays top down list of all events
/// Each event has a list of objects that are related to it
/// It means it displays all objects meantioned in the 
/// action or condition method of the event
/// </summary>
public class ObjectsGroupedByEventsTreeViewItem : PartOfWorld
{
	public string NameOfPartOfWorld { get; init; } = "Objects Grouped By Events";
	public IObjects Objects { get; private set; }
	public IEvents Events { get; private set; }
	public ObservableCollection<EventWithRelatedObjectsTreeViewItem> EventsWithRelatedObjects { get; set; } = new();
	public ObjectsGroupedByEventsTreeViewItem(IObjects objects, IEvents events)
	{
		Events = events;
		Objects = objects;

		foreach (var ev in events.Collection)
		{
			EventsWithRelatedObjects.Add(new EventWithRelatedObjectsTreeViewItem(ev));
		}

		Events.Collection.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is IEvent ev)
					{
						EventsWithRelatedObjects.Add(new EventWithRelatedObjectsTreeViewItem(ev));
					}
				}
			}
			else if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is IEvent ev)
					{
						EventsWithRelatedObjects.Remove(EventsWithRelatedObjects.First(item => item.Event == ev));
					}
				}
			}
		};

	}
}

public class EventWithRelatedObjectsTreeViewItem : PartOfWorld
{
	public string NameOfPartOfWorld { get; init; } = "Events With Related Objects";
	public IEvent Event { get; private set; }
	public EventWithRelatedObjectsTreeViewItem(IEvent event_)
	{
		this.Event = event_;
	}
}

public class EventsTreeViewItem : PartOfWorld
{
	public string NameOfPartOfWorld { get; init; } = "Events";
	public ObservableCollection<EventsPartWorkTreeItem>? EventsPart { get; set; }
}


public interface EventsPartWorkTreeItem
{
	public abstract string Name { get; set; }
}

public class AllEventsWorkTreeItem : EventsPartWorkTreeItem
{
	public string Name { get; set; } = "All Events";
	public IEvents Events { get; private set; }

	public AllEventsWorkTreeItem(IEvents events)
	{
		this.Events = events;
	}
}

public class EventContainersTreeViewItem : EventsPartWorkTreeItem
{
	public string Name { get; set; } = "Event Containers";
	private Func<MainWindow?> tryToGetMainWindow;
	public ObservableCollection<EventContainerTreeViewItem> EventContainers { get; set; } = new();

	public EventContainersTreeViewItem(IEvents events, Func<MainWindow?> tryToGetMainWindow)
	{
		this.tryToGetMainWindow = tryToGetMainWindow;
		EventContainers = new ObservableCollection<EventContainerTreeViewItem>();
		foreach (var container in events.FirstLevelEventContainers)
		{
			EventContainers.Add(new EventContainerTreeViewItem(container, tryToGetMainWindow)
			{
				Name = container.Name
			});
		}

		addBindings(events);
	}

	private void addBindings(IEvents events)
	{
		foreach (var subContainerTreeItem in EventContainers)
		{
			subContainerTreeItem.Container.OnDelete += (sender, args) =>
			{
				EventContainers.Remove(subContainerTreeItem);
			};
		}

		events.FirstLevelEventContainers.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is EventContainer container)
					{
						EventContainers.Add(new EventContainerTreeViewItem(container, tryToGetMainWindow) { Name = container.Name });
					}
				}
			}
			else if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is EventContainer container)
					{
						EventContainers.Remove(EventContainers.First(item => item is EventContainerTreeViewItem itemTreeItem && itemTreeItem.Container == container));
					}
				}
			}
		};
	}

	/// <summary>
	/// Opens a dialog to create a new event container
	/// </summary>
	/// <returns></returns>
	public async void AddNewEventContainer_Click()
	{
		MainWindow? mainWindow = tryToGetMainWindow();
		if (mainWindow is null)
			return;

		var createEventContainer = new Action<string>(name =>
		{
			try
			{
				mainWindow.WorldActions?.CreateNewEventContainer(name, null);
			}
			catch (ConceptWithIDAlreadyExistsException e)
			{
				Log.Error(e, "Event container with this name already exists");
				mainWindow.Notifications.ShowErrorNotification("event_container_with_this_name_already_exists_title", "event_container_with_this_name_already_exists");
				return;
			}
		});
		await new CreatorNewEventContainerDialogView(createEventContainer).ShowDialog(mainWindow);
	}
}

public class EventContainerTreeViewItem : ViewModelBase, EventsPartWorkTreeItem, INotifyPropertyChanged
{
	public ObservableCollection<object> Items { get; set; } = new();
	public EventContainer Container { get; set; }
	private Func<MainWindow?> tryToGetMainWindow;
	public EventContainerTreeViewItem(EventContainer container, Func<MainWindow?> tryToGetMainWindow)
	{
		this.Container = container;
		this.tryToGetMainWindow = tryToGetMainWindow;

		foreach (var subContainerRef in container.SubContainers)
		{
			if (subContainerRef.TryGetConcept() is EventContainer subContainer)
			{
				Items.Add(new EventContainerTreeViewItem(subContainer, tryToGetMainWindow) { Name = subContainer.Name });
			}
		}
		foreach (var obj in container.Events)
		{
			if (obj is Reference<IEvent> refEvent)
			{
				var ev = refEvent.TryGetConcept();
				if (ev is not null)
					Items.Add(ev);
			}
		}

		addBindings(container, tryToGetMainWindow);
	}

	private void addBindings(EventContainer container, Func<MainWindow?> tryToGetMainWindow)
	{
		container.OnIdChanged += (sender, args) =>
		{
			Name = container.Name;
		};

		foreach (var subContainerRef in container.SubContainers)
		{
			if (subContainerRef.TryGetConcept() is EventContainer subContainer)
			{
				subContainer.OnDelete += (sender, args) =>
				{
					Items.Remove(Items.First(item => item is EventContainerTreeViewItem itemTreeItem && itemTreeItem.Container == subContainer));
				};
			}
		}

		container.SubContainers.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is Reference<EventContainer> subContainerRef)
					{
						var subContainer = (EventContainer)subContainerRef.TryGetConcept()!;
						if (subContainer is not null)
						{
							subContainer.OnDelete += (sender, args) =>
							{
								Items.Remove(Items.First(item => item is EventContainerTreeViewItem itemTreeItem && itemTreeItem.Container == subContainer));
							};
						}
						else
						{
							subContainerRef.OnConceptFound += (sender, args) =>
							{
								subContainer = (EventContainer)subContainerRef.TryGetConcept()!;
								subContainer.OnDelete += (sender, args) =>
								{
									Items.Remove(Items.First(item => item is EventContainerTreeViewItem itemTreeItem && itemTreeItem.Container == subContainer));
								};
							};
						}
					}
				}
			}
		};

		container.Events.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is Reference<IEvent> refEvent)
					{
						var ev = refEvent.TryGetConcept();
						if (ev is not null)
							Items.Add(ev);
					}
				}
			}
			if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is Reference<IEvent> refEvent)
					{
						var ev = refEvent.TryGetConcept();
						if (ev is not null)
							Items.Remove(ev);
					}
				}
			}
		};
	}

	interface EventOrEventContainer
	{
		public abstract string Name { get; set; }
	}

	private string name = "";
	public string Name
	{
		get => name;
		set => this.RaiseAndSetIfChanged(ref name, value);
	}

	public async void AddNewEventContainer_Click(MainWindow mainWindow)
	{
		var createEventContainer = new Action<string>(name =>
		{
			try
			{
				var newContainer = mainWindow.WorldActions?.CreateNewEventContainer(name, Container);
				if (newContainer is not null)
					Items.Add(new EventContainerTreeViewItem(newContainer!, tryToGetMainWindow) { Name = newContainer.Name });

			}
			catch (ConceptWithIDAlreadyExistsException e)
			{
				Log.Error(e, "Event container with this name already exists");
				mainWindow.Notifications.ShowErrorNotification("event_container_with_this_name_already_exists_title", "event_container_with_this_name_already_exists");
				return;
			}
		});
		await new CreatorNewEventContainerDialogView(createEventContainer).ShowDialog(mainWindow);
	}
}

public class LinearEventWorkTreeItem : EventsPartWorkTreeItem
{
	public string Name { get; set; } = "Linear Events";
	public ObservableCollection<LinearEvent> LinearEvents { get; set; }

	public LinearEventWorkTreeItem()
	{
		LinearEvents = new ObservableCollection<LinearEvent>();
	}
}

public class VisualizationsTreeViewItem : PartOfWorld
{
	public string NameOfPartOfWorld { get; init; } = "Visualtisations";
	public ObservableCollection<VisualisationPart> VisualisationParts { get; set; } = new ObservableCollection<VisualisationPart>();
}

public class VisualisationPart
{
	public string Name { get; set; } = "Visualisation Part";
}

public class EventSequenceVisualisation : VisualisationPart
{
	public IEvents Events;

	public EventSequenceVisualisation(IEvents events)
	{
		Name = "Event Sequence";
		Events = events;
	}
}



public class WorksTreeViewItem : WorldOrWorksTreeViewItem
{
	public string Name { get; set; } = "Works";

	public ObservableCollection<WorkTreeViewItem> Works { get; set; } = new ObservableCollection<WorkTreeViewItem>();
}

public class WorkTreeViewItem
{
	public string Name { get; set; } = "";
}


/// <summary>
/// TODO implement and move out
/// </summary>
public class LinearEvent
{
	public const string ID_PREFIX = "@event_linear:";

	public string Name { get; set; } = "";
}
