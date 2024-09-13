using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WorldsFactory.project;
using WorldsFactory.world.ids;
using Avalonia.Platform.Storage;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library;
public interface ILibrary : PartOfWorld
{
	public ObservableCollection<IClass> Classes { get; init; }
	public IIDConceptManager IdManager { get; init; }
	public ITagManager TagManager { get; init; }
	

	/// <summary>
	/// Creates a class with given id.
	/// </summary>
	/// <param name="id">Id</param>
	/// <returns>returns null if there is already something with same id.</returns>
	public IClass? CreateClass(string prefixId, string postfixId, IStorageFolder classesFolder, bool withInitMethod = true);
	public void DeleteClass(string id);
	public IClass? GetClassById(string id);
	public void MergeLibrary(ILibrary library);
	public ObservableCollection<WFType> AllTypes { get; init; }
	public ILibraryLoader Loader { get; init; }
	

}