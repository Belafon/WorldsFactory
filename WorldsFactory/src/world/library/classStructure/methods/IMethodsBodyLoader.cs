using WorldsFactory.world.ids;

namespace WorldsFactory.world.library.classStructure;

public interface IMethodsBodyLoader
{
	public Task<string?> Load();
	public Task<IMethodsBody> CreateNewMethodsBody(List<Parameter> parameters);
	public void Rename(string newName, string newId);
	public void Save(string code);
    void DeleteMethodsBodyFile();
	public IIDConceptManager IdManager { get; }

}