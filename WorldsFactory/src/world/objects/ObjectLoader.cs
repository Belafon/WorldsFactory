using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using NP.Utilities;
using Serilog;
using WorldsFactory.src.screen;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.objects;
public class ObjectLoader : IObjectLoader
{
	private IStorageFolder folder;
	private IIDConceptManager idManager;
	public IIDConceptManager IdManager { get => idManager; }
	private const string JSON_FILE_NAME_POSTFIX = ".json";

	private ITagManager tagManager;
	public ObjectLoader(IStorageFolder folder, IIDConceptManager idManager, ITagManager tagManager)
	{
		this.folder = folder;
		this.idManager = idManager;
		this.tagManager = tagManager;
	}

	public WFObject CreateNewObject(string name, WFType type)
	{
		var newObject = new WFObject(name, type, idManager, tagManager, this, null);

		Save(newObject);
		return newObject;
	}

	public async void Save(WFObject obj)
	{
		var file = await findFile(obj.Id);
		if (file is null)
			file = await folder.CreateFileAsync(obj.Id);

		if (file is null)
			throw new CannotCreateFolderException("ObjectLoader: Could not create file " + obj.Id);

		await using var stream = await file.OpenWriteAsync();
		using var streamWriter = new StreamWriter(stream);


		var data = JsonConvert.SerializeObject(obj, Formatting.Indented, new JsonSerializerSettings
		{
			Converters = {
				new ClassReferenceConverter(idManager),
				new WFTypeConverter(idManager),
				new ObjectReferenceConverter(idManager),
			}
		});

		await streamWriter.WriteAsync(data);
		streamWriter.Close();
		stream.Close();
	}

	/// <summary>
	/// Loads all objects in the directory and in its subdirectories
	/// </summary>
	/// <returns></returns>
	/// <exception cref="ArgumentException"></exception> <summary>
	/// </summary>
	/// <returns></returns>
	public async Task<ObservableCollection<WFObject>> Load()
	{
		return new ObservableCollection<WFObject>(await Load(folder));
	}

	private async Task<HashSet<WFObject>> Load(IStorageFolder folder)
	{
		var objects = new HashSet<WFObject>();
		await foreach (var item in folder.GetItemsAsync())
		{
			if (item is IStorageFolder subFolder)
			{
				objects.AddAll(await Load(subFolder));
			}
			else if (item is IStorageFile file)
			{
				if (file.Name.StartsWith("@method:"))
					continue;

				var obj = await LoadObject(file);
				if (obj is not null)
					objects.Add(obj);
			}
		}

		return objects;
	}

	private async Task<WFObject?> LoadObject(IStorageFile file)
	{
		await using var stream = await file.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		string allContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();

		var methodLoader = new InitMethodLibraryLoader
		{
			MethodsFolder = folder
		};

		try
		{
			var obj = JsonConvert.DeserializeObject<WFObject>(allContent, new JsonSerializerSettings
			{
				Converters = {
					new WFObjectConverter(idManager, tagManager, this, methodLoader),
					new WFTypeConverter(idManager),
					new ClassReferenceConverter(idManager),
					new ObjectReferenceConverter(idManager),
				}
			});
			
			if(obj is null)
			{
				Log.Error("ObjectLoader: Could not deserialize an object from file {0}", file.Name);
			}
			
			return obj;
		}
		catch (System.Exception)
		{
			Log.Error("Could not deserialize object {0}", file.Name);
			return null;
		}
	}

	public async Task DeleteObject(WFObject wFObject)
	{
		await deleteObject(wFObject.Id);

		// remove init method
		var initMethod = wFObject.InitMethod;
		if (initMethod is not null)
			initMethod.Delete(idManager, tagManager);
	}
	
	public async Task deleteObject(string id)
	{
		var file = await findFile(id);
		if (file is not null)
			await file.DeleteAsync();
		else
			Log.Warning("ObjectLoader: Could not find file " + id);	
	}
	private async Task<IStorageFile?> findFile(string objectsId)
	{
		await foreach (var item in folder.GetItemsAsync())
		{
			if (item is IStorageFile file
				&& file.Name == objectsId)
				return file;
		}

		return null;
	}


	public async void Rename(string oldId, WFObject obj, IMethod initMethod)
	{
		var file = await findFile(oldId);
		if (file is null)
			throw new ArgumentException("ObjectLoader: Could not find file " + obj.Id);

		await deleteObject(oldId);
		renameInitMethod(obj.Name, initMethod);
		Save(obj);
	}


	public IMethod CreateInitMethod(WFObject wFObject)
	{
		Method? initMethod = null;
		var fileName = wFObject.Id;
		var objName = wFObject.Name;

		initMethod = createNewInitMethod(fileName, objName, wFObject);

		if (initMethod is null)
			throw new ArgumentException("ObjectLoader: Could not create init method for " + wFObject.Id);

		return initMethod;
	}
	private Method createNewInitMethod(string fileName, string objName, WFObject wFObject)
	{
		var methodLoader = new InitMethodLibraryLoader
		{
			MethodsFolder = folder
		};
		var emptyClass = new EmptyClass()
		{
			Id = WFObject.ID_PREFIX
		};
		var parameters = new List<Parameter>();
		var objectsTypeRef = wFObject.Type;
		
		if(objectsTypeRef is not Reference<IClass> refClass)
			throw new ArgumentException("ObjectLoader: objectsTypeRef is not a reference to a class for " + wFObject.Id);
		else {
			var clazzRefClone = refClass.Clone();
			var selfParameter = new Parameter(wFObject.Name, clazzRefClone);
			parameters.Add(selfParameter);	
		}


		var initMethodName = objName + "_initialize";
		var initMethod = new Method(tagManager, idManager, methodLoader, emptyClass, initMethodName, BasicType.Void, parameters);

		return initMethod;
	}



	private void renameInitMethod(string newName, IMethod initMethod)
	{
		initMethod.Rename(newName + "_initialize", WFObject.ID_PREFIX);
		initMethod.Parameters[0].Name = newName;
	}

	private class InitMethodLibraryLoader : ILibraryLoader
	{
		public IStorageFolder? MethodsFolder { get; set; }
	}

	private class EmptyClass : IClass, WFType
	{
		public EmptyClass()
		{
			OnDelete += (sender, args) => {};
			OnParentChanged += (sender, args) => {};
			PropertyChanged += (sender, args) => {};
			OnIdChanged += (sender, args) => {};
			OnDelete.Invoke(this, EventArgs.Empty);
			OnParentChanged.Invoke(this, EventArgs.Empty);
			PropertyChanged.Invoke(this, new PropertyChangedEventArgs(""));
			OnIdChanged.Invoke(this, new PropertyChangedEventArgs(""));
		}
		public string Description { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
		public Reference<IClass>? Parent { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
		public FullyObservableCollection<Reference<IClass>> Children { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
		public ObservableCollection<Property> Properties { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
		public ObservableCollection<IMethod> Methods { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
		public string Id { get; set; } = null!;

		public string PostfixId => throw new NotImplementedException();

		public string Name => throw new NotImplementedException();

		string WFType.GetPostfixId => throw new NotImplementedException();

		public event EventHandler? OnDelete;
		public event EventHandler? OnParentChanged;
		public event PropertyChangedEventHandler? PropertyChanged;
		public event PropertyChangedEventHandler? OnIdChanged;

		public IMethod CreateNewMethod(string name, WFType type, List<Parameter> parameters, ILibraryLoader library)
		{
			throw new NotImplementedException();
		}

		public Property CreateNewProperty(string name, WFType type, ObservableCollection<string> tags)
		{
			throw new NotImplementedException();
		}

		public void Delete(ILibrary? library)
		{
			throw new NotImplementedException();
		}

		public string GetPostfixId()
		{
			return Id;
		}

		public Reference<IClass> GetReference()
		{
			throw new NotImplementedException();
		}

		public ObservableCollection<string> GetTags()
		{
			throw new NotImplementedException();
		}

		public void Rename(string newName)
		{
			throw new NotImplementedException();
		}

		public void SaveToFile()
		{
		}
	}
}
