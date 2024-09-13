using Avalonia.Platform.Storage;
using Newtonsoft.Json;

namespace WorldsFactory.works;

/// <summary>
/// Manages loading and saving of <see cref="IWork"/> objects
/// into the projects subfolder "Works"
/// </summary>
public class WorkLoader : IWorkLoader
{
	private IStorageFolder worksFolder;
	public WorkLoader(IStorageFolder worksFolder)
	{
		this.worksFolder = worksFolder;
	}
	public Task<IWork?> Load(string name)
	{
		return findFile(name).ContinueWith((file) => loadFromFile(file.Result)).Unwrap();
	}
	private async Task<IWork?> loadFromFile(IStorageFile? file)
	{
		if (file is null)
		{
			throw new FileNotFoundException();
		}
		await using var stream = await file.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		var fileContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();

		IWork? work = JsonConvert.DeserializeObject<Work>(fileContent, new JsonSerializerSettings
		{
			Converters = { }
		});
		return work;
	}
	public void Save(IWork work)
	{
		findFile(work.Name).ContinueWith(async file =>
		{
			if (file.Result is null)
			{
				throw new FileNotFoundException();
			}
			await using var stream = await file.Result.OpenWriteAsync();
			using var streamWriter = new StreamWriter(stream);
			await streamWriter.WriteAsync(JsonConvert.SerializeObject(work));
			streamWriter.Close();
			stream.Close();
		});
	}
	public void Delete(IWork work)
	{
		findFile(work.Name).ContinueWith(async file =>
		{
			if (file.Result is null)
				return;

			await file.Result.DeleteAsync();
		});
	}
	private async Task<IStorageFile?> findFile(string name)
	{
		IStorageFile? findingFile = null;
		await foreach (var item in worksFolder.GetItemsAsync())
		{
			if (item is IStorageFile file &&
				file.Name == name)
			{
				findingFile = file;
				break;
			}
		}
		return findingFile;
	}
	public async Task<IStorageFolder?> GetWorkFolder(string name){
		return await findStorageItem(name).ContinueWith(task => {
			if(task.Result is IStorageFolder folder)
				return folder;
			else
				return null;
		});
	}
	private async Task<IStorageItem?> findStorageItem(string name)
	{
		IStorageItem? findingItem = null;
		await foreach (var item in worksFolder.GetItemsAsync())
		{
			if (item.Name == name)
			{
				findingItem = item;
				break;
			}
		}
		return findingItem;
	}
	public async Task<List<IWork>> LoadAll()
	{
		List<IWork> works = new();
		await foreach (var item in worksFolder.GetItemsAsync())
		{
			if (item is IStorageFile file)
			{
				await loadFromFile(file).ContinueWith(task =>
				{
					if (task.Result is not null)
						works.Add(task.Result);
				});
			}
		}
		return works;
	}
}
