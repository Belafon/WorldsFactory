using System;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using WorldsFactory.keyListener;
using WorldsFactory.screen;
using WorldsFactory.screen.panelCards;
using Avalonia.Markup.Xaml;
using WorldsFactory.mainWindow;
using WorldsFactory.world;
using WorldsFactory.world.createNew;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.project;
using WorldsFactory.world.objects;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.world.events;
using WorldsFactory.world.ids;
using WorldsFactory.works.storyInterpreterWork.export.ui;
using System.Collections.ObjectModel;
using Avalonia.LogicalTree;
using Serilog;

namespace WorldsFactory;

/// <summary>
/// Main window of the application, 
/// when the last MainWindow is closed, 
/// the application exits.
/// </summary>
public partial class MainWindow : Window
{
	MainWindowViewModel model;
	public ICreateNewWorldsItemWithIdActions? WorldActions { get; private set; }
	public ICurrentlyOpenedProject? OpenedProject { get; private set; }
	private KeyListener keyListener = new KeyListener();
	public int Id { get; private set; }
	private static int nextId = 0;
	public MainGridCell RootContentGrid { get; private set; }
	private DockPanel windowContent;
	public Notifications Notifications { get; set; }
	public Stack<Card> FocusCardHistory = new Stack<Card>();
	
	// BUG avoid memory leaks
	// TODO remove cards when they are closed
	public Dictionary<UserControl, IPanelCardsContainer> CardsContainers = new Dictionary<UserControl, IPanelCardsContainer>();
	public HashSet<IPanelCardsContainer> Containers = new HashSet<IPanelCardsContainer>();

	/// <summary>
	/// Required by Avalonia, not implemented
	/// </summary>
	public MainWindow()
	{
		throw new NotImplementedException();
	}

	public ProjectActions ProjectActions { get; private set; }

	/// <summary>
	/// Initializes the main window with the given user control as the root content.
	/// </summary>
	/// <param name="startUserControl"></param>
	/// <param name="tabName"></param>
	public MainWindow(UserControl startUserControl, string tabName, ProjectActions projectActions)
	{
		InitializeComponent();
		Icon = null;
		DataContext = model = new MainWindowViewModel(this);
		ProjectActions = projectActions;

		Notifications = new Notifications(this);

		Width = Screens.Primary!.Bounds.Width * 8 / 10;
		Height = Screens.Primary!.Bounds.Height * 8 / 10;

		Padding = new Thickness(
			OffScreenMargin.Left,
			OffScreenMargin.Top,
			OffScreenMargin.Right,
			OffScreenMargin.Bottom);

		PanelCardView rootPanelCard = new PanelCardView(
			startUserControl,
			tabName,
			this)
		{
			Name = "rootPanelCard",
			HorizontalAlignment = Avalonia.Layout.HorizontalAlignment.Stretch,
			VerticalAlignment = Avalonia.Layout.VerticalAlignment.Stretch
		};


		RootContentGrid = rootPanelCard;
		windowContent = this.FindControl<DockPanel>("WindowContent")!;
		windowContent.Children.Add(rootPanelCard);

		Id = nextId++;
	}

	/// <summary>
	/// Closes all cards in all containers
	/// <see cref="PanelCardView"/>
	/// </summary>
	/// <param name="card"></param>
	public void ClearAllCards(Card card)
	{
		int countContainers = Containers.Count();
		for (int i = 0; i < countContainers - 1; i++)
		{
			var container = Containers.First();
			container.CloseAllCards();
		}
		Containers.First().CloseAllCards(card);
	}



	protected override void OnKeyDown(KeyEventArgs e)
	{
		keyListener.OnKeyChanged(KeyAction.down, this, e);
		base.OnKeyDown(e);
	}
	protected override void OnKeyUp(KeyEventArgs e)
	{
		keyListener.OnKeyChanged(KeyAction.up, this, e);
		base.OnKeyUp(e);
	}
	protected override void OnLostFocus(RoutedEventArgs e)
	{
		keyListener.OnLostFocusClear();
		base.OnLostFocus(e);
	}
	protected override void OnClosed(EventArgs e)
	{
		Windows.closeMainWindow(Id);
		base.OnClosed(e);
	}

	private PanelCardView? findFirstParentPanelCardView(Control control)
	{
		if (control is PanelCardView panelCardView)
			return panelCardView;
		if (control.Parent is null)
			return null;
		return findFirstParentPanelCardView((Control)control.Parent);
	}
	private void addControlToContentByCeilPosition(int column, int row, Side side, UserControl control, string tabName)
	{
		// TODO implement
		throw new NotImplementedException();
	}
	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}

	/// <summary>
	/// Removes all cells and adds the given cell as the root content.
	/// </summary>
	/// <param name="cell"></param>
	public void SetRootContent(MainGridCell cell)
	{
		RootContentGrid = cell;
		windowContent.Children.Clear();
		windowContent.Children.Add((Control)cell);
	}


	public async Task ShowDialogAsyncToCreateWorldItem(string title, HashSet<string> prefixes, string prefixHint, Action<string, string> createWorldItem)
	{
		await new CreatorNewWorldsItemWithIdDialog(
			new CreatorWorldsItemWithIdCardView(prefixes, prefixHint, createWorldItem), title
		).ShowDialog(this);
	}

	/// <summary>
	/// Changes the program state only
	/// </summary>
	/// <param name="worldActions"></param>
	/// <param name="project"></param>
	public void BindWithProject(ICreateNewWorldsItemWithIdActions worldActions, ICurrentlyOpenedProject project)
	{
		WorldActions = worldActions;
		model.IsWindowBindedWithProject = true;
		this.OpenedProject = project;
	}

	internal async Task showDialogAsyncToCreateObject(string title, HashSet<string> prefixes, string prefixHint, Action<string, WFType> createWorldItem)
	{
		await new CreatorNewWorldsItemWithIdDialog(
			new CreatorObjectCardView(prefixes, prefixHint, WorldActions!.Library, createWorldItem), title
		)
		{
			Width = 700
		}.ShowDialog(this);
	}
}

public enum Side
{
	Left, Top, Right, Bottom
}

public class MainWindowViewModel : ViewModelBase
{
	private bool isProjectBinded = false;
	public bool IsWindowBindedWithProject
	{
		get { return isProjectBinded; }
		set
		{
			isProjectBinded = value;
			RaisePropertyChanged(nameof(IsWindowBindedWithProject));
		}
	}

	MainWindow mainWindow;
	public MainWindowViewModel(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	public async void CreateNewClass_Clicked()
	{
		if (!IsWindowBindedWithProject)
		{
			Assert.Fail("Try to create new Class even the window is not binded with a project yet.");
			return;
		}

		await mainWindow.ShowDialogAsyncToCreateWorldItem(
			"Create new class",
			new HashSet<string>() { "@class:" },
			Class.ID_PREFIX,
			(prefixId, postfixId) =>
			{
				try
				{
					mainWindow.WorldActions!.CreateNewClass(prefixId, postfixId);
				}
				catch (ConceptWithIDAlreadyExistsException)
				{
					mainWindow.Notifications.ShowErrorNotification("cannot_create_new_class_title", "empty");
				}
			}
		);
	}

	public async void CreateNewObject_Clicked()
	{
		if (!IsWindowBindedWithProject)
		{
			Assert.Fail("Try to create new Object even the window is not binded with a project yet.");
			return;
		}
		await mainWindow.showDialogAsyncToCreateObject(
			"Create new object",
			new HashSet<string>() { "@object:" },
			WFObject.ID_PREFIX,
			(name, type) =>
			{
				try
				{
					mainWindow.WorldActions!.CreateNewObject(name, type);
				}
				catch (ConceptWithIDAlreadyExistsException)
				{
					mainWindow.Notifications.ShowErrorNotification("cannot_create_new_object_title", "empty");
				}
			}
		);
	}

	public async void CreateNewEvent_Clicked()
	{
		if (!IsWindowBindedWithProject)
		{
			Assert.Fail("Try to create new Event even the window is not binded with a project yet.");
			return;
		}
		await mainWindow.ShowDialogAsyncToCreateWorldItem(
			"Create new event",
			new HashSet<string>() { "@event:" },
			Event.ID_PREFIX,
			(prefixId, postfixId) =>
			{
				try
				{
					mainWindow.WorldActions!.CreateNewEvent(prefixId, postfixId);
				}
				catch (ConceptWithIDAlreadyExistsException)
				{
					mainWindow.Notifications.ShowErrorNotification("cannot_create_new_event_title", "empty");
				}
			}
		);
	}

	public async void CreateNewLinearEvent_Clicked()
	{
		if (!IsWindowBindedWithProject)
		{
			Assert.Fail("Try to create new Linear event even the window is not binded with a project yet.");
			return;
		}
		await mainWindow.ShowDialogAsyncToCreateWorldItem(
			"Create new linear event",
			new HashSet<string>() { "@linearEvent:" },
			LinearEvent.ID_PREFIX,
			(prefixId, postfixId) =>
			{
				try
				{
					mainWindow.WorldActions!.CreateNewLinearEvent(prefixId, postfixId);
				}
				catch (ConceptWithIDAlreadyExistsException)
				{
					mainWindow.Notifications.ShowErrorNotification("cannot_create_new_linear_event_title", "empty");
				}
			}
		);
	}

	public void ExportAllData_Clicked()
	{
		if (!IsWindowBindedWithProject)
		{
			Assert.Fail("Try to create new Linear event even the window is not binded with a project yet.");
			return;
		}

		var container = GetLastExistingFocusedCard()?.container;
		if (container is not null)
		{
			container.AppendCard(new ExportStoryInterpretersDataView(mainWindow.OpenedProject!), "Export");
		}
		else
		{
			container = mainWindow.CardsContainers.First().Value;
			container.AppendCard(new ExportStoryInterpretersDataView(mainWindow.OpenedProject!), "Export");
		}
	}

	public void OpenProject_Clicked()
	{
		var lastFocusedExistingCard = GetLastExistingFocusedCard();
		if (lastFocusedExistingCard is not null)
			mainWindow.ProjectActions.ShowOpenProjectCardView(lastFocusedExistingCard);
		else
		{
			var card = mainWindow.CardsContainers.First().Value.cards.First();
			if (card is not null)
				mainWindow.ProjectActions.ShowOpenProjectCardView(card);
			else Log.Error("Cannot open Open Project card view");
		}
	}

	public void CreateNewProject_Clicked()
	{
		var lastFocusedExistingCard = GetLastExistingFocusedCard();
		if (lastFocusedExistingCard is not null)
			mainWindow.ProjectActions.ShowCreateNewProjectCardView(lastFocusedExistingCard);
		else
		{
			var card = mainWindow.CardsContainers.First().Value.cards.First();
			if (card is not null)
				mainWindow.ProjectActions.ShowCreateNewProjectCardView(card);
			else Log.Error("Cannot open New Project card view");
		}

	}

	public void ViewProjectOverview_Clicked()
	{
		var lastFocusedExistingCard = GetLastExistingFocusedCard();

		if (lastFocusedExistingCard is not null){
			mainWindow.ProjectActions.ShowProjectOverviewCardView(lastFocusedExistingCard, mainWindow);
		}
		else
		{
			var card = mainWindow.CardsContainers.First().Value.cards.First();
			if (card is not null)
				mainWindow.ProjectActions.ShowProjectOverviewCardView(card, mainWindow);
			else Log.Error("Cannot open Project Overview card view");
		}
	}


	/// <summary>
	/// Gets the last focused card that is not closed.
	/// </summary>
	/// <returns></returns>
	public Card? GetLastExistingFocusedCard()
	{
		if(mainWindow.FocusCardHistory.Count == 0)
		{
			return null;
		}
		var last = mainWindow.FocusCardHistory.Peek();
		while (last.IsClosed)
		{
			if (mainWindow.FocusCardHistory.Count == 0)
			{
				return null;
			}
			mainWindow.FocusCardHistory.Pop();
			last = mainWindow.FocusCardHistory.Peek();
		}
		return last;
	}
}
