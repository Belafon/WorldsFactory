using Avalonia.Platform.Storage;

namespace WorldsFactory.works;


/// <summary>
/// Manages loading and saving of <see cref="IWork"/> objects
/// into the projects subfolder "Works"
/// </summary>
public interface IWorkLoader
{
	/// <summary>
	/// Tries to load a work from the storage with the given name.
	/// </summary>
	/// <param name="name"></param>
	/// <returns></returns>
	public Task<IWork?> Load(string name);
	/// <summary>
	/// Tries to load all works from the storage folder.
	/// </summary>
	/// <returns></returns>
	public Task<List<IWork>> LoadAll();
	public void Save(IWork work);
	public void Delete(IWork work);
	public Task<IStorageFolder?> GetWorkFolder(string name);
}
