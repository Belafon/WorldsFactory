using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using Serilog;
using WorldsFactory.project;
using WorldsFactory.screen;
using WorldsFactory.world.objects;
using WorldsFactory.world.createNew;
using WorldsFactory.world.library;
using System.Collections.ObjectModel;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.createNew;


public partial class CreatorObjectCardView : UserControl
{
	private CreatorObjectCardViewModel model;
	public AutoCompleteBox TypeTextAutoCompleteBox;
	public AutoCompleteBox PrefixesAutoCompleteBox;
	internal Action<string, WFType> OnObjectCreated { get; } = (name, type) => { };
	
	public CreatorObjectCardView()
	{
		throw new NotImplementedException();
	}
	public CreatorObjectCardView(HashSet<string> prefixes, string prefixHint, ILibrary library, Action<string, WFType> onObjectCreated)
	{
		OnObjectCreated = onObjectCreated;
		InitializeComponent();
		DataContext = model = new CreatorObjectCardViewModel(prefixes, library, this);
		PrefixesAutoCompleteBox = this.FindControl<AutoCompleteBox>("Prefixes")!;
		PrefixesAutoCompleteBox.Text = prefixHint;
		PrefixesAutoCompleteBox.PointerEntered += (s, e) =>
		{
			PrefixesAutoCompleteBox.IsDropDownOpen = true;
		};
		TypeTextAutoCompleteBox = this.FindControl<AutoCompleteBox>("TypeTextBox")!;
	}

	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}


	private void CreateItem_Clicked(object sender, RoutedEventArgs e)
	{
		try
		{
			if(!model.IsTypeValid)
			{
				TypeTextAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
				TypeTextAutoCompleteBox.Focus();
				return;
			}

			string? prefix = PrefixesAutoCompleteBox.Text;
			if (prefix is null || !model.Prefixes.Contains(prefix))
			{
				PrefixesAutoCompleteBox.Text = "";
				PrefixesAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
				PrefixesAutoCompleteBox.Focus();
				return;
			}

			TextBox idTextBox = this.FindControl<TextBox>("IdTextBox")!;
			string? id = idTextBox.Text;
			if (id is null || id.Equals(""))
			{
				idTextBox.Focus();
				return;
			}

			if (VisualRoot is Window window)
			{
				window.Close();
			}
			var type = model.Library.AllTypes.First(type => type.Id == model.Type);
			if (type is null)
				throw new Exception("Type is null");
			OnObjectCreated(id, type);
		}
		catch (InvalidIdFormatException)
		{
			Log.Warning("Invalid id format");
		}
	}
}

public class CreatorObjectCardViewModel : ViewModelBase
{
	public ILibrary Library { get; }
	CreatorObjectCardView view;
	
	public CreatorObjectCardViewModel(HashSet<string> prefixes, ILibrary library, CreatorObjectCardView view)
	{
		this.view = view;
		Library = library;
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


	private bool isTypeValid = false;
	public bool IsTypeValid
	{
		get => isTypeValid;
		set
		{
			if (isTypeValid == value)
				return;
			isTypeValid = value;

			if (isTypeValid)
				view.TypeTextAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				view.TypeTextAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
		}
	}


	private string type = "";
	public string Type
	{
		get => type;
		set
		{
			if (type.Equals(value))
				return;

			type = value;

			if (Library.AllTypes.Any(type => type.Id == value))
			{
				IsTypeValid = true;
				view.TypeTextAutoCompleteBox.SelectedItem = Library.AllTypes.First(type => type.Id == value);
			}
			else IsTypeValid = false;
			this.RaiseAndSetIfChanged(ref type, value);
		}
	}
}
