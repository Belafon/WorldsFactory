using System.Collections.ObjectModel;
using Avalonia.Controls;

namespace WorldsFactory.screen.panelCards
{
	/// <summary>
	/// Interface of <see cref="PanelCardViewModel"/>, 
	/// this represents a container for the cards,
	/// that shows one of them and allows to switch between them.
	/// </summary>
	public interface IPanelCardsContainer
	{
		public ObservableCollection<Card> cards { get; }
		public void AppendCard(UserControl card, string name);
		public void AddEmptyCard();
		public void AddCard(Card card);
		public void CloseCard(Card card);
		public void AddCard(Card card, Side side);
		public void AddCard(Card card, Side side, int widthOfNewPanel);
		public Card? FocusedCard { get; set; }
		public void CloseAllCards(Card? card = null);
		public void Delete();
	}
}