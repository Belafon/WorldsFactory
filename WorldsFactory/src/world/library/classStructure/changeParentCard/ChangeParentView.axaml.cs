using System.Runtime.InteropServices;
using System.Collections.ObjectModel;
using System.Security.Policy;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using WorldsFactory.screen;
using WorldsFactory.world.library.classStructure.types;
using ReactiveUI;
using Avalonia.Input;
using Serilog;
using Avalonia.VisualTree;
using Avalonia.Media;
using Avalonia.Interactivity;

namespace WorldsFactory.world.library.classStructure;
public partial class ChangeParentView : UserControl
{
	public ChangeParentView()
	{
		throw new NotImplementedException();
	}
	public ChangeParentView(ILibrary library, IClass clazz)
	{
		InitializeComponent();
		var setParentAutoCompleteBox = this.FindControl<AutoCompleteBox>("SetParentAutoCompleteBox")!;
		DataContext = new ChangeParentViewModel(library, clazz, setParentAutoCompleteBox);
	}
}

public class ChangeParentViewModel : ViewModelBase
{
	public ILibrary Library { get; init; }

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
				setParentAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				setParentAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
		}
	}
	private string text;
	public string Text
	{	
		get => text;
		set
		{
			string text = "";
			if (Library.Classes.Any(c => c.Id.Equals(value)))
			{
				var parentClazz = Library.Classes.First(c => c.Id.Equals(value));
				if (parentClazz != clazz)
				{
					clazz.Parent = parentClazz.GetReference();
					text = value;
					IsTypeValid = true;
				}
				else IsTypeValid = false;
			} else if (value.Equals(""))
			{
				clazz.Parent = null;
				text = value;
				IsTypeValid = true;

			}
			else IsTypeValid = false;
			RaiseAndSetIfChanged(ref this.text, value);
		}
	}
	private IClass clazz;
	private AutoCompleteBox setParentAutoCompleteBox;
	public ChangeParentViewModel(ILibrary library, IClass clazz, AutoCompleteBox setParentAutoCompleteBox)
	{
		this.setParentAutoCompleteBox = setParentAutoCompleteBox;
		Library = library;
		if (clazz.Parent is not null)
		{
			text = clazz.Parent.Id;
			IsTypeValid = true;
		}
		else
		{
			text = "";
			Text = Class.ID_PREFIX;
			IsTypeValid = false;
		}
		this.clazz = clazz;
		this.setParentAutoCompleteBox.PointerEntered += (sender, args) =>
		{
			if (IsTypeValid)
				return;
			setParentAutoCompleteBox.IsDropDownOpen = true;
		};
		this.setParentAutoCompleteBox.LostFocus += OnFocusLost!;

	}

	public void OnFocusLost(object sender, RoutedEventArgs e)
	{
		if (!isTypeValid)
			return;

	}
}