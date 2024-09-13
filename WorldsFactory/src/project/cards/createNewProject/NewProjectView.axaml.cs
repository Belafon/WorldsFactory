using System.Security.AccessControl;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using Avalonia.Interactivity;
using Avalonia.Platform.Storage;
using Avalonia.Controls.Notifications;
using Avalonia.Layout;
using System;
using System.Threading.Tasks;
using System.IO;
using System.Collections.Generic;
using WorldsFactory.screen;
using Newtonsoft.Json;

namespace WorldsFactory.project;

public partial class NewProjectCardView : UserControl
{
	// Define event handlers and other members here

	private ProjectActions projectActions;
	private AllProjects allProjects;
	public NewProjectCardView(AllProjects allProjects, ProjectActions projectActions)
	{
		InitializeComponent();

		this.projectActions = projectActions;
		this.allProjects = allProjects;

		// Attach event handlers to your buttons here
		Button selectPathButton = this.FindControl<Button>("SelectPathButton")!;
		selectPathButton.Click += SelectPathToExistingProjectButton_Click!;

		Button createButton = this.FindControl<Button>("CreateButton")!;
		createButton.Click += CreateButton_Click!;

		Button loadExistingWorldButton = this.FindControl<Button>("SelectPathWorldButton")!;
		loadExistingWorldButton.Click += SelectPathOfExistingWorld_Click!;
	}

	public NewProjectCardView()
	{
		throw new NotImplementedException();
	}

	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}

	private async void SelectPathToExistingProjectButton_Click(object sender, RoutedEventArgs e)
	{
		Uri? path = await selectFolder(this);

		if (path is not null)
		{
			this.FindControl<TextBox>("PathInput")!.Text = path.AbsolutePath!;
		}
	}

	private async void SelectPathOfExistingWorld_Click(object sender, RoutedEventArgs e)
	{
		IStorageFile? file = await selectWorldsFile(this);

		if (file is null)
			return;

		// get name of the world
		await using var stream = await file.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		// Reads all the content of file as a text.
		string allContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();

		WorldFileData? world = JsonConvert.DeserializeObject<WorldFileData>(allContent);
		if (world is not null)
			this.FindControl<TextBlock>("ExistingWorldName")!.Text = world.Name;
		else
		{
			if (TopLevel.GetTopLevel(this) is MainWindow window)
			{
				window.Notifications.ShowNotification("invalid_world_structure_title", "invalid_world_structure", NotificationType.Error);
			}
			return;
		}
		string? path = (await file.GetParentAsync())?.TryGetLocalPath();
		if (path is not null)
		{
			this.FindControl<TextBox>("PathInputToWorld")!.Text = path!;
		}

	}

	/// <summary>
	/// creates new project with new world or existing world
	/// </summary>
	private void CreateButton_Click(object sender, RoutedEventArgs e)
	{

		string? projectName = this.FindControl<TextBox>("ProjectName")!.Text;
		string? selectedProjectPath = this.FindControl<TextBox>("PathInput")!.Text;
		string? newWorldName = this.FindControl<TextBox>("NewWorldName")!.Text;
		string? existingWorldName = this.FindControl<TextBlock>("ExistingWorldName")!.Text;
		string? selectedWorldPath = this.FindControl<TextBox>("PathInputToWorld")!.Text;

		var topLevel = TopLevel.GetTopLevel(this)!;

		if (projectName is null
			|| selectedProjectPath is null
			|| selectedProjectPath == ""
			|| newWorldName == "")
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("empty_name_or_path_title", "empty_name_or_path", NotificationType.Error);
			}
			return;
		}
		if((newWorldName is null || projectName == "") && (existingWorldName is null || existingWorldName == ""))
		{
			if (topLevel is MainWindow window)
			{
				window.Notifications.ShowNotification("no_world_selected_title", "no_world_selected", NotificationType.Error);
			}
			return;
		}
		// check if the project should create a new world, or load an existing one
		TabControl tabControl = this.FindControl<TabControl>("SelectWorldTabControl")!;
		bool isCreateNewWorld = tabControl.SelectedIndex == 0;

		if (!isCreateNewWorld)
		{
			if (selectedWorldPath is null || existingWorldName is null)
			{
				if (topLevel is MainWindow window)
				{
					window.Notifications.ShowNotification("selected_world_empty_path_title", "selected_world_empty_path", NotificationType.Error);
				}
				return;
			}
		}
		else
		{
			if (newWorldName is null)
			{
				if (topLevel is MainWindow window)
				{
					window.Notifications.ShowNotification("empty_new_world_name_title", "empty_new_world_name", NotificationType.Error);
				}
				return;
			}
		}

		// TODO possible extension, go through all projects and their worlds and create a selector for the user

		projectActions.CreateProject(new ProjectCreator.Parameters.Builder()
			.WithProject(projectName, selectedProjectPath)
			.WithWorld(isCreateNewWorld ? newWorldName! : existingWorldName!, selectedWorldPath!)
			.IsNewProject(isCreateNewWorld)
			.Build(), topLevel);
	}


	private async Task<Uri?> selectFolder(UserControl userControl)
	{
		// Get top level from the current control. Alternatively, you can use Window reference instead.
		var topLevel = TopLevel.GetTopLevel(userControl)!;

		// Start async operation to open the dialog.
		var folders = await topLevel.StorageProvider.OpenFolderPickerAsync(new FolderPickerOpenOptions
		{
			Title = "Select folder",
			AllowMultiple = false
		});


		if (folders.Count >= 1)
		{

			var folder = folders[0];
			return folder.Path;
		}
		return null;
	}

	private async Task<IStorageFile?> selectWorldsFile(UserControl userControl)
	{
		var topLevel = TopLevel.GetTopLevel(userControl)!;

		// Start async operation to open the dialog.
		var files = await topLevel.StorageProvider.OpenFilePickerAsync(new FilePickerOpenOptions
		{
			Title = "Find a file world.json",
			FileTypeFilter = GetWorldFileTypes(),
			AllowMultiple = false
		});

		if (files.Count >= 1)
		{

			if (files[0] is IStorageFile)
				return files[0];
		}
		return null;
	}

	private List<FilePickerFileType>? GetWorldFileTypes()
	{
		return new List<FilePickerFileType>
				{
					new("World file")
					{
							Patterns = new[] { "world.json" },
							MimeTypes = new[] { "world.json" },
							AppleUniformTypeIdentifiers = new []{ "world.json" }
					}
				};
	}
}
