using System.Collections.ObjectModel;
using System.Collections.Specialized;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Markup.Xaml.MarkupExtensions;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using NP.Utilities;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.src.screen;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;

public class Class : ConceptWithID, IClass
{
	public const string ID_PREFIX = "@class:";
	public event EventHandler? OnDelete;
	private IClassLoader loader;
	private string postFixId = "";

	[JsonProperty("postfixId")]
	public new string PostfixId
	{
		get => postFixId;
		private set
		{
			if (postFixId != null && postFixId != value)
			{
				var oldPostFixId = postFixId;
				postFixId = value;
				try
				{
					Id = ID_PREFIX + value;
				}
				catch (ConceptWithIDAlreadyExistsException e)
				{
					postFixId = oldPostFixId;
					throw e;
				}

				foreach (var property in Properties)
					property.Rename(property.Name, value);

				foreach (var method in Methods)
					method.Rename(method.Name, value);

				RaisePropertyChanged("PostfixId");

				loader.Rename(value)
				.ContinueWith((task) =>
				{
					SaveToFile();
				});
			}
			else
			{
				postFixId = value;
				Id = ID_PREFIX + value;
			}
		}
	}

	public string Name
	{
		get => PostfixId;
		set => PostfixId = value;
	}

	public Class(
		string id,
		IClassLoader loader)
		: base(
			ID_PREFIX + id,
			new ObservableCollection<string>(new string[] { "class", "Class" }),
			loader.IdClassesManager,
			loader.TagManager
		)
	{
		this.loader = loader;
		postFixId = id;
		setSaveOnPropertyChangedAction();
	}


	internal Class(
		string id,
		ObservableCollection<string> tags,
		IClassLoader loader,
		string description,
		Reference<IClass>? parent,
		FullyObservableCollection<Reference<IClass>> children,
		ObservableCollection<Property> properties,
		ObservableCollection<IMethod> methods
	) : base(ID_PREFIX + id, tags, loader.IdClassesManager, loader.TagManager)
	{
		Methods = methods;
		Children = children;
		this.loader = loader;
		postFixId = id;
		this.parent = parent;
		this.description = description;
		Properties = properties;
		setSaveOnPropertyChangedAction();
	}

	internal void setSaveOnPropertyChangedAction()
	{
		var saveClass = new NotifyCollectionChangedEventHandler((sender, args) => SaveToFile());
		OnParentChanged += (s, e) => SaveToFile();
		Children.CollectionChanged += saveClass;
		Properties.CollectionChanged += saveClass;
		Properties.CollectionChanged += (sender, args) =>
		{
			if (args.OldItems is not null)
			{
				foreach (var property in args.OldItems)
				{
					if (property is Property prop)
						prop.Delete(loader.IdManager, loader.TagManager);
				}
			}
			if (args.NewItems is not null)
			{
				foreach (var property in args.NewItems)
				{
					if (property is Property prop)
						prop.PropertyChanged += (sender, args) => SaveToFile();
				}
			}
		};

		Methods.CollectionChanged += saveClass;
		Methods.CollectionChanged += (sender, args) =>
		{
			if (args.OldItems is not null)
			{
				foreach (var m in args.OldItems)
				{
					if (m is IMethod method)
						method.Delete(loader.IdManager, loader.TagManager);
				}
			}
		};
		foreach (var property in Properties)
		{
			property.PropertyChanged += (sender, args) => SaveToFile();
			property.OnIdChanged += (sender, args) => SaveToFile();

			// if type has concept found already, add updating on id change
			if (property.Type is IReference typeRef)
				typeRef.OnIdChanged += (sender, args) => SaveToFile();

			// if type is changed, add updating on id change
			property.PropertyChanged += (sender, args) =>
			{
				if (args.PropertyName == "Type")
					if (property.Type is IReference typeRef)
						typeRef.OnIdChanged += (sender, args) => SaveToFile();
				if (args.PropertyName == "StringArrayBrackets")
					SaveToFile();
			};
		}

		if (Parent is not null)
			Parent.OnIdChanged += (sender, args) => SaveToFile();

		Children.ItemPropertyChanged += (sender, args) => SaveToFile();

	}

	[JsonIgnore]
	private string description = "";

	[JsonProperty("description")]
	public string Description
	{
		get => description;
		set
		{
			if (description != value)
			{
				description = value;
				SaveToFile();
			}
		}
	}

	[JsonIgnore]
	private Reference<IClass>? parent;

	public event EventHandler? OnParentChanged = (s, e) => { };

	[JsonProperty("parent")]
	public Reference<IClass>? Parent
	{
		get => parent;
		set
		{
			if (value == parent)
				return;

			// Remove this class from the old parent's children
			if (parent is not null
				&& parent.TryGetConcept() is IClass oldClass)
			{
				lock (oldClass.Children)
				{
					var listToRemove = oldClass.Children.Where(c => c.Id == Id);

					while (listToRemove.Any())
						oldClass.Children.Remove(listToRemove.First());
				}
			}

			parent = value;

			// if new value is null, nothing else to do
			// otherwise, add this class to the new parent's children
			if (value is not null)
			{
				var parentObject = value.TryGetConcept();
				if (parentObject is not null
					&& parentObject is IClass parentClass)
				{
					lock (parentClass.Children)
					{
						parentClass.Children.Add(GetReference());
					}
				}
			}

			if (OnParentChanged is not null)
				OnParentChanged(this, new EventArgs());


			SaveToFile();
		}
	}

	[JsonProperty("children")]
	public FullyObservableCollection<Reference<IClass>> Children { get; set; } =
		new FullyObservableCollection<Reference<IClass>>();

	[JsonProperty("properties")]
	public ObservableCollection<Property> Properties { get; set; } =
		new ObservableCollection<Property>();

	[JsonProperty("methods")]
	public ObservableCollection<IMethod> Methods { get; set; } =
		new ObservableCollection<IMethod>();

	public ObservableCollection<string> GetTags()
	{
		return Tags;
	}

	public string GetPostfixId()
	{
		if (PostfixId is null)
			Assert.Fail("PostfixId is null.");
		return PostfixId!;
	}

	public Reference<IClass> GetReference()
	{
		return new Reference<IClass>(this, loader.IdManager);
	}

	public override string ToString()
	{
		return Id;
	}

	public void SaveToFile()
	{
		loader.SaveToFile(this);
	}

	public IMethod CreateNewMethod(string name, WFType type, List<Parameter> parameters, ILibraryLoader loader)
	{
		var method = new Method(this.loader.TagManager, this.loader.IdManager, loader, this, name, type, parameters);
		Methods.Add(method);
		return method;
	}

	public void Delete(ILibrary? library)
	{
		if (Children.Count > 0)
			return; // TODO notify user, also check if an object of the type exists

		OnDelete?.Invoke(this, new EventArgs());

		foreach (var property in Properties)
			property.Delete(loader.IdManager, loader.TagManager);

		foreach (var method in Methods)
			method.Delete(loader.IdClassesManager, loader.TagManager);

		if (library is not null)
			loader.Delete(library);

		loader.IdClassesManager.DeleteConcept(this);
		loader.TagManager.DeleteObject(this);

		if (library is not null)
			library.Classes.Remove(this);
		
	}

	public Property CreateNewProperty(string name, WFType type, ObservableCollection<string> tags)
	{
		var match = Property.PropertyNameStructurePattern.Match(name);
		if (!match.Success)
			throw new ArgumentException("Property name is not valid");
		var nameDecl = match.Groups[1].Value;
		var arrayBrackets = match.Groups[2].Value;
		var property = new Property.Builder()
			.SetClassesPostfixId(PostfixId!)
			.SetName(nameDecl)
			.SetType(type)
			.SetTags(tags)
			.SetArrayBrackets(arrayBrackets)
			.SetIdManager(loader.IdManager)
			.SetTagManager(loader.TagManager)
			.Build();
		Properties.Add(property);
		return property;
	}

	public void Rename(string newName)
	{
		PostfixId = newName;
	}
}

public class ClassConverter : JsonConverter<Class>
{
	private readonly ITagManager tagManager;
	private readonly IDClassesManager idManager;
	private IStorageFile classFile;
	private readonly ILibraryLoader libraryLoader;

	public ClassConverter(ITagManager tagManager, IDClassesManager idManager, IStorageFile classFile, ILibraryLoader libraryLoader)
	{
		this.libraryLoader = libraryLoader;
		this.classFile = classFile;
		this.tagManager = tagManager ?? throw new ArgumentNullException(nameof(tagManager));
		this.idManager = idManager ?? throw new ArgumentNullException(nameof(idManager));
	}

	public override Class? ReadJson(JsonReader reader, Type objectType, Class? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
			return null;

		JObject jsonObject = JObject.Load(reader);
		string id = jsonObject.Value<string>("id")!;
		ObservableCollection<string> tags = jsonObject["tags"]!.ToObject<ObservableCollection<string>>()!;

		// Deserialize other properties as needed
		string description = jsonObject.Value<string>("description")!;
		var parent = jsonObject["parent"]!.ToObject<Reference<IClass>>(serializer)!;

		var children = jsonObject["children"]!.ToObject<FullyObservableCollection<Reference<IClass>>>(serializer)!;

		string postfixId = jsonObject["postfixId"]!.ToObject<string>(serializer)!;

		// Create a new Class instance using the constructor
		var classLoader = new ClassLoader(classFile, idManager, tagManager);

		// add new property converter
		serializer.Converters.Add(new PropertyConverter(classLoader.IdManager, tagManager, postfixId));
		var properties = jsonObject["properties"]!.ToObject<ObservableCollection<Property>>(serializer)!;

		int i = 0;
		while (i < properties.Count)
		{
			if (properties[i] is null)
				properties.RemoveAt(i);
			else
				i++;
		}
		serializer.Converters.Add(new MethodConverter(classLoader.IdManager, tagManager, libraryLoader, postfixId));
		var methods = jsonObject["methods"]!.ToObject<ObservableCollection<Method>>(serializer)!;
		var iMethods = new ObservableCollection<IMethod>(methods);


		if (postfixId is null
			|| tags is null
			|| classLoader is null
			|| description is null
			|| children is null
			|| properties is null
			|| iMethods is null)
			return null;

		Class newClass = new Class(postfixId, tags, classLoader, description, parent, children, properties, iMethods)
		{
			Methods = iMethods
		};
		foreach (var imethod in iMethods)
		{
			if (imethod is Method method)
				method.setOnParametersChanged(newClass);
		}

		return newClass;
	}

	public override void WriteJson(JsonWriter writer, Class? value, JsonSerializer serializer)
	{
		throw new NotImplementedException();
	}

	public override bool CanWrite => false; // Allow writing with the default action

}