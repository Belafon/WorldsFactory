using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Markup.Xaml;
using ReactiveUI;

namespace WorldsFactory.screen.panelCards
{
	/// <summary>
	/// Displays a tab for a card. Each card in a <see cref="PanelCardView"/> has a tab.
	/// It allows to switch between cards, or close them.
	/// Dragging the tab is implemented in <see cref="PanelCardView"/>.
	/// </summary>
	public partial class CardsTabView : UserControl
	{
		private TextBlock? cardsTabViewTitle;
		public UserControl? Card { get; private set; }
		public UserControl? PanelCard;
		
		public CardsTabView(){}
		public CardsTabView(UserControl card, string title){
			InitializeComponent();
			this.Card = card;
			cardsTabViewTitle = this.FindControl<TextBlock>("CardsTabViewTitle");
			this.Title = title;
		}

		public string Title {
			get => cardsTabViewTitle!.Text!;
			set => cardsTabViewTitle!.Text = value; 
		}

		protected override void OnPointerReleased(PointerReleasedEventArgs e){
			Cursor = new Cursor(StandardCursorType.Arrow);    
			base.OnPointerReleased(e);
		}
	
		private void InitializeComponent(){
			AvaloniaXamlLoader.Load(this);
		}
	}
}