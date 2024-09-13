
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Reactive;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using NP.Utilities;
using ReactiveUI;
using WorldsFactory.screen.panelCards.cards;

namespace WorldsFactory.screen.panelCards;

/// <summary>
/// This is ViewModel for the <see cref="PanelCardView"/>. 
/// It is a container for cards. 
/// Selected card is displayed.
/// </summary>
internal class PanelCardViewModel : ViewModelBase, IPanelCardsContainer
{
	public ObservableCollection<Card> cards { get; } = new ObservableCollection<Card>();
	private UserControl? cardBody;
	public UserControl? CardBody
	{
		get => cardBody;
		set => this.RaiseAndSetIfChanged(ref cardBody, value);
	}
	private Card? focusedCard;
	public Card? FocusedCard
	{
		get => focusedCard;
		set
		{
			if (focusedCard is not null)
				focusHistory.Add(focusedCard);
			if (value is not null)
				this.CardBody = value.cardBody;
			else this.CardBody = null;
			this.RaiseAndSetIfChanged(ref focusedCard, value);
		}
	}
	private MainWindow mainWindow;
	private List<Card> focusHistory = new List<Card>();
	internal PanelCardView panel;
	public PanelCardViewModel(MainWindow mainWindow, PanelCardView panel)
	{
		this.mainWindow = mainWindow;
		this.panel = panel;
		AddEmptyCard();
		mainWindow.Containers.Add(this);
	}
	public PanelCardViewModel(MainWindow mainWindow, Card card, PanelCardView panel)
	{
		this.mainWindow = mainWindow;
		this.panel = panel;
		AddCard(card);
		mainWindow.Containers.Add(this);
	}
	public PanelCardViewModel(
		UserControl cardBody, string name,
		MainWindow mainWindow,
		PanelCardView panel)
	{
		this.mainWindow = mainWindow;
		this.panel = panel;

		AppendCard(cardBody, name);
		mainWindow.Containers.Add(this);
	}

	public void AddEmptyCard()
	{
		AppendCard(new EmptyCardView(), "EmptyCard");
	}
	public void AppendCard(UserControl card, string name)
	{
		Card newCard = new Card(name, card, this);
		mainWindow.CardsContainers.Add(card, newCard.container!);
		cards.Add(newCard);
		FocusedCard = newCard;
	}
	public void AddCard(Card card, int position)
	{
		card.container = this;
		cards.Insert(position, card);
		FocusedCard = card;
		mainWindow.CardsContainers.Add(card.cardBody, card.container);
	}
	public void AddCard(Card card)
	{
		cards.Add(card);
		if (FocusedCard != card)
			FocusedCard = card;
		card.container = this;
		mainWindow.CardsContainers.Add(card.cardBody, card.container);
	}
	public void AddCard(Card card, Side side)
	{
		var panelCard = new PanelCardView(mainWindow, card);
		panel.SplitPanel(panelCard, side);
	}

	public void AddCard(Card card, Side side, int widthOfNewPanel)
	{
		var panelCard = new PanelCardView(mainWindow, card);
		panel.SplitPanel(panelCard, side);
	}


	public void CloseCard(Card card)
	{
		removeCardFromHistory(card);
		removeCardFromContainer(card);
		setFocusedCard();
	}
	private void removeCardFromHistory(Card card)
	{
		focusHistory.RemoveAll(x => x == card);
		mainWindow.CardsContainers.Remove(card.cardBody);
	}
	private void removeCardFromContainer(Card card)
	{
		cards.Remove(card);
		if (cards.Count == 0)
		{
			panel.RemoveItself();
		}
	}

	private void setFocusedCard()
	{
		if (cards.Count == 0)
		{
			if (this.FocusedCard is not null)
				this.FocusedCard = null;
			return;
		}

		Card? focusedCard = this.FocusedCard;
		if (this.FocusedCard is null)
		{

			if (focusHistory.Count > 0)
			{
				focusedCard = focusHistory[focusHistory.Count - 1];
				focusHistory.RemoveAt(focusHistory.Count - 1);
			}
			else
			{
				focusedCard = cards[cards.Count - 1];
			}

			if (focusedCard != this.FocusedCard)
				this.FocusedCard = focusedCard;
		}
	}

	public void CloseAllCards(Card? card = null)
	{
		var cardCount = cards.Count;

		if (card is not null)
			AddCard(card);

		for (int i = 0; i < cardCount; i++)
		{
			CloseCard(cards[0]);
		}
	}

	public void Delete()
	{
		CloseAllCards();
		if (mainWindow.Containers.Contains(this))
			mainWindow.Containers.Remove(this);
	}
}
