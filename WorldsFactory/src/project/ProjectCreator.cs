using System.Reflection.PortableExecutable;
using System.IO.Enumeration;
using System.Security.AccessControl;
using System.Threading;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Diagnostics;
using System.IO;
using Avalonia.Controls;
using Avalonia.Controls.Notifications;
using Avalonia.Platform.Storage;
using Avalonia.Platform.Storage.FileIO;
using Newtonsoft.Json;
using WorldsFactory.screen;
using WorldsFactory.screen.panelCards;
using WorldsFactory.boot;
using WorldsFactory.world;

namespace WorldsFactory.project;

public class ProjectCreator
{
	/// <summary>
	/// Creates a new project and opens it, if the creation was successful.
	/// </summary>
	/// <param name="param">All necessary parameters for the project creation.</param>
	/// <param name="topLevel">Top level window, usually <see cref="MainWindow"/>.</param>
	/// <param name="openProjectAction">Action that opens the project.
	/// <returns></returns>
	public async Task CreateProject(Parameters param, TopLevel topLevel, Action<string> openProjectAction)
	{

		IStorageFolder? projectsRoot = await createProjectFile(param, topLevel);
		if (projectsRoot is null)
			return;

		bool error = await createWorldFile(param, topLevel, projectsRoot);
		if (error)
		{
			await projectsRoot.DeleteAsync();
			return;
		}

		ProjectFile projectFile = ProjectFile.CreateNewProjectFileObject(projectsRoot, param.ProjectName);

		string? projectsRootPath = StorageProviderExtensions.TryGetLocalPath(projectsRoot);

		Debug.Assert(projectsRootPath is not null);

		openProjectAction(projectsRootPath);


	}
	private async Task<IStorageFolder?> createProjectFile(Parameters param, TopLevel topLevel)
	{
		Uri projectsLocationUri = new Uri(param.SelectedProjectPath);

		// Check if the location of the projects folder is valid
		IStorageFolder? projectsLocation = await topLevel.StorageProvider.TryGetFolderFromPathAsync(projectsLocationUri);
		if (projectsLocation is null)
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("invalid_project_structure_title", "invalid_project_structure", NotificationType.Error);
			}
			return null;
		}

		// Check if the project folder already exists
		var uri = new Uri(Path.Combine(projectsLocationUri.AbsolutePath, param.ProjectName));
		IStorageFolder? projectsRootFolder = await topLevel.StorageProvider.TryGetFolderFromPathAsync(uri);
		if (projectsRootFolder is not null)
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("folder_with_same_name_title", "folder_with_same_name", NotificationType.Error);
			}
			return null;
		}

		// Create the project folder
		var projectsRoot = await projectsLocation.CreateFolderAsync(param.ProjectName);
		if (projectsRoot is null)
		{
			// Error in creation of the folder, rights of the user maybe?
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("error_in_folder_creation_title", "error_in_folder_creation", NotificationType.Error);
			}
			return null;
		}

		// Create the project file
		var fileCreationSuccess = await projectsRoot.CreateFileAsync(ProjectFile.Name);
		if (fileCreationSuccess is null)
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("error_in_project_file_creation_title", "error_in_project_file_creation", NotificationType.Error);
			}
			return null;
		}

		return projectsRoot;
	}
	private async Task<bool> createWorldFile(Parameters param, TopLevel topLevel, IStorageFolder projectsRoot)
	{

		// Create the World folder
		IStorageFolder? worldFolder = null;
		worldFolder = await projectsRoot.CreateFolderAsync(ProjectFile.WorldsFolderName);

		if (worldFolder is null)
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("error_in_world_folder_creation_title", "error_in_world_folder_creation", NotificationType.Error);
			}
			return true;
		}


		if (param.IsNewProject)
		{
			// Create the World file
			var worldFile = await worldFolder.CreateFileAsync(ProjectFile.WorldsFileName);
			if (worldFile is null)
			{
				if (topLevel is MainWindow window)
				{
					window.Notifications.ShowNotification("error_in_world_file_creation_title", "error_in_world_file_creation", NotificationType.Error);
				}
				return true;
			}

			// Create the world object
			DateTime todayDayTime = DateTime.Today;
			WorldFileData? world = new WorldFileData(param.WorldName!, todayDayTime);

			// save it in the file
			await using var stream = await worldFile.OpenWriteAsync();
			await using var writer = new StreamWriter(stream);
			await writer.WriteLineAsync(JsonConvert.SerializeObject(world!, Formatting.Indented));
			writer.Close();
			stream.Close();
			WorldCreator.Create(worldFolder, topLevel.StorageProvider);
		}
		else
		{
			// Copy the world folder
			FilesHandlers.CopyDirectory(param.SelectedWorldPath!, worldFolder!.TryGetLocalPath()!, true);

			// Check the structure
			Uri pathToWorld = new Uri(Path.Combine(param.SelectedWorldPath!, ProjectFile.WorldsFileName));
			IStorageFile? existingWorldFile = await topLevel.StorageProvider.TryGetFileFromPathAsync(pathToWorld);
			string? content = null;

			if (existingWorldFile is not null)
			{
				await using var streamData = await existingWorldFile.OpenReadAsync();
				using var reader = new System.IO.StreamReader(streamData);
				content = reader.ReadToEnd();
				reader.Close();
				streamData.Close();

			}

			if (content is null)
			{
				if (topLevel is MainWindow window)
				{
					window.Notifications.ShowNotification("error_existing_world_file_not_found_title", "error_existing_world_file_not_found", NotificationType.Error);
				}
				return true;
			}

			WorldFileData? world = JsonConvert.DeserializeObject<WorldFileData>(content!);

			if (world is null)
			{
				if (topLevel is MainWindow window)
				{
					window.Notifications.ShowNotification("error_existing_world_file_has_invalid_structure_title", "error_existing_world_file_has_invalid_structure", NotificationType.Error);
				}
				return true;
			}
		}
		return false;
	}
	
	/// <summary>
	/// Class that holds all necessary parameters for the project creation.
	/// The class can be built by the <see cref="Builder"/> class.
	/// </summary>
	public class Parameters
	{
		public string ProjectName { get; private set; }
		public string SelectedProjectPath { get; private set; }
		public string? WorldName { get; private set; }
		public string? SelectedWorldPath { get; private set; }
		public bool IsNewProject { get; private set; }

		private Parameters(
			string projectName,
			string selectedProjectPath,
			string worldName,
			string selectedWorldPath,
			bool isNewProject)
		{
			ProjectName = projectName ?? throw new ArgumentNullException(nameof(projectName));
			SelectedProjectPath = selectedProjectPath ?? throw new ArgumentNullException(nameof(selectedProjectPath));
			WorldName = worldName ?? throw new ArgumentNullException(nameof(worldName));
			SelectedWorldPath = selectedWorldPath;
			IsNewProject = isNewProject;
		}
		public class Builder
		{
			private string? projectName;
			private string? selectedProjectPath;
			private string? worldName;
			private string? selectedWorldPath;
			private bool isNewProject;

			public Builder WithProject(string name, string path)
			{
				projectName = name;
				selectedProjectPath = path;
				return this;
			}

			public Builder WithWorld(string name, string path)
			{
				worldName = name;
				selectedWorldPath = path;
				return this;
			}

			public Builder IsNewProject(bool isNewProject)
			{
				this.isNewProject = isNewProject;
				return this;
			}

			public Parameters Build()
			{
				if ((string.IsNullOrEmpty(projectName)
					|| string.IsNullOrEmpty(selectedProjectPath)
					|| string.IsNullOrEmpty(worldName))
					&&
					!isNewProject && string.IsNullOrEmpty(selectedWorldPath))
				{
					throw new InvalidOperationException("Not all required values are set.");
				}

				return new Parameters(
					projectName!,
					selectedProjectPath!,
					worldName!,
					selectedWorldPath!,
					isNewProject);
			}
		}
	}
}
