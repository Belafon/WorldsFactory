using System.Linq;
using System.Collections.ObjectModel;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Media;
using NP.Utilities;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;

public partial class CreateNewMethodView : UserControl
{

	public AutoCompleteBox setReturnTypeAutoCompleteBox;
	public TextBox setMethodsNameTextBox { get; }
	public CreateNewMethodViewModel Model { get; }
	public Button CreateButton { get; }
	public CreateNewMethodView()
	{
		throw new NotImplementedException();
	}
	public CreateNewMethodView(ILibrary library, IClass clazz, Action<IMethod> onMethodCreated)
	{
		InitializeComponent();
		setReturnTypeAutoCompleteBox = this.FindControl<AutoCompleteBox>("SetReturnTypeAutoCompleteBox")!;
		setMethodsNameTextBox = this.FindControl<TextBox>("SetMethodsNameAutoCompleteBox")!;
		CreateButton = this.FindControl<Button>("CreateNewMethodButton")!;
		DataContext = Model = new CreateNewMethodViewModel(library, clazz, onMethodCreated, this);
	}
}

public class CreateNewMethodViewModel : ViewModelBase
{
	/// <summary>
	///  this is an action that will be called when method is created
	/// it is used for example for opening change method view
	/// </summary>
	private Action<IMethod> onMethodCreated;
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
				view.setReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				view.setReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);

			if (IsValid())
				view.CreateButton.IsEnabled = true;
			else view.CreateButton.IsEnabled = false;
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
				view.setMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				view.setMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Red);

			if (IsValid())
				view.CreateButton.IsEnabled = true;
			else view.CreateButton.IsEnabled = false;
		}
	}
	private string returnTypeInput = "";
	public string ReturnTypeInput
	{
		get => returnTypeInput;
		set
		{
			if (returnTypeInput.Equals(value))
				return;

			returnTypeInput = value;

			if (Library.AllTypes.Any(type => type.Id == value))
			{
				IsTypeValid = true;
				view.setReturnTypeAutoCompleteBox.SelectedItem = Library.AllTypes.First(type => type.Id == value);
			}
			else IsTypeValid = false;
			this.RaiseAndSetIfChanged(ref returnTypeInput, value);
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

			if (clazz.Methods.Any(method => method.Name.Equals(value))
				|| value.IsNullOrEmpty() || value.Equals(""))
				IsNameValid = false;
			else IsNameValid = true;

			nameInput = value;
			this.RaiseAndSetIfChanged(ref nameInput, value);
		}
	}

	private IClass clazz;
	public ILibrary Library { get; }
	private CreateNewMethodView view;
	public ObservableCollection<ParameterCreatorView> Parameters { get; set; }

	public CreateNewMethodViewModel(
		ILibrary library,
		IClass clazz,
		Action<IMethod> onMethodCreated,
		CreateNewMethodView view,
		bool addCreateButton = false)
	{
		this.clazz = clazz;
		Library = library;
		this.view = view;
		this.onMethodCreated = onMethodCreated;

		// set visibility to hidden 
		view.CreateButton.IsEnabled = false;
		Parameters = new ObservableCollection<ParameterCreatorView>();
		Parameters.Add(new ParameterCreatorView.Builder(library)
			.IsContainedByParameters(Parameters)
			.Build());

		view.setReturnTypeAutoCompleteBox.PointerEntered += (sender, args) =>
		{
			if (IsTypeValid)
				return;
			view.setReturnTypeAutoCompleteBox.Focus();
			view.setReturnTypeAutoCompleteBox.Text = returnTypeInput;
			view.setReturnTypeAutoCompleteBox.IsDropDownOpen = true;
		};
	}

	public void CreateMethod()
	{
		if (!IsNameValid || !IsTypeValid)
		{
			if (!IsNameValid)
				view.setMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
			else view.setMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Green);
			if (!IsTypeValid)
				view.setReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
			else view.setReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			return;
		}


		// lets create new method
		WFType? type = Library.AllTypes.First(type => type.Id == ReturnTypeInput);
		if (type == null)
		{
			Log.Error("CreateNewMethodView.CreateNewMethod: Type is null");
			Assert.Fail("Type is null");
			return;
		}
		string name = NameInput;
		getAllParameters(out List<Parameter> parameters);

		var method = clazz.CreateNewMethod(name, type, parameters, Library.Loader);

		view.setMethodsNameTextBox.Text = "";
		view.setReturnTypeAutoCompleteBox.Text = "";
		onMethodCreated(method);
	}

	private void getAllParameters(out List<Parameter> parameters)
	{
		parameters = new List<Parameter>();
		foreach (var parameterCreatorView in Parameters!)
		{
			if (parameterCreatorView.Model.IsValid())
			{
				parameters.Add(new Parameter(parameterCreatorView.Model.NameInput,
					Library.AllTypes.First(type => type.Id == parameterCreatorView.Model.TypeInput)));
			}
		}
	}

	internal void RemoveTemplate(ParameterCreatorView paramView)
	{
		if (Parameters.Count > 1)
			Parameters.Remove(paramView);
		else
		{
			paramView.Model.reset();
		}
	}

	public bool IsValid()
	{
		return IsNameValid && IsTypeValid;
	}
}