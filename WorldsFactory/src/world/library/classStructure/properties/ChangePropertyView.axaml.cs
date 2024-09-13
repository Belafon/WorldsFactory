using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Text.RegularExpressions;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Media;
using Microsoft.VisualBasic;
using NP.Utilities;
using ReactiveUI;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;
public partial class ChangePropertyView : UserControl
{
	public ChangePropertyViewModel Model { get; init; }
	public ChangePropertyView()
	{
		throw new NotImplementedException();
	}
	public ChangePropertyView(ILibrary library, IClass clazz, Property? property = null)
	{
		InitializeComponent();
		var setPropertyTypeAutoCompleteBox = this.FindControl<AutoCompleteBox>("SetPropertyTypeAutoCompleteBox")!;
		var setPropertiesNameAutoTextBox = this.FindControl<TextBox>("SetPropertiesNameAutoCompleteBox")!;
		var changePropertiesViewControl = this.FindControl<UserControl>("ChangePropertyViewControl")!;
		DataContext = Model = new ChangePropertyViewModel(clazz, setPropertyTypeAutoCompleteBox, setPropertiesNameAutoTextBox, changePropertiesViewControl, property, library);
	}
}

public class ChangePropertyViewModel : ViewModelBase
{
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
				setPropertyTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				setPropertyTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
		}
	}
	private bool isNameValid;
	public bool IsNameValid
	{
		get => isNameValid;
		set
		{
			if (isNameValid == value)
				return;
			isNameValid = value;

			if (isNameValid)
				setPropertiesNameAutoTextBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				setPropertiesNameAutoTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
		}
	}

	public ILibrary Library { get; init; }
	private WFType? lastType;
	private string lastName;
	public IClass Clazz { get; init; }
	private AutoCompleteBox setPropertyTypeAutoCompleteBox { get; init; }
	private TextBox setPropertiesNameAutoTextBox { get; init; }
	public Property? Property { get; private set; }
	private string typeInput;
	public string TypeInput
	{
		get => typeInput;
		set
		{
			if (typeInput.Equals(value))
				return;

			if (Library.AllTypes.Any(type => type.Id == value))
			{
				IsTypeValid = true;
				setPropertyTypeAutoCompleteBox.SelectedItem = Library.AllTypes.First(type => type.Id == value);
			}
			else IsTypeValid = false;
			this.RaiseAndSetIfChanged(ref typeInput, value);
		}
	}

	private string nameInput = "";
	public string NameInput
	{
		get => nameInput;
		set
		{
			if (nameInput.Equals(value))
				return;

			if (isPropertyNameStructureValid(value))
				IsNameValid = true;
			else IsNameValid = false;

			nameInput = value;
			this.RaiseAndSetIfChanged(ref nameInput, value);
		}
	}

	private bool isPropertyNameStructureValid(string value)
	{
		if (value.IsNullOrEmpty())
			return false;
		var match = Property.PropertyNameStructurePattern.Match(value);
		var propertyName = match.Groups[1].Value;

		if (Clazz.Properties.Any(property => property.Name.Equals(value) && (property.ArrayBrackets.Equals(match.Groups[2].Value))))
			return false;

		if (propertyName.IsNullOrEmpty())
			return false;
		return true;
	}

	private string propertiesIdPrefix;
	public string PropertiesIdPrefix
	{
		get => propertiesIdPrefix;
		set => this.RaiseAndSetIfChanged(ref propertiesIdPrefix, value);
	}
	int id = -1;
	public static int next_id = 0;
	public ChangePropertyViewModel(
		IClass clazz,
		AutoCompleteBox setPropertyTypeAutoCompleteBox,
		TextBox setPropertiesNameAutoTextBox,
		UserControl changePropertiesViewControl,
		Property? property,
		ILibrary library
		)
	{
		setPropertyTypeAutoCompleteBox.PointerEntered += (sender, args) =>
		{
			if (IsTypeValid)
				return;
			setPropertyTypeAutoCompleteBox.IsDropDownOpen = true;
		};

		Library = library;

		this.Clazz = clazz;
		this.Property = property;
		this.lastType = property is null ? null : property.Type;
		this.lastName = property is not null ? property.Type.GetPostfixId : "";

		this.setPropertyTypeAutoCompleteBox = setPropertyTypeAutoCompleteBox;
		this.setPropertiesNameAutoTextBox = setPropertiesNameAutoTextBox;

		changePropertiesViewControl.LostFocus += OnFocusLost!;
		typeInput = "";
		propertiesIdPrefix = "";

		PropertiesIdPrefix = Property.GetPrefix(clazz.GetPostfixId());
		if (property is not null)
		{
			IsTypeValid = true;
			TypeInput = property.Type.Id;
			nameInput = property.FullName;
			isNameValid = true;
		}
		else
		{
			nameInput = "";
			isNameValid = false;
			isTypeValid = false;
		}

		if (property is not null)
		{
			id = next_id++;
			property.PropertyChanged += PropertyChangedHandler!;

			if (property.Type is IReference typeRef)
			{
				typeRef.OnIdChanged += OnIdChangedHandler!;
				
			}
		}




	}

	~ChangePropertyViewModel()
	{
		onClose();
	}

	private void onClose()
	{
		if (Property is not null)
		{
			Property.PropertyChanged -= PropertyChangedHandler!;
			if (Property.Type is IReference typeRef)
			{
				typeRef.OnIdChanged -= OnIdChangedHandler!;
			}
		}
	}

	void PropertyChangedHandler(object sender, PropertyChangedEventArgs args)
	{
		Log.Information("Property {0} changed, id {1}", Property!.Name, id);
		var property = sender as Property;
		if (args.PropertyName == "Type")
		{
			TypeInput = property!.Type.Id;
			if (property.Type is IReference typeRef)
			{
				typeRef.OnIdChanged += OnIdChangedHandler!;
			}
		}
	}

	void OnIdChangedHandler(object sender, EventArgs args)
	{
		Log.Information("Id changed, id {0}", id);
		var typeRef = sender as IReference;
		if (typeRef != null)
		{
			TypeInput = typeRef.Id;
		}
	}



	private void OnFocusLost(object sender, RoutedEventArgs args)
	{
		if (!IsTypeValid || !IsNameValid)
		{
			PropertiesIdPrefix = lastName;
			return;
		}

		var propertiesName = NameInput;
		var item = setPropertyTypeAutoCompleteBox.SelectedItem;

		if (item is not WFType)
			throw new UnhandledErrorException("Selected item is not a type");

		if (item is WFType type)
		{
			if (Property is null)
			{
				// add a new property
				var tags = new ObservableCollection<string>();
				var newProperty = Clazz.CreateNewProperty(propertiesName, type, tags);
				Property = newProperty;
			}
			else
			{
				var match = Property.PropertyNameStructurePattern.Match(propertiesName);
				if (!match.Success)
					throw new ArgumentException("Property name is not valid");
				var nameDecl = match.Groups[1].Value;
				var arrayBrackets = match.Groups[2].Value;
				Property.Type = type;
				Property.StringArrayBrackets = arrayBrackets;
				Property.Rename(nameDecl, Clazz.GetPostfixId());
			}
			lastType = type;
			lastName = propertiesName;
		}
	}
}



