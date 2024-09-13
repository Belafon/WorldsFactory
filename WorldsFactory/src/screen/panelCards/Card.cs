using System;
using System.Collections.Generic;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Media;
using Avalonia.Styling;
using ReactiveUI;
using Serilog;

namespace WorldsFactory.screen.panelCards;
/// <summary>
/// Represents a card in a cards system
/// A card can be displayed in a panel card <see cref="PanelCardView"/>
/// as its content (where all info would be showed), or as a tab (just the title)
 /// in a list of current tabs <see cref="CardsTabView"/>. 
/// </summary>
public class Card : ReactiveObject
{
	/// <summary>
	/// Represents a card in a cards system
	/// </summary>
	/// <param name="tabName"></param>
	/// <param name="cardBody"></param>
	/// <param name="panelCard"></param>
	public Card(string tabName, UserControl cardBody, IPanelCardsContainer? panelCard = null)
	{
		Windows.ViewCards.Add(cardBody, this);
		this.container = panelCard;
		this.TabName = tabName;
		this.cardBody = cardBody;
		card = this;
	}
	
	private string tabName = null!;
	public string TabName { 
		get => tabName;
		set => this.RaiseAndSetIfChanged(ref tabName, value);
	}
	public UserControl cardBody { get; set; }
	public IPanelCardsContainer? container { get; set; }
	private Card? _card;
	public event EventHandler? OnClose; 
	public bool IsClosed { get; private set; } = false;
	
	public Card card
	{
		get => _card!;
		set => this.RaiseAndSetIfChanged(ref _card, value);
	}

	public async void DragCard(PointerEventArgs e)
	{
		var data = new DataObject();
		data.Set("Card", this);
		var result = await DragDrop.DoDragDrop(e, data, DragDropEffects.Move);
	}


	public void CloseItself()
	{
		IsClosed = true;

		if (container is not null)
		{
			container.CloseCard(this);
			container = null;
		}
		else
		{
			Log.Warning("Card-closeItself: panelCard is null");
		}
		
		OnClose?.Invoke(this, EventArgs.Empty);
	}
	
	/// <summary>
	/// Closes the card but does not call the OnClose event,
	/// It can be used to tempmorarily close the card.
	/// </summary>
	public void CloseItselfForAWhile()
	{
		if (container is not null)
		{
			container.CloseCard(this);
			container = null;
		}
		else
		{
			Log.Warning("Card-closeItself: panelCard is null");
		}
	}
}
