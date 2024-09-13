using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Serilog;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.methods;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.world.objects;

namespace WorldsFactory.world.library.classStructure;
public class Method : ConceptWithID, IMethod
{
	public static readonly string ID_PREFIX = "@method:";
	private string name;

	/// <summary>
	/// Changes name and Id 
	/// Setter can throw <see cref="ConceptWithIDAlreadyExistsException"/> 
	/// </summary> 
	public string Name
	{
		private set
		{
			name = value;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Name"));
		}
		get => name;
	}

	public void Rename(string newName, string classIdPrefix)
	{
		var newId = ID_PREFIX + classIdPrefix + "_" + newName;
		if (newId != Id)
		{
			Id = newId;
			Name = newName;
		}
	}

	private WFType returnType;
	public WFType ReturnType
	{
		set
		{
			if (returnType.Equals(value))
				return;
			returnType = value;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("ReturnType"));
		}
		get => returnType;
	}

	public ObservableCollection<Parameter> Parameters { set; get; }

	[JsonIgnore]
	public IMethodsBody? Body { set; get; }
	[JsonIgnore]
	public List<Action> OnDelete { get; init; } = new List<Action>();

	public string classesPostfixId { get; set; }

	public Method(ITagManager tagManager, IIDConceptManager idManager, ILibraryLoader loader, IClass clazz, string name, WFType returnType, List<Parameter> parameters) : base(
		ID_PREFIX + clazz.GetPostfixId() + "_" + name,
		new ObservableCollection<string>(new string[] { "method", "Method" }),
		idManager,
		tagManager
		)
	{

		this.name = name;
		this.returnType = returnType;
		this.classesPostfixId = clazz.GetPostfixId();

		Parameters = new ObservableCollection<Parameter>();

		if (clazz is not WFType) // TODO: is not Static Method
		{
			Parameters.Add(new Parameter("self", clazz.GetReference()));
		}

		foreach (var parameter in parameters)
			Parameters.Add(parameter);

		PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "ReturnType"
				|| args.PropertyName == "Name")
				clazz.SaveToFile();
		};

		Parameters.CollectionChanged += (sender, args) =>
		{
			clazz.SaveToFile();
		};

		if (loader.MethodsFolder is null)
		{
			Log.Error("Method.Method: loader.MethodsFolder is null");
			Assert.Fail("loader.MethodsFolder is null");
			return;
		}

		var methodsBodyLoader = new MethodsBodyLoader(loader.MethodsFolder, this, idManager);
		createNewMethodsBody(methodsBodyLoader, Parameters.ToList());
	}

	private async void createNewMethodsBody(MethodsBodyLoader methodsBodyLoader, List<Parameter> parameters)
	{
		Body = await methodsBodyLoader.CreateNewMethodsBody(parameters);
	}

	internal Method(ITagManager tagManager, IIDConceptManager idManager, ILibraryLoader loader, string name, WFType returnType, List<Parameter> parameters, string classesPostfixId) : base(
		"@method:" + classesPostfixId + "_" + name,
		new ObservableCollection<string>(new string[] { "method", "Method" }),
		idManager,
		tagManager
		)
	{
		this.name = name;
		this.returnType = returnType;
		this.classesPostfixId = classesPostfixId;
		Parameters = new ObservableCollection<Parameter>(parameters);
		var methodsBodyLoader = new MethodsBodyLoader(loader.MethodsFolder!, this, idManager);
		Body = new MethodsBody(methodsBodyLoader);
	}

	internal void setOnParametersChanged(IClass clazz)
	{
		PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "ReturnType"
				|| args.PropertyName == "Name")
			{
				clazz.SaveToFile();
			}
		};

		Parameters.CollectionChanged += (sender, args) =>
		{
			clazz.SaveToFile();
		};
	}

	public void Delete(IIDConceptManager idManager, ITagManager tagManager)
	{
		int delteActionsCount = OnDelete.Count;
		for (int i = 0; i < delteActionsCount; i++)
		{
			OnDelete[0]();
		}

		Body?.Delete();
		idManager.DeleteConcept(this);
		tagManager.DeleteObject(this);
	}

	public new event PropertyChangedEventHandler? PropertyChanged;
}

public class MethodConverter : JsonConverter<Method>
{
	private IIDConceptManager idManager;
	private ITagManager tagManager;
	private ILibraryLoader loader;
	private string classesPostfixId;
	public MethodConverter(IIDConceptManager idManger, ITagManager tagManager, ILibraryLoader loader, string classesPostfixId)
	{
		this.idManager = idManger;
		this.tagManager = tagManager;
		this.loader = loader;
		this.classesPostfixId = classesPostfixId;
	}
	public override void WriteJson(JsonWriter writer, Method? value, JsonSerializer serializer)
	{
		throw new NotImplementedException();
	}

	public override Method? ReadJson(JsonReader reader, Type objectType, Method? existingValue, bool hasExistingValue,
		JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
			return null;

		JObject jsonObject = JObject.Load(reader);
		string name = jsonObject.Value<string>("Name")!;
		WFType returnType = jsonObject["ReturnType"]!.ToObject<WFType>(serializer)!;
		List<Parameter> parameters = jsonObject["Parameters"]!.ToObject<List<Parameter>>(serializer)!;

		try
		{
			return new Method(tagManager, idManager, loader, name, returnType, parameters, classesPostfixId);
		}
		catch (ArgumentException)
		{
			Assert.Fail("Method with name " + name + " cannot be loaded");
			Log.Error("MethodConverter.ReadJson: Method with name " + name + " cannot be loaded");
			return null;
		}
	}

}