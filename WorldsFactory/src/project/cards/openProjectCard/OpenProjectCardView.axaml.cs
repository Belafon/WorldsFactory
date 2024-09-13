using System.Runtime.CompilerServices;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System;
using System.Threading.Tasks;
using Avalonia.Interactivity;
using System.Linq;
using Serilog;
using Avalonia.Diagnostics.ViewModels;
using Avalonia.Controls.Notifications;
using Avalonia.Layout;
using Avalonia.Platform.Storage;
using WorldsFactory.screen;
using WorldsFactory.project;


namespace WorldsFactory.project.cards
{

	public partial class OpenProjectCardView : UserControl
	{
		private AllProjects allProjects { get; set; }
		private ProjectActions projectActions { get; set; }

		private OpenProjectCardViewViewModel model;

		public OpenProjectCardView()
		{
			throw new NotImplementedException();
		}
		
		public OpenProjectCardView(AllProjects allProjects, ProjectActions projectActions)
		{
			InitializeComponent();

			DataContext = model = new OpenProjectCardViewViewModel(allProjects, projectActions);
			
			this.allProjects = allProjects;
			this.projectActions = projectActions;

			allProjects.AddAllProjectAction += OnAddAllProject;
			allProjects.RemoveAllProjectAction += OnRemoveAllProject;
			allProjects.AddRecentProjectAction += OnAddRecentProject;
			allProjects.RemoveRecentProjectAction += OnRemoveRecentProject;
		}

		private void OnAddAllProject(ProjectReference reference)
		{
			model.AllProjects.Add(reference);

		}

		private void OnRemoveAllProject(ProjectReference reference)
		{
			model.AllProjects.Remove(reference);
		}

		private void OnAddRecentProject(ProjectReference reference)
		{
			model.RecentProjects.Add(reference);
		}

		private void OnRemoveRecentProject(ProjectReference reference)
		{
			model.RecentProjects.Remove(reference);
		}

		private void InitializeComponent()
		{
			AvaloniaXamlLoader.Load(this);
		}

		public void OnCreateNewProjectButton_Clicked(object sender, RoutedEventArgs e)
		{
			projectActions.ShowCreateNewProjectCardView(Windows.ViewCards[this]);
		}

		public async void OnFindExistingProjectButton_Clicked(object sender, RoutedEventArgs e)
		{

			string? projectPath = await findProjectInExplorer();
			if (projectPath != null)
			{
				if (!allProjects.ValidateProjectStructure(projectPath, out string error))
				{
					if (TopLevel.GetTopLevel(this) is MainWindow window)
					{
						window.Notifications.ShowErrorNotification("invalid_project_structure_title", "invalid_project_structure");
					}
				}
				else
				{
					var project = allProjects.AddNewProjectFromDirectory(projectPath);
					projectActions.OpenProject(project, TopLevel.GetTopLevel(this)!);
				}

			}
		}

		public void OnOpenProjectButton_Clicked(ProjectReference projectReference)
		{
			projectActions.OpenProject(projectReference, TopLevel.GetTopLevel(this)!);
		}

		/// <summary>
		/// Opens new window with file explorer
		/// </summary>
		/// <returns></returns>
		private async Task<string?> findProjectInExplorer()
		{
			//var file = await DoOpenFilePickerAsync(userControl);
			return await openFileExplorerToFindProjectFile();
		}

		private List<FilePickerFileType>? GetProjectFileTypes()
		{
			return new List<FilePickerFileType>
				{
					new("Project file")
					{
							Patterns = new[] { "project.json" },
							MimeTypes = new[] { "project.json" },
							AppleUniformTypeIdentifiers = new []{ "project.json" }
					}
				};
		}

		private async Task<string?> openFileExplorerToFindProjectFile()
		{
			// Get top level from the current control. Alternatively, you can use Window reference instead.
			var topLevel = TopLevel.GetTopLevel(this)!;

			// Start async operation to open the dialog.
			var files = await topLevel.StorageProvider.OpenFilePickerAsync(new FilePickerOpenOptions
			{
				Title = "Find a file project.json",
				FileTypeFilter = GetProjectFileTypes(),
				AllowMultiple = false
			});

			if (files.Count >= 1)
			{
				if (files[0] is IStorageFile)
					return (await files[0].GetParentAsync())?.TryGetLocalPath();
			}
			return null;
		}
	}

	internal class OpenProjectCardViewViewModel
	{
		public ObservableCollection<ProjectReference> AllProjects { get; private set; }

		public ObservableCollection<ProjectReference> RecentProjects { get; private set; }

		public OpenProjectCardViewViewModel(AllProjects allProjects, ProjectActions projectActions)
		{
			if (allProjects.GetAllKnownProjects() != null)
				AllProjects = new ObservableCollection<ProjectReference>(allProjects.GetAllKnownProjects());
			else AllProjects = new ObservableCollection<ProjectReference>();

			if (allProjects.GetRecentlyOpenedProjects() != null)
				RecentProjects = new ObservableCollection<ProjectReference>(allProjects.GetRecentlyOpenedProjects());
			else RecentProjects = new ObservableCollection<ProjectReference>();
		}
	}
}
