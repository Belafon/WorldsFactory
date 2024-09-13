using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Globalization;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using NP.Utilities;
using WorldsFactory.screen;

namespace WorldsFactory.world.library.classStructure;
public partial class ChangePropertiesView : UserControl
{
	public ChangePropertiesView()
	{
		throw new NotImplementedException();
	}
	public ChangePropertiesView(ILibrary library, IClass clazz)
	{
		InitializeComponent();
		Button createPropertyButton = this.FindControl<Button>("CreateNewPropertyButton")!;
		DataContext = new ChangePropertiesViewModel(clazz, library, createPropertyButton);
	}
}

public class ChangePropertiesViewModel : ViewModelBase
{
	public ObservableCollection<ChangePropertyView> Properties { get; set; } = new ObservableCollection<ChangePropertyView>();
	public ILibrary Library { get; init; }
	private IClass clazz { get; init; }

	private Button createPropertyButton { get; init; }
	public ChangePropertiesViewModel(
		IClass clazz,
		ILibrary library,
		Button createPropertyButton
		)
	{
		Library = library;
		this.clazz = clazz;
		this.createPropertyButton = createPropertyButton;
		Properties.AddAll(clazz.Properties.Select(p => new ChangePropertyView(library, clazz, p)));

		// bind Properties collection, so it will updates whenever library.properties changes
		clazz.Properties.CollectionChanged += (sender, args) =>
		{
			if (args.Action == NotifyCollectionChangedAction.Add)
			{
				Property property = (Property)args.NewItems![0]!;
				var itemsToRemove = new List<ChangePropertyView>();

				foreach (var changePropertyView in Properties.ToList())
				{
					if (changePropertyView.Model.NameInput.Equals(property.FullName))
					{
						itemsToRemove.Add(changePropertyView);
					}
				}

				foreach (var itemToRemove in itemsToRemove)
				{
					Properties.Remove(itemToRemove);
				}

				Properties.Add(new ChangePropertyView(library, clazz, property));
				createPropertyButton.IsEnabled = true;
			}
			else if (args.Action == NotifyCollectionChangedAction.Remove)
			{
				Property property = (Property)args.OldItems![0]!;
				clazz.Properties.Remove(property);
				//Properties.Remove(Properties.First(p => p.model.Clazz.Properties.Contains(property)));
			}
		};
	}

	public void DeleteProperty(ChangePropertyView propertyView)
	{
		if (propertyView.Model.Property is not null)
			clazz.Properties.Remove(propertyView.Model.Property);
		else
			Properties.Remove(propertyView);
	}

	public void CreateProperty_Click()
	{
		createPropertyButton.IsEnabled = false;
		ChangePropertyView propertyView = new ChangePropertyView(Library, clazz);
		Properties.Add(propertyView);
	}
}