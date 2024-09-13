using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using NP.Utilities;
using Serilog;
using WorldsFactory.screen.panelCards;
using WorldsFactory.world.events;
using WorldsFactory.world.events.eventsInfoCard;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.objects;
using WorldsFactory.world.objects.objectInfoCard;
using WorldsFactory.world.visualisations.events.relationGraphs.relationGraphWithSubGraphs;
using WorldsFactory.world.visualisations.events.relationGraphs.simpleRelationGraph;

namespace WorldsFactory.project;

public enum PlaceToShowNewCard
{
	LastSelected,
	NewPanelCardView
}
public class OverviewProjectActions : IOverviewProjectActions
{
	public Action<IClass, PlaceToShowNewCard> OnClassSelected { get; init; }
	public Action<IEvent, PlaceToShowNewCard> OnEventSelected { get; init; }
	private ICurrentlyOpenedProject project;
	private MainWindow mainWindow;
	private IPanelCardsContainer? card;
	public OverviewProjectActions(ICurrentlyOpenedProject project, MainWindow mainWindow)
	{
		this.project = project;
		this.mainWindow = mainWindow;
		OnClassSelected += onClassSelected;
		OnEventSelected += onEventSelected;

	}

	public void SetCardContainer(IPanelCardsContainer card)
	{
		this.card = card;
	}
	private void onClassSelected(IClass clazz, PlaceToShowNewCard inNewCard)
	{
		Assert.NotNull(card);
		if (card is null)
			return;

		if (inNewCard == PlaceToShowNewCard.NewPanelCardView
			|| mainWindow.FocusCardHistory.Count < 2)
		{
			openClassOverviewInNewPanelCardView(clazz);
		}
		else
		{
			openClassOverviewInLastSelectedCard(clazz);
		}
	}

	private void onEventSelected(IEvent event_, PlaceToShowNewCard inNewCard)
	{
		Assert.NotNull(card);
		if (card is null)
			return;

		if (inNewCard == PlaceToShowNewCard.NewPanelCardView
			|| mainWindow.FocusCardHistory.Count < 2)
		{
			openEventOverviewInNewPanelCardView(event_);
		}
		else
		{
			openEventOverviewInLastSelectedCard(event_);
		}
	}

	private void openClassOverviewInLastSelectedCard(IClass clazz)
	{
		var projectOverviewCard = mainWindow.FocusCardHistory.Pop();
		var lastCard = getFirstNotOverviewProjectCardWithPanelCardView();
		if (lastCard is null)
		{
			openClassOverviewInNewPanelCardView(clazz);
			return;
		}
		mainWindow.FocusCardHistory.Push(projectOverviewCard);

		var classCard = new ClassInfoCardView(project.World.Library, clazz);
		var newCard = new Card("Class info - " + clazz.GetPostfixId(), classCard, card!);
		setOnClassDeletedAction(clazz, newCard);
		
		bindTabNameWithClassId(clazz, newCard);
		
		if (lastCard.container!.cards.Any(x => isCardInfoAboutSpecificClass(clazz, x)))
		{
			var card = lastCard.container!.cards.First(x => isCardInfoAboutSpecificClass(clazz, x));
			lastCard.container!.FocusedCard = card;
			card.cardBody.Focus();
			return;
		}
		lastCard.container!.AddCard(newCard);

	}

	private bool isCardInfoAboutSpecificClass(IClass clazz, Card card)
	{
		if (card.cardBody is ClassInfoCardView classInfoCardView)
			if (classInfoCardView.Clazz.Id.Equals(clazz.Id))
				return true;
		return false;
	}

	private void openClassOverviewInNewPanelCardView(IClass clazz)
	{
		var classCard = new ClassInfoCardView(project.World.Library, clazz);

		Card newCard = new Card("Class info - " + clazz.GetPostfixId(), classCard, card!);

		bindTabNameWithClassId(clazz, newCard);

		setOnClassDeletedAction(clazz, newCard);

		card!.AddCard(newCard, Side.Right, 300);
	}

	private static void bindTabNameWithClassId(IClass clazz, Card newCard)
	{
		var classNameChanging = new PropertyChangedEventHandler((sender, args) =>
		{
			if (args.PropertyName == "PostfixId")
				newCard.TabName = "Class info - " + clazz.GetPostfixId();
		});

		clazz.PropertyChanged += classNameChanging;
		newCard.OnClose += (sender, args) =>
		{
			clazz.PropertyChanged -= classNameChanging;
		};
	}

	private void setOnClassDeletedAction(IClass clazz, Card newCard)
	{
		EventHandler? eventHandler = null;
		eventHandler = (sender, args) =>
		{
			clazz.OnDelete -= eventHandler;
			newCard.CloseItself();
		};
		clazz.OnDelete += eventHandler;
	}

	private void openEventOverviewInLastSelectedCard(IEvent event_)
	{
		Log.Information("Opening event overview in last selected card");
		var projectOverviewCard = mainWindow.FocusCardHistory.Pop();
		var lastCard = getFirstNotOverviewProjectCardWithPanelCardView();
		if (lastCard is null)
		{
			openEventOverviewInNewPanelCardView(event_);
			return;
		}
		mainWindow.FocusCardHistory.Push(projectOverviewCard);

		var eventCard = new EventInfoCardView(event_, project.World.Events);
		var newCard = new Card("Event info - " + event_.Name, eventCard, card!);
		if (lastCard.container!.cards.Any(x => isEventInfoAboutSpecificEvent(event_, x)))
		{
			var card = lastCard.container!.cards.First(x => isEventInfoAboutSpecificEvent(event_, x));
			lastCard.container!.FocusedCard = card;
			card.cardBody.Focus();
			return;
		}
		lastCard.container!.AddCard(newCard);
	}

	private bool isEventInfoAboutSpecificEvent(IEvent event_, Card card)
	{
		if (card.cardBody is EventInfoCardView eventInfoCardView)
			if (eventInfoCardView.Event.Id.Equals(event_.Id))
				return true;
		return false;
	}


	private void openEventOverviewInNewPanelCardView(IEvent event_)
	{
		Log.Information("Opening event overview in new panel card view");
		var eventCard = new EventInfoCardView(event_, project.World.Events);
		Card newCard = new Card("Event info - " + event_.Name, eventCard, card!);
		
		setOnEventDeletedAction(event_, newCard);
		
		card!.AddCard(newCard, Side.Right, 300);
	}
	
	private void setOnEventDeletedAction(IEvent event_, Card newCard)
	{
		EventHandler? eventHandler = null;
		eventHandler = (sender, args) =>
		{
			event_.OnDelete -= eventHandler;
			newCard.CloseItself();
		};
		event_.OnDelete += eventHandler;
	}

	private Card? getFirstNotOverviewProjectCardWithPanelCardView()
	{
		Card? selectedItem = null;

		while (mainWindow.FocusCardHistory.Count > 0)
		{
			var item = mainWindow.FocusCardHistory.Pop();
			if (item.cardBody is not OverviewProjectView && item.container is not null)
			{
				selectedItem = item;
				break;
			}
		}

		if (selectedItem is not null)
		{
			mainWindow.FocusCardHistory.Push(selectedItem);
		}

		return selectedItem;
	}

	public void OnEventSequenceVisualisationSelected(EventSequenceVisualisation obj, PlaceToShowNewCard placeToShowNewCard)
	{
		if (placeToShowNewCard == PlaceToShowNewCard.NewPanelCardView)
		{
			openEventSequenceVisualisationInNewPanelCardView(obj);
		}
		else
		{
			openEventSequenceVisualisationInLastSelectedCard(obj);
		}	
	}
	
	private void openEventSequenceVisualisationInNewPanelCardView(EventSequenceVisualisation obj)
	{
		Log.Information("Opening event sequence visualisation in new panel card view");
		var graph = new RelationGraphWithSubGraphs(obj.Events, this);
		var newCard = new Card("Event sequence visualisation", graph, card!);
		card!.AddCard(newCard, Side.Right, 300);
	}

	private void openEventSequenceVisualisationInLastSelectedCard(EventSequenceVisualisation obj)
	{
		Log.Information("Opening event sequence visualisation in last selected card");
		var lastCard = getFirstNotOverviewProjectCardWithPanelCardView();
		if (lastCard is null)
		{
			openEventSequenceVisualisationInNewPanelCardView(obj);
			return;
		}

		if (lastCard.container!.cards.Any(x => x.cardBody is RelationGraphWithSubGraphs))
		{
			var card = lastCard.container!.cards.First(x => x.cardBody is RelationGraphWithSubGraphs);
			lastCard.container!.FocusedCard = card;
			card.cardBody.Focus();
			return;
		}
		var graph = new RelationGraphWithSubGraphs(obj.Events, this);
		lastCard.container!.AddCard(new Card("Event sequence visualisation", graph, card!), Side.Right, 300);
	}

	public void OnObjectSelected(WFObject wfObject, PlaceToShowNewCard placeToShowNewCard)
	{
		if (placeToShowNewCard == PlaceToShowNewCard.NewPanelCardView)
		{
			openObjectOverviewInNewPanelCardView(wfObject);
		}
		else
		{
			openObjectOverviewInLastSelectedCard(wfObject);
		}
	}

	private void openObjectOverviewInNewPanelCardView(WFObject wfObject)
	{
		Log.Information("Opening object overview in new panel card view");
		var objectCard = new ObjectInfoCardView(wfObject);
		var newCard = new Card("Object info - " + wfObject.Name, objectCard, card!);
		card!.AddCard(newCard, Side.Right, 300);
	}

	private void openObjectOverviewInLastSelectedCard(WFObject wfObject)
	{
		Log.Information("Opening object overview in last selected card");
		var lastCard = getFirstNotOverviewProjectCardWithPanelCardView();
		if (lastCard is null)
		{
			openObjectOverviewInNewPanelCardView(wfObject);
			return;
		}

		if (lastCard.container!.cards.Any(x => x.cardBody is ObjectInfoCardView))
		{
			var card = lastCard.container!.cards.First(x => x.cardBody is ObjectInfoCardView);
			lastCard.container!.FocusedCard = card;
			card.cardBody.Focus();
			return;
		}
		var objectCard = new ObjectInfoCardView(wfObject);
		lastCard.container!.AddCard(new Card("Object info - " + wfObject.Name, objectCard, card!), Side.Right, 300);
	}
}