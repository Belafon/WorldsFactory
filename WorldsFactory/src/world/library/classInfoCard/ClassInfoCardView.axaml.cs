using System.Collections.ObjectModel;
using System.Configuration.Assemblies;
using Avalonia.Controls;
using Avalonia.Controls.Presenters;
using Avalonia.Input;
using Avalonia.VisualTree;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.screen.panelCards.cards;
using WorldsFactory.src.screen;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library;
public partial class ClassInfoCardView : UserControl
{
	public IClass Clazz { get; init; }
	public ClassInfoCardView()
	{
		throw new NotImplementedException();
	}
	public ClassInfoCardView(ILibrary library, IClass clazz)
	{
		InitializeComponent();
		Clazz = clazz;
		var content = this.FindControl<DockPanel>("contentClassInfo")!;
		DataContext = new ClassInfoCardViewModel(library, clazz, content);
		TreeView treeView = this.FindControl<TreeView>("ClassInfoTreeView")!;
	}

}

public class ClassInfoCardViewModel : ViewModelBase
{
	public ObservableCollection<ClassTypeTreeViewItem> ClassTypeTreeViewItems { get; init; }

	public ObservableCollection<object> SelectedItems { get; init; }
		= new ObservableCollection<object>();

	public ClassInfoCardViewModel(ILibrary library, IClass clazz, DockPanel contentControl)
	{
		ClassTypeTreeViewItems = new ObservableCollection<ClassTypeTreeViewItem>
		{
			new ParentTreeViewItem(clazz),
			new ChildrenTreeViewItem(clazz),
			new PropertiesTreeViewItem(clazz),
			new MethodsTreeViewItem(clazz)
		};
		SelectedItems.CollectionChanged += (sender, args) =>
		{
			if (SelectedItems.Count == 0 || sender is null)
			{
				return;
			}

			OnSelectionChanged(contentControl, clazz, library);
		};
	}

	public void OnSelectionChanged(DockPanel contentControl, IClass clazz, ILibrary library)
	{
		switch (SelectedItems[0])
		{
			case ParentTreeViewItem _:
				displayViewInContentSpace(contentControl, new ChangeParentView(library, clazz));
				break;
			case PropertiesTreeViewItem _:
				displayViewInContentSpace(contentControl, new ChangePropertiesView(library, clazz));
				break;
			case PropertyTreeViewItem propertyTreeViewItem:
				var propertyView = new ChangePropertyView(library, clazz, propertyTreeViewItem.Property);
				displayViewInContentSpace(contentControl, propertyView);
				Action? delteProperty = null;
				delteProperty = () =>
				{
					if (contentControl.Children.Contains(propertyView))
						displayViewInContentSpace(contentControl, new EmptyCardView());

					if (delteProperty is not null
							&& propertyTreeViewItem.Property.OnDelete.Contains(delteProperty))
						propertyTreeViewItem.Property.OnDelete.Remove(delteProperty);
				};
				propertyTreeViewItem.Property.OnDelete.Add(delteProperty);
				break;
			case MethodsTreeViewItem methods:
				Action<IMethod> OnMethodCreated = (method) =>
				{
					var changeMethodView = new ChangeMethodView(library, clazz, method);
					displayViewInContentSpace(contentControl, changeMethodView);
					Action? methodDelete = null;
					methodDelete = () =>
					{
						if (contentControl.Children.Contains(changeMethodView))
							displayViewInContentSpace(contentControl, new EmptyCardView());
					};
					changeMethodView.DetachedFromVisualTree += (sender, args) =>
					{
						if (method.OnDelete.Contains(methodDelete))
							method.OnDelete.Remove(methodDelete);
					};
					method.OnDelete.Add(methodDelete);
				};
				var createNewMethod = new CreateNewMethodView(library, clazz, OnMethodCreated);

				displayViewInContentSpace(contentControl, createNewMethod);
				break;
			case MethodTreeViewItem methodTreeViewItem:
				var changeMethodView = new ChangeMethodView(library, clazz, methodTreeViewItem.Method);
				displayViewInContentSpace(contentControl, changeMethodView);
				Action? methodDelete = null;
				methodDelete = () =>
				{
					if (contentControl.Children.Contains(changeMethodView))
						displayViewInContentSpace(contentControl, new EmptyCardView());
				};

				changeMethodView.DetachedFromVisualTree += (sender, args) =>
				{
					if (methodTreeViewItem.Method.OnDelete.Contains(methodDelete))
						methodTreeViewItem.Method.OnDelete.Remove(methodDelete);
				};

				methodTreeViewItem.Method.OnDelete.Add(methodDelete);
				break;
			default:
				Log.Error("Unknown type of selected item");
				break;
		}
	}

	private void displayViewInContentSpace(DockPanel contentControl, UserControl view)
	{
		contentControl.Children.Clear();
		contentControl.Children.Add(view);
	}
}

public abstract class ClassTypeTreeViewItem : ViewModelBase
{
	public IClass Clazz { get; set; }
	public ClassTypeTreeViewItem(IClass clazz)
	{
		this.Clazz = clazz;
	}
	public abstract string Name { get; set; }
}


public class ParentTreeViewItem : ClassTypeTreeViewItem
{
	private const string PARENT_TAG_NAME = "Parent";
	private string name = PARENT_TAG_NAME;
	public override string Name
	{
		get => name;
		set {
			Log.Information("ParentTreeViewItem.Name: {value}", value);
			this.RaiseAndSetIfChanged(ref name, value);
		}
	}
	public ParentTreeViewItem(IClass clazz) : base(clazz)
	{
		if (Clazz.Parent is not null)
		{
			Name = "Parent - " + Clazz.Parent.Name;
			// Triggered, also when parent's id changed
			Clazz.Parent.OnIdChanged += (sender, args) =>
			{
				Name = "Parent - " + Clazz.Parent.Name;
			};
		}

		// Triggered, when parent is changed to another
		Clazz.OnParentChanged += (sender, args) =>
		{
			if (Clazz.Parent is null)
				Name = PARENT_TAG_NAME;
			else
			{
				// update checking if name of parent is changed
				Clazz.Parent!.OnIdChanged += (sender, args) =>
				{
					Name = "Parent - " + Clazz.Parent.Name;
				};
				Name = "Parent - " + Clazz.Parent.Name;
			}
		};
	}
}


public class ChildrenTreeViewItem : ClassTypeTreeViewItem
{
	public override string Name { get; set; } = "Children";
	public ChildrenTreeViewItem(IClass clazz) : base(clazz)
	{
	}
}



internal class PropertiesTreeViewItem : ClassTypeTreeViewItem
{
	public override string Name { get; set; } = "Properties";
	public ObservableCollection<PropertyTreeViewItem> Properties { get; set; } = new ObservableCollection<PropertyTreeViewItem>();
	public PropertiesTreeViewItem(IClass clazz) : base(clazz)
	{
		foreach (var property in clazz.Properties)
		{
			Properties.Add(new PropertyTreeViewItem(property, Clazz));
		}
		clazz.Properties.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (var property in args.NewItems)
				{
					if (property is not Property)
						continue;
					Properties.Add(new PropertyTreeViewItem((Property)property, Clazz));
				}
			}
			else if (args.OldItems is not null)
			{
				foreach (var property in args.OldItems)
				{
					if (property is not Property)
						continue;
					Properties.Remove(Properties.First(x => x.Property == property));
					clazz.Properties.Remove((Property)property);
				}
			}
		};
	}

}


internal class MethodsTreeViewItem : ClassTypeTreeViewItem
{
	public override string Name { get; set; } = "Methods";
	public ObservableCollection<MethodTreeViewItem> Methods { get; set; } = new ObservableCollection<MethodTreeViewItem>();
	public MethodsTreeViewItem(IClass clazz) : base(clazz)
	{
		foreach (var method in clazz.Methods)
		{
			Methods.Add(new MethodTreeViewItem(method, Clazz));
		}
		clazz.Methods.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (var method in args.NewItems)
				{
					if (method is not IMethod)
						continue;
					Methods.Add(new MethodTreeViewItem((IMethod)method, Clazz));
				}
			}
			else if (args.OldItems is not null)
			{
				foreach (var method in args.OldItems)
				{
					if (method is not IMethod)
						continue;
					Methods.Remove(Methods.First(x => x.Method == method));
					clazz.Methods.Remove((IMethod)method);
				}
			}
		};
	}
}

internal class MethodTreeViewItem
{
	public IMethod Method { get; set; }
	public IClass clazz;
	public MethodTreeViewItem(IMethod method, IClass clazz)
	{
		Method = method;
		this.clazz = clazz;
	}
	public void DeleteMethod()
	{
		clazz.Methods.Remove(Method);
	}
}

internal class PropertyTreeViewItem
{
	public Property Property { get; set; }
	public IClass clazz;
	public PropertyTreeViewItem(Property property, IClass clazz)
	{
		Property = property;
		this.clazz = clazz;
	}
	public void DeleteProperty()
	{
		clazz.Properties.Remove(Property);
	}
}
