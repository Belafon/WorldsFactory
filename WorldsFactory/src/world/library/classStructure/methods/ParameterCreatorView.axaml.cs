using System.Collections.ObjectModel;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using NP.Utilities;
using WorldsFactory.screen;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;

public partial class ParameterCreatorView : UserControl
{
	public AutoCompleteBox typeOfParameterAutoCompleteBox;
	public TextBox nameOfParameterTextBox;
	public ParameterCreatorViewModel Model { get; }
	public Parameter? Parameter { get; set; }
	public ParameterMutability Mutability { get; set; }
	
	public ParameterCreatorView()
	{
		throw new NotImplementedException();
	}
	private ParameterCreatorView(
		ILibrary library, 
		ObservableCollection<ParameterCreatorView> parameters, 
		Parameter? parameter = null, 
		Action<ParameterCreatorView>? onParamNotValid = null, 
		Action<ParameterCreatorView>? onParamIsValid = null, 
		ParameterMutability mutability = default)
	{
		InitializeComponent();
		this.Mutability = mutability;
		Parameter = parameter;
		typeOfParameterAutoCompleteBox = this.FindControl<AutoCompleteBox>("TypeOfParameterAutoCompleteBox")!;
		nameOfParameterTextBox = this.FindControl<TextBox>("NameOfParameterTextBox")!;
		DataContext = Model = new ParameterCreatorViewModel(library, this, parameters, onParamNotValid, onParamIsValid);
		if (parameter != null)
		{
			typeOfParameterAutoCompleteBox.Text = parameter.Type.Id;
			nameOfParameterTextBox.Text = parameter.Name;
		}

		if (mutability == ParameterMutability.Immutable)
		{
			typeOfParameterAutoCompleteBox.IsEnabled = false;
			nameOfParameterTextBox.IsEnabled = false;
		}
	}



}

public class ParameterCreatorViewModel : ViewModelBase
{
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
				paramView.nameOfParameterTextBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				paramView.nameOfParameterTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
		}
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
				paramView.typeOfParameterAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				paramView.typeOfParameterAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);

		}
	}


	private string typeInput = "";
	public string TypeInput
	{
		get => typeInput;
		set
		{
			if (typeInput.Equals(value))
				return;
			typeInput = value;


			if (Library.AllTypes.Any(type => type.Id == value))
			{
				IsTypeValid = true;
				paramView.typeOfParameterAutoCompleteBox.SelectedItem = Library.AllTypes.First(type => type.Id == value);
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

			if (parameters.Any(parameter => parameter.Model.NameInput.Equals(value))
				|| value.IsNullOrEmpty() || value.Equals(""))
				IsNameValid = false;
			else IsNameValid = true;

			nameInput = value;
			this.RaiseAndSetIfChanged(ref nameInput, value);
		}
	}


	public ILibrary Library { get; }
	private ParameterCreatorView paramView;
	private ObservableCollection<ParameterCreatorView> parameters { get; }
	private Action<ParameterCreatorView>? onParamNotValid;
	private Action<ParameterCreatorView>? onParamIsValid;
	public ParameterCreatorViewModel(
		ILibrary library,
		ParameterCreatorView paramView,
		ObservableCollection<ParameterCreatorView> parameters,
		Action<ParameterCreatorView>? onParamNotValid,
		Action<ParameterCreatorView>? onParamIsValid)
	{
		this.onParamNotValid = onParamNotValid;
		this.onParamIsValid = onParamIsValid;
		this.parameters = parameters;
		this.Library = library;
		this.paramView = paramView;

		if (paramView.Parameter is not null)
		{
			isNameValid = true;
			isTypeValid = true;
		}
		else
		{
			isNameValid = false;
			isTypeValid = false;
		}

		paramView.typeOfParameterAutoCompleteBox.PointerEntered += (sender, e) =>
		{
			if (IsTypeValid)
				return;
			paramView.typeOfParameterAutoCompleteBox.IsDropDownOpen = true;
		};

		paramView.LostFocus += (sender, e) => checkValidityAndCreateNewTemplate();
	}
	private void checkValidityAndCreateNewTemplate()
	{

		if (!IsValid())
		{
			if (paramView.Parameter is not null)
			{
				// if the param is not valid until 10 milisec, 
				// then the parameter will be removeed
				Task.Delay(10).ContinueWith(async _ =>
				{
					await Avalonia.Threading.Dispatcher.UIThread.InvokeAsync(() =>
					{
						if (!IsValid())
						{
							onParamNotValid?.Invoke(paramView);
							paramView.Parameter = null;							
						}
					});
				});
			}
			return;
		}

		// create new or update
		var type = Library.AllTypes.First(type => type.Id == paramView.Model.TypeInput);
		if (type is null)
		{
			Assert.Fail("ChangeMethodView-onParamIsValid, type not found");
			return;
		}

		if (paramView.Parameter is not null)
		{
			if (paramView.Parameter.Type.Id == type.Id
				&& paramView.Parameter.Name == paramView.Model.NameInput)
				return;

			// update parameter
			paramView.Parameter.Name = paramView.Model.NameInput;
			paramView.Parameter.Type = type;
		}
		else
		{
			// create new parameter
			var param = new Parameter(paramView.Model.NameInput, type);
			paramView.Parameter = param;

			// create new paramterCreatorView
			parameters.Add(new ParameterCreatorView.Builder(Library)
				.IsContainedByParameters(parameters)
				.WithOnParamIsValid(onParamIsValid)
				.WithOnParamNotValid(onParamNotValid)
				.Build());
			
			onParamIsValid?.Invoke(paramView);
		}
	}


	internal void reset()
	{
		paramView.Model.NameInput = "";
		paramView.Model.TypeInput = "";
		paramView.Parameter = null;
	}

	public bool IsValid()
	{
		return IsNameValid && IsTypeValid;
	}
}


public partial class ParameterCreatorView
{
	public class Builder
	{
		private ILibrary _library;
		private ObservableCollection<ParameterCreatorView> _parameters = new ObservableCollection<ParameterCreatorView>();
		private Parameter? _parameter;
		private Action<ParameterCreatorView>? _onParamNotValid;
		private Action<ParameterCreatorView>? _onParamIsValid;
		private ParameterMutability mutability;

		public Builder(ILibrary library, ParameterMutability mutability = ParameterMutability.Mutatable)
		{
			_library = library;
			this.mutability = mutability;
		}

		public Builder IsContainedByParameters(ObservableCollection<ParameterCreatorView> parameters)
		{
			_parameters = parameters;
			return this;
		}

		public Builder WithParameter(Parameter? parameter)
		{
			_parameter = parameter;
			return this;
		}

		public Builder WithOnParamNotValid(Action<ParameterCreatorView>? onParamNotValid)
		{
			_onParamNotValid = onParamNotValid;
			return this;
		}

		public Builder WithOnParamIsValid(Action<ParameterCreatorView>? onParamIsValid)
		{
			_onParamIsValid = onParamIsValid;
			return this;
		}

		public ParameterCreatorView Build()
		{
			if (_library == null)
				throw new InvalidOperationException("Library is required.");

			return new ParameterCreatorView(_library, _parameters, _parameter, _onParamNotValid, _onParamIsValid, mutability);
		}
	}


}
public enum ParameterMutability
{
	Mutatable,
	Immutable
}