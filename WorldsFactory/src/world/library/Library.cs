using System.Collections.ObjectModel;
using WorldsFactory.screen;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure;
using Avalonia.Platform.Storage;
using Serilog;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library;
public class Library : ViewModelBase, ILibrary
{
	public string NameOfPartOfWorld { get; init; } = "Library";
	public IIDConceptManager IdManager { get; init; }
	public ITagManager TagManager { get; init; }
	
	public ObservableCollection<IClass> Classes { get; init; } = new();
	public ObservableCollection<WFType> AllTypes { get; init; } = new();
	public ILibraryLoader Loader { get; init; }
	public Library(IDConceptManager iDConceptManager, ITagManager tagManager, ILibraryLoader loader)
	{
		Loader = loader;
		TagManager = tagManager;
		IdManager = new IDClassesManager(iDConceptManager);
		Classes.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach(Class clazz in args.NewItems.OfType<Class>())
				{
					var reference = clazz.GetReference();
					AllTypes.Add(reference);
					setIdUpdateOnChange(clazz, reference);
				}
			}
			else if (args.OldItems is not null)
			{
				foreach(IClass clazz in args.OldItems.OfType<IClass>())
				{
					WFType? type = null;
					if(AllTypes.Any(t => t.Id.Equals(clazz.Id)))
						type = AllTypes.First(t => t.Id.Equals(clazz.Id));
					
					if(type is not null)
					{
						AllTypes.Remove(type);
					}
				}
			}
		};
		
		foreach(var type in BasicType.AllTypes.Values)
		{
			AllTypes.Add(type);
		}
	}

	private void setIdUpdateOnChange(Class clazz, Reference<IClass> reference)
	{
		clazz.OnDelete += (sender, args) =>
		{
			AllTypes.Remove(reference);
		};
	}

	public IClass? CreateClass(string prefixId, string postfixId, IStorageFolder classesFolder, bool withInitMethod = true)
	{
		IStorageFile? classFile = classesFolder.CreateFileAsync(postfixId + ".json").Result;

		if (classFile is null)
		{
			Log.Error("Couldn't create class file {0}", postfixId);
			return null;
		}
		if(IdManager is IDClassesManager idClassesManager)
		{
			var classLoader = new ClassLoader(classFile, idClassesManager, TagManager);
			IClass newClass = new Class(postfixId, classLoader);
				
			Classes.Add(newClass);
			newClass.SaveToFile();
			
			if(withInitMethod)
				newClass.Methods.Add(
					new Method(classLoader.TagManager, classLoader.IdManager, Loader, newClass, "initialize", BasicType.Void, new List<Parameter>())
				);
			return newClass;
		}  
		Assert.Fail("IdManager is not IDClassesManager");
		return null;
	}

	public void DeleteClass(string id)
	{
		throw new NotImplementedException();
	}

	public IClass? GetClassById(string id)
	{
		throw new NotImplementedException();
	}

	public void MergeLibrary(ILibrary library)
	{
		throw new NotImplementedException();
	}
}