using System;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using WorldsFactory.screen;

/// <summary>
/// Handles reference to a project. 
/// These references are stored in app resources
/// using <see cref="AllProjects"/> class.
/// </summary>
namespace WorldsFactory.project;
public record ProjectReference(string Name, string Path)
{
	public async Task<IStorageFolder?> GetProjectFolder(IStorageProvider storageProvider)
	{
		Uri uri = new Uri(Path);
		return await storageProvider.TryGetFolderFromPathAsync(uri);
	}

	public async Task<IStorageFolder?> GetWorksFolder(IStorageProvider storageProvider)
	{
		var projectFolderTask = GetProjectFolder(storageProvider);
		var projectFolder = await projectFolderTask;
		if (projectFolder is not null)
		{
			var item = await findItem(projectFolder, "Works");
			if (item is IStorageFolder folder)
				return folder;
			else
				return await projectFolder.CreateFolderAsync("Works");
		}
		else
			return null;
	}


	private async Task<IStorageItem?> findItem(IStorageFolder folder, string name)
	{
		IStorageItem? outItem = null;
		await foreach (var item in folder.GetItemsAsync())
		{
			if (item.Name == name)
				outItem = item;
		}
		return outItem;
	}

}