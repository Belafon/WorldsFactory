using WorldsFactory.world.ids;

namespace WorldsFactory.world.library.classStructure;

public interface IClassLoader
{
	public IIDConceptManager IdClassesManager { get; init; }
	public IIDConceptManager IdManager { get; init; }
	public ITagManager TagManager { get; init; }
	public void SaveToFile(Class clazz);
	public void Delete(ILibrary library);
	public Task Rename(string newName);
}