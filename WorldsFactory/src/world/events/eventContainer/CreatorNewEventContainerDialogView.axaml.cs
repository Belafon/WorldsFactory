using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using WorldsFactory.screen;

namespace WorldsFactory.world.events.eventContainer;

public partial class CreatorNewEventContainerDialogView : Window
{
	public CreatorNewEventContainerDialogView(Action<string> createEventContainer)
	{
		InitializeComponent();
		DataContext = new CreatorNewEventContainerDialogViewModel(createEventContainer, Close);
	}
}

public class CreatorNewEventContainerDialogViewModel : ViewModelBase
{
	private string name = "";
	public string Name { 
		get => name;
		set => this.RaiseAndSetIfChanged(ref name, value);
	}

	private Action<string> createEventContainer { get; }
	private Action close { get; }
	public CreatorNewEventContainerDialogViewModel(Action<string> createEventContainer, Action close)
	{
		this.createEventContainer = createEventContainer;
		this.close = close;
	}
	public void CreateEventContainer_Click()
	{
		if(Name.Length < 1)
			return;
		
		if(Name.Contains(" "))
			Name = Name.Replace(" ", "_" );

		createEventContainer(Name);
		this.close();
	}
}