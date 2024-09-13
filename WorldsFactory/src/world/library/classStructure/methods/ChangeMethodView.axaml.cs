using System.Collections.ObjectModel;
using System.Reflection.Metadata.Ecma335;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using DynamicData.Binding;
using NP.Utilities;
using Serilog;
using WorldsFactory.screen;

namespace WorldsFactory.world.library.classStructure;
public partial class ChangeMethodView : UserControl
{
	internal IClass Clazz;
	internal ChangeMethodViewModel Model;
	internal TextBox NameTextBox;
	public AutoCompleteBox TypeAutoCompleteBox;
	public ChangeMethodView()
	{
		throw new NotImplementedException();
	}
	public ChangeMethodView(ILibrary library, IClass clazz, IMethod method)
	{
		InitializeComponent();
		this.Clazz = clazz;
		NameTextBox = this.FindControl<TextBox>("SetMethodsNameTextBox")!;
		TypeAutoCompleteBox = this.FindControl<AutoCompleteBox>("SetReturnTypeAutoCompleteBox")!;
		DataContext = Model = new ChangeMethodViewModel(this, method, library);

		method.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "Name")
			{
				NameTextBox.Text = method.Name;
			}
			else if (args.PropertyName == "ReturnType")
			{
				TypeAutoCompleteBox.Text = method.ReturnType.Id;
			}
		};

		DockPanel textEditorWithPythonContainer = this.FindControl<DockPanel>("TextEditorWithPython")!;
		var textEditor = new TextEditorWithPythonView(method, 76)
		{
			OnTextUpdated = (s) =>
			{
				method.Body!.Code = s;
			}
		};
		textEditorWithPythonContainer.Children.Add(textEditor);
	}
}

public class ChangeMethodViewModel : ViewModelBase
{
	private ChangeMethodView view;
	public IMethod Method { get; }
	public ObservableCollection<ParameterCreatorView> Parameters { get; init; } = new ObservableCollection<ParameterCreatorView>();
	public ILibrary Library { get; }
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
				view.SetReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				view.SetReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
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
				view.SetMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Green);
			else
				view.SetMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
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
				view.SetReturnTypeAutoCompleteBox.SelectedItem = Library.AllTypes.First(type => type.Id == value);
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

			if (view.Clazz.Methods.Any(method => method.Name.Equals(value))
				|| value.IsNullOrEmpty() || value.Equals(""))
				IsNameValid = false;
			else IsNameValid = true;

			nameInput = value;
			this.RaiseAndSetIfChanged(ref nameInput, value);
		}
	}
	public ChangeMethodViewModel(ChangeMethodView view, IMethod method, ILibrary library)
	{
		Library = library;
		this.view = view;
		Method = method;
		foreach (Parameter parameter in method.Parameters)
		{
			var mutability = ParameterMutability.Mutatable;
			
			if(parameter.Name == "self")
				mutability = ParameterMutability.Immutable;

			Parameters.Add(new ParameterCreatorView.Builder(Library, mutability)
			.IsContainedByParameters(Parameters)
			.WithParameter(parameter)
			.WithOnParamNotValid(onParamNotValid)
			.WithOnParamIsValid(onParamIsValid)
			.Build());
		}

		// add empty one for adding new
		Parameters.Add(new ParameterCreatorView.Builder(Library)
			.IsContainedByParameters(Parameters)
			.WithOnParamNotValid(onParamNotValid)
			.WithOnParamIsValid(onParamIsValid)
			.Build());

		method.Parameters.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				// if Parameters contains already an item with same name, remove it

				foreach (Parameter newParameter in args.NewItems)
				{
					bool isRemovedLastOne = false;
					if (Parameters.Any(x => x.Model.NameInput.Equals(newParameter.Name)))
					{
						var paramWithSameName = Parameters.First(x => x.Model.NameInput == newParameter.Name);
						isRemovedLastOne = paramWithSameName == Parameters.Last();
						Parameters.Remove(paramWithSameName);
					}

					var lastParameter = Parameters.Last();
					if(!isRemovedLastOne)
						Parameters.Remove(lastParameter);
						
					Parameters.Add(new ParameterCreatorView.Builder(Library)
						.IsContainedByParameters(Parameters)
						.WithParameter(newParameter)
						.WithOnParamNotValid(onParamNotValid)
						.WithOnParamIsValid(onParamIsValid)
						.Build());
					
					if(!isRemovedLastOne)
						Parameters.Add(lastParameter);
				}
			}

			if (args.OldItems != null)
			{
				foreach (Parameter parameter in args.OldItems)
				{
					if(Parameters.Any(x => x.Model.NameInput == parameter.Name))
						Parameters.Remove(Parameters.First(x => x.Model.NameInput == parameter.Name));
				}
			}
		};

		method.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "Name")
			{
				NameInput = method.Name;
			}
			else if (args.PropertyName == "ReturnType")
			{
				ReturnTypeInput = method.ReturnType.Id;
			}
		};

		view.NameTextBox.LostFocus += (sender, args) =>
			updateMethodsNameOrType(view, method);

		view.TypeAutoCompleteBox.LostFocus += (sender, args) =>
			updateMethodsNameOrType(view, method);

		ReturnTypeInput = method.ReturnType.Id;
		NameInput = method.Name;
	}

	private void updateMethodsNameOrType(ChangeMethodView view, IMethod method)
	{
		if (method.Name.Equals(view.NameTextBox.Text)
			&& method.ReturnType.Id.Equals(view.TypeAutoCompleteBox.Text))
			return;

		if (!isNameValid)
			view.SetMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
		else view.SetMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Green);

		if (!isTypeValid)
			view.SetReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Red);
		else view.SetReturnTypeAutoCompleteBox.BorderBrush = new SolidColorBrush(Colors.Green);

		if (!IsValid())
			return;


		if (view.Clazz.Methods.Any(method => method.Name.Equals(view.NameTextBox.Text)))
		{
			view.SetMethodsNameTextBox.BorderBrush = new SolidColorBrush(Colors.Red);
			return;
		}

		if (view.Clazz.Methods.Any(method => method.Name.Equals(view.NameTextBox.Text)))
		{
			IsNameValid = false;
			return;
		}

		Method.Rename(view.NameTextBox.Text!, view.Clazz.GetPostfixId());

		var retType = Library.AllTypes.First(type => type.Id == view.TypeAutoCompleteBox.Text);
		if (retType is null)
		{
			Log.Error("ChangeMethodView-updateMethodsNameOrType, type not found");
			Assert.Fail("ChangeMethodView-updateMethodsNameOrType, type not found");
			return;
		}


		Method.ReturnType = retType;
	}

	private void onParamIsValid(ParameterCreatorView paramView)
	{
		if(paramView.Parameter is null)
		{
			Assert.Fail("ChangeMethodView-onParamIsValid, parameter is null");
			return;
		}

		Method.Parameters.Add(paramView.Parameter);
	}

	private void onParamNotValid(ParameterCreatorView paramView)
	{
		if (paramView.Parameter is null)
		{
			Assert.Fail("ChangeMethodView-onParamNotValid, param is null");
			return;
		}
		Parameters.Remove(paramView);
		Method.Parameters.Remove(paramView.Parameter);
	}

	public bool IsValid()
	{
		return IsNameValid && IsTypeValid;
	}
}