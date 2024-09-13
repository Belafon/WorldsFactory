using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using WorldsFactory.keyListener;

namespace WorldsFactory.screen;

/// <summary>
/// This is the first loading screen of the application.
/// </summary>
public partial class StartLoadingWindow : Window
{
	public StartLoadingWindow()
	{
		SystemDecorations = SystemDecorations.None;
		InitializeComponent();
	}

	protected override void OnKeyDown(KeyEventArgs e)
	{
		base.OnKeyDown(e);
	}
	protected override void OnKeyUp(KeyEventArgs e)
	{
		base.OnKeyUp(e);
	}

	protected override void OnLostFocus(RoutedEventArgs e)
	{
		base.OnLostFocus(e);
	}
	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}
}