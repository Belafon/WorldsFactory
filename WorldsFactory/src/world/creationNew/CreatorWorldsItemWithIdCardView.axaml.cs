using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Logging;
using Avalonia.Markup.Xaml;
using Serilog;

using System.Collections.ObjectModel;
using WorldsFactory.screen;

namespace WorldsFactory.world.createNew;
public partial class CreatorWorldsItemWithIdCardView : UserControl
{
	private Action<string, string> createWorldItem;
	private CreatorWorldsItemWithIdViewModel model;
	
	public CreatorWorldsItemWithIdCardView()
	{
		throw new NotImplementedException();
	}
	
	public CreatorWorldsItemWithIdCardView(
		HashSet<string> prefixes, 
		string prefixHint, 
		Action<string, string> createWorldItem)
	{
		this.createWorldItem = createWorldItem;
		InitializeComponent();
		DataContext = model = new CreatorWorldsItemWithIdViewModel(prefixes);
		AutoCompleteBox autoCompleteBox = this.FindControl<AutoCompleteBox>("PrefixesAutoCompleteBox")!;
		autoCompleteBox.Text = prefixHint;

	}
	
	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}
	
	
	private void CreateItem_Clicked(object sender, RoutedEventArgs e)
	{
		try
		{
			
			AutoCompleteBox autoCompleteBox = this.FindControl<AutoCompleteBox>("PrefixesAutoCompleteBox")!;
			string? prefix = autoCompleteBox.Text;  
			if (prefix is null || !model.Prefixes.Contains(prefix))
			{
				autoCompleteBox.Text = "";
				autoCompleteBox.Focus();
				return;
			}
			
			
			TextBox idTextBox = this.FindControl<TextBox>("IdTextBox")!;
			string? id = idTextBox.Text;
			if(id is null || id.Equals(""))
			{
				idTextBox.Focus();
				return;
			}
			
			if(VisualRoot is Window window)
			{
				window.Close();
			}
			createWorldItem(prefix, id);
		}
		catch (InvalidIdFormatException)
		{
			Log.Warning("Invalid id format");
		}
	}
}


public class CreatorWorldsItemWithIdViewModel : ViewModelBase
{	
	public CreatorWorldsItemWithIdViewModel(HashSet<string> prefixes)
	{
		this.prefixes = new ObservableCollection<string>(prefixes);
		id = "";
	}
	
	private string id;
	public string Id
	{
		get { return id; }
		set { RaiseAndSetIfChanged(ref id, value); }
	}

	private ObservableCollection<string> prefixes;
	public ObservableCollection<string> Prefixes
	{
		get { return prefixes; }
		set { RaiseAndSetIfChanged(ref prefixes, value); }
	}
}

public class InvalidIdFormatException : Exception
{
	public InvalidIdFormatException()
	{
	}

	public InvalidIdFormatException(string message)
		: base(message)
	{
	}

	public InvalidIdFormatException(string message, Exception inner)
		: base(message, inner)
	{
	}
}
