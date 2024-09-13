using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace WorldsFactory.world.createNew;
public partial class CreatorNewWorldsItemWithIdDialog : Window
{
	private DockPanel content;

	public CreatorNewWorldsItemWithIdDialog()
	{
		throw new NotImplementedException();
	}

	public CreatorNewWorldsItemWithIdDialog(UserControl cardView, string title)
	{
		InitializeComponent();
		Icon = null;
		content = this.FindControl<DockPanel>("WindowContent")!;
		content.Children.Add(cardView);
		Title = title;
	}
}